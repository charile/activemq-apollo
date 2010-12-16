/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.apollo.broker

import java.util.concurrent.TimeUnit

import org.fusesource.hawtdispatch._
import _root_.org.fusesource.hawtdispatch.ScalaDispatchHelpers._
import java.util.concurrent.atomic.AtomicInteger

import collection.{SortedMap}
import org.apache.activemq.apollo.store.{StoreUOW}
import protocol.ProtocolFactory
import collection.mutable.ListBuffer
import org.apache.activemq.apollo.store._
import org.apache.activemq.apollo.util._
import org.apache.activemq.apollo.util.list._
import org.fusesource.hawtdispatch.{Dispatch, ListEventAggregator, DispatchQueue, BaseRetained}
import org.apache.activemq.apollo.dto.QueueDTO
import OptionSupport._
import security.SecurityContext

object Queue extends Log {
  val subcsription_counter = new AtomicInteger(0)

  val PREFTCH_LOAD_FLAG = 1.toByte
  val PREFTCH_HOLD_FLAG = 2.toByte
}

import Queue._

/**
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
class Queue(val host: VirtualHost, var id:Long, val binding:Binding, var config:QueueDTO) extends BaseRetained with Route with DeliveryConsumer with BaseService {

  var inbound_sessions = Set[DeliverySession]()
  var all_subscriptions = Map[DeliveryConsumer, Subscription]()

  val filter = binding.message_filter

  override val dispatchQueue: DispatchQueue = createQueue(binding.label);
  dispatchQueue.setTargetQueue(getRandomThreadQueue)
  dispatchQueue {
    debug("created queue for: " + binding.label)
  }
  setDisposer(^ {
    ack_source.release
    dispatchQueue.release
    session_manager.release
  })


  val ack_source = createSource(new ListEventAggregator[(Subscription#AcquiredQueueEntry, Boolean, StoreUOW)](), dispatchQueue)
  ack_source.setEventHandler(^ {drain_acks});
  ack_source.resume

  val session_manager = new SinkMux[Delivery](messages, dispatchQueue, Delivery)

  // sequence numbers.. used to track what's in the store.
  var message_seq_counter = 1L

  val entries = new LinkedNodeList[QueueEntry]()
  val head_entry = new QueueEntry(this, 0L).head
  var tail_entry = new QueueEntry(this, next_message_seq)
  entries.addFirst(head_entry)

  //
  // In-frequently accessed tuning configuration.
  //

  /**
   *  The amount of memory buffer space for receiving messages.
   */
  def tune_producer_buffer = config.producer_buffer.getOrElse(32*1024)

  /**
   *  The amount of memory buffer space for the queue..
   */
  def tune_queue_buffer = config.queue_buffer.getOrElse(32*1024)

  //
  // Frequently accessed tuning configuration.
  //

  /**
   * Should this queue persistently store it's entries?
   */
  var tune_persistent = true

  /**
   * Should messages be flushed or swapped out of memory if
   * no consumers need the message?
   */
  var tune_swap = true

  /**
   * The number max number of flushed queue entries to load
   * for the store at a time.  Not that Flushed entires are just
   * reference pointers to the actual messages.  When not loaded,
   * the batch is referenced as sequence range to conserve memory.
   */
  var tune_flush_range_size = 0

  /**
   *  The amount of memory buffer space to use per subscription.
   */
  var tune_consumer_buffer = 0

  def configure(c:QueueDTO) = {
    config = c
    tune_persistent = host.store !=null && config.persistent.getOrElse(true)
    tune_swap = tune_persistent && config.swap.getOrElse(true)
    tune_flush_range_size = config.flush_range_size.getOrElse(10000)
    tune_consumer_buffer = config.consumer_buffer.getOrElse(32*1024)
  }
  configure(config)

  var enqueue_item_counter = 0L
  var dequeue_item_counter = 0L
  var enqueue_size_counter = 0L
  var dequeue_size_counter = 0L
  var nack_item_counter = 0L
  var nack_size_counter = 0L

  def queue_size = enqueue_size_counter - dequeue_size_counter
  def queue_items = enqueue_item_counter - dequeue_item_counter

  var loading_size = 0
  var flushing_size = 0
  var flushed_items = 0

  var capacity = 0
  var capacity_used = 0

  val swap_source = createSource(EventAggregators.INTEGER_ADD, dispatchQueue)
  swap_source.setEventHandler(^{ swap_messages });
  swap_source.resume

  protected def _start(onCompleted: Runnable) = {

    capacity = tune_queue_buffer;

    def completed: Unit = {
      // by the time this is run, consumers and producers may have already joined.
      onCompleted.run
      schedual_consumer_sample
      // wake up the producers to fill us up...
      if (messages.refiller != null) {
        messages.refiller.run
      }

      // kick off dispatching to the consumers.
      trigger_swap
      dispatchQueue << head_entry
    }

    if( tune_persistent ) {

      if( id == -1 ) {
        id = host.queue_id_counter.incrementAndGet

        val record = new QueueRecord
        record.key = id
        record.binding_data = binding.binding_data
        record.binding_kind = binding.binding_kind

        host.store.addQueue(record) { rc =>
          dispatchQueue {
            completed
          }
        }

      } else {

        host.store.listQueueEntryRanges(id, tune_flush_range_size) { ranges=>
          dispatchQueue {
            if( ranges!=null && !ranges.isEmpty ) {

              ranges.foreach { range =>
                val entry = new QueueEntry(Queue.this, range.firstQueueSeq).init(range)
                entries.addLast(entry)

                message_seq_counter = range.lastQueueSeq + 1
                enqueue_item_counter += range.count
                enqueue_size_counter += range.size
                tail_entry = new QueueEntry(Queue.this, next_message_seq)
              }

              debug("restored: "+enqueue_item_counter)
            }
            completed
          }
        }
        
      }

    } else {
      if( id == -1 ) {
        id = host.queue_id_counter.incrementAndGet
      }
      completed
    }
  }

  protected def _stop(onCompleted: Runnable) = {
    // TODO: perhaps we should remove all the entries
    onCompleted.run
  }

  def addCapacity(amount:Int) = {
    val was_full = messages.full
    capacity += amount
    if( was_full && !messages.full ) {
      messages.refiller.run
    }
  }

  object messages extends Sink[Delivery] {

    var refiller: Runnable = null

    def full = (capacity_used >= capacity) || !serviceState.isStarted

    def offer(delivery: Delivery): Boolean = {
      if (full) {
        false
      } else {

        val entry = tail_entry
        tail_entry = new QueueEntry(Queue.this, next_message_seq)
        val queueDelivery = delivery.copy
        entry.init(queueDelivery)
        
        if( tune_persistent ) {
          queueDelivery.uow = delivery.uow
        }

        entries.addLast(entry)
        enqueue_item_counter += 1
        enqueue_size_counter += entry.size

        // Do we need to do a persistent enqueue???
        if (queueDelivery.uow != null) {
          entry.as_loaded.store
        }

        var dispatched = false
        if( entry.hasSubs ) {
          // try to dispatch it directly...
          entry.dispatch
        }

        val prev = entry.getPrevious

        if( (prev.as_loaded!=null && prev.as_loaded.flushing) || (prev.as_flushed!=null && !prev.as_flushed.loading) ) {
          entry.flush(!entry.as_loaded.acquired)
        } else {
          trigger_swap
        }

        // release the store batch...
        if (queueDelivery.uow != null) {
          queueDelivery.uow.release
          queueDelivery.uow = null
        }

        true
      }
    }
  }


  def display_stats: Unit = {
    info("contains: %d messages worth %,.2f MB of data, producers are %s, %d/%d buffer space used.", queue_items, (queue_size.toFloat / (1024 * 1024)), {if (messages.full) "being throttled" else "not being throttled"}, capacity_used, capacity)
    info("total messages enqueued %d, dequeues %d ", enqueue_item_counter, dequeue_item_counter)
  }

  def display_active_entries: Unit = {
    var cur = entries.getHead
    var total_items = 0L
    var total_size = 0L
    while (cur != null) {
      if (cur.is_loaded || cur.hasSubs || cur.is_prefetched || cur.is_flushed_range ) {
        info("  => " + cur)
      }

      total_size += cur.size
      if (cur.is_flushed || cur.is_loaded) {
        total_items += 1
      } else if (cur.is_flushed_range ) {
        total_items += cur.as_flushed_range.count
      }
      
      cur = cur.getNext
    }
    info("tail: " + tail_entry)

    // sanitiy checks..
    if(total_items != queue_items) {
      warn("queue_items mismatch, found %d, expected %d", total_size, queue_items)
    }
    if(total_size != queue_size) {
      warn("queue_size mismatch, found %d, expected %d", total_size, queue_size)

    }
  }

  def trigger_swap = {
    if( tune_swap ) {
      swap_source.merge(1)
    }
  }

  def swap_messages = {

    // reset the prefetch flags...
    var cur = entries.getHead
    while( cur!=null ) {
      cur.prefetch_flags = 0
      cur = cur.getNext
    }

    // Set the prefetch flags
    all_subscriptions.valuesIterator.foreach( _.refill_prefetch )

    // swap out messages.
    cur = entries.getHead
    while( cur!=null ) {
      val next = cur.getNext
      val loaded = cur.as_loaded
      if( loaded!=null ) {
        if( cur.prefetch_flags==0 && !loaded.acquired  ) {
          val flush_asap = !cur.as_loaded.acquired
          cur.flush(flush_asap)
        } else {
          cur.load // just in case it's getting flushed.
        }
      }
      cur = next
    }


    // Combine flushed items into flushed ranges
    if( flushed_items > tune_flush_range_size*2 ) {

      debug("Looking for flushed entries to combine")

      var distance_from_sub = tune_flush_range_size;
      var cur = entries.getHead
      var combine_counter = 0;

      while( cur!=null ) {

        // get the next now.. since cur may get combined and unlinked
        // from the entry list.
        val next = cur.getNext

        if( cur.prefetch_flags!=0 ) {
          distance_from_sub = 0
        } else {
          distance_from_sub += 1
          if( cur.can_combine_with_prev ) {
            cur.getPrevious.as_flushed_range.combineNext
            combine_counter += 1
          } else {
            if( cur.is_flushed && distance_from_sub > tune_flush_range_size ) {
              cur.flush_range
              combine_counter += 1
            }
          }

        }
        cur = next
      }
      debug("combined %d entries", combine_counter)
    }

  }

  def schedual_consumer_sample:Unit = {

    def slowConsumerCheck = {
      if( serviceState.isStarted ) {

        // target tune_min_subscription_rate / sec
        all_subscriptions.foreach{ case (consumer, sub)=>
          
          if ( sub.tail_parkings < 0 ) {

            // re-calc the avg_advanced_size
            sub.advanced_sizes += sub.advanced_size
            while( sub.advanced_sizes.size > 5 ) {
              sub.advanced_sizes = sub.advanced_sizes.drop(1)
            }
            sub.avg_advanced_size = sub.advanced_sizes.foldLeft(0)(_ + _) /  sub.advanced_sizes.size

          }
          
          sub.total_advanced_size += sub.advanced_size
          sub.advanced_size = 0
          sub.tail_parkings = 0

        }

        swap_messages
        schedual_consumer_sample
      }
    }

    dispatchQueue.dispatchAfter(1, TimeUnit.SECONDS, ^{
      slowConsumerCheck
    })
  }


  def drain_acks = {
    ack_source.getData.foreach {
      case (entry, consumed, tx) =>
        if( consumed ) {
          entry.ack(tx)
        } else {
          entry.nack
        }
    }
    messages.refiller.run
  }


  

  /////////////////////////////////////////////////////////////////////
  //
  // Implementation of the DeliveryConsumer trait.  Allows this queue
  // to receive messages from producers.
  //
  /////////////////////////////////////////////////////////////////////

  def matches(delivery: Delivery) = filter.matches(delivery.message)

  def is_persistent = tune_persistent

  def connect(p: DeliveryProducer) = new DeliverySession {
    retain

    override def consumer = Queue.this

    override def producer = p

    val session = session_manager.open(producer.dispatchQueue)

    dispatchQueue {
      inbound_sessions += this
      addCapacity( tune_producer_buffer )
    }


    def close = {
      session_manager.close(session)
      dispatchQueue {
        addCapacity( -tune_producer_buffer )
        inbound_sessions -= this
      }
      release
    }

    // Delegate all the flow control stuff to the session
    def full = session.full

    def offer(delivery: Delivery) = {
      if (session.full) {
        false
      } else {
        delivery.message.retain
        if( tune_persistent && delivery.uow!=null ) {
          delivery.uow.retain
        }
        val rc = session.offer(delivery)
        assert(rc, "session should accept since it was not full")
        true
      }
    }

    def refiller = session.refiller

    def refiller_=(value: Runnable) = {session.refiller = value}
  }

  /////////////////////////////////////////////////////////////////////
  //
  // Implementation of the Route trait.  Allows consumers to bind/unbind
  // from this queue so that it can send messages to them.
  //
  /////////////////////////////////////////////////////////////////////

  def connected() = {}

  def bind(value: DeliveryConsumer, security:SecurityContext): Result[Zilch, String] = {
    if(  host.authorizer!=null && security!=null ) {
      if( value.browser ) {
        if( !host.authorizer.can_receive_from(security, host, config) ) {
          return new Failure("Not authorized to browse the queue")
        }
      } else {
        if( !host.authorizer.can_consume_from(security, host, config) ) {
          return new Failure("Not authorized to consume from the queue")
        }
      }
    }
    bind(value::Nil)
    Success(Zilch)
  }

  def bind(values: List[DeliveryConsumer]) = retaining(values) {
    for (consumer <- values) {
      val subscription = new Subscription(this, consumer)
      all_subscriptions += consumer -> subscription
      addCapacity( tune_consumer_buffer )
    }
  } >>: dispatchQueue

  def unbind(values: List[DeliveryConsumer]) = releasing(values) {
    for (consumer <- values) {
      all_subscriptions.get(consumer) match {
        case Some(subscription) =>
          all_subscriptions -= consumer
          subscription.close
          addCapacity( -tune_consumer_buffer )
        case None =>
      }

    }
  } >>: dispatchQueue

  def disconnected() = throw new RuntimeException("unsupported")

  /////////////////////////////////////////////////////////////////////
  //
  // Implementation methods.
  //
  /////////////////////////////////////////////////////////////////////

  private def next_message_seq = {
    val rc = message_seq_counter
    message_seq_counter += 1
    rc
  }

  val store_flush_source = createSource(new ListEventAggregator[QueueEntry#Loaded](), dispatchQueue)
  store_flush_source.setEventHandler(^ {drain_store_flushes});
  store_flush_source.resume

  def drain_store_flushes() = {
    val data = store_flush_source.getData
    data.foreach { loaded =>
      loaded.flushed
    }
    messages.refiller.run

  }

  val store_load_source = createSource(new ListEventAggregator[(QueueEntry#Flushed, MessageRecord)](), dispatchQueue)
  store_load_source.setEventHandler(^ {drain_store_loads});
  store_load_source.resume


  def drain_store_loads() = {
    val data = store_load_source.getData
    data.foreach { case (flushed,messageRecord) =>
      flushed.loaded(messageRecord)
    }

    data.foreach { case (flushed,_) =>
      if( flushed.entry.hasSubs ) {
        flushed.entry.run
      }
    }
  }

  def collocate(value:DispatchQueue):Unit = {
    if( value.getTargetQueue ne dispatchQueue.getTargetQueue ) {
      debug("co-locating %s with %s", dispatchQueue.getLabel, value.getLabel);
      this.dispatchQueue.setTargetQueue(value.getTargetQueue)
    }
  }
}

object QueueEntry extends Sizer[QueueEntry] {
  def size(value: QueueEntry): Int = value.size
}

class QueueEntry(val queue:Queue, val seq:Long) extends LinkedNode[QueueEntry] with Comparable[QueueEntry] with Runnable with DispatchLogging {
  override protected def log = Queue
  // Subscriptions waiting to dispatch this entry.
  var parked:List[Subscription] = Nil

  // The number of subscriptions which have requested this entry to be prefeteched (held in memory) so that it's
  // ready for them to get dispatched.
  var prefetch_flags:Byte = 0

  // The current state of the entry: Tail | Loaded | Flushed | Tombstone
  var state:EntryState = new Tail

  def is_prefetched = prefetch_flags == 1

  def <(value:QueueEntry) = this.seq < value.seq
  def <=(value:QueueEntry) = this.seq <= value.seq

  def head():QueueEntry = {
    state = new Head
    this
  }

  def tail():QueueEntry = {
    state = new Tail
    this
  }

  def init(delivery:Delivery):QueueEntry = {
    state = new Loaded(delivery, false)
    queue.capacity_used += size
    this
  }

  def init(qer:QueueEntryRecord):QueueEntry = {
    state = new Flushed(qer.messageKey, qer.size)
    this
  }

  def init(range:QueueEntryRange):QueueEntry = {
    state = new FlushedRange(range.lastQueueSeq, range.count, range.size)
    this
  }

  def hasSubs = !parked.isEmpty

  /**
   * Dispatches this entry to the consumers and continues dispatching subsequent
   * entries as long as the dispatch results in advancing in their dispatch position.
   */
  def run() = {
    var next = this;
    while( next!=null && next.dispatch) {
      next = next.getNext
    }
  }

  def ::=(sub:Subscription) = {
    parked ::= sub
  }

  def :::=(l:List[Subscription]) = {
    parked :::= l
  }


  def -=(s:Subscription) = {
    parked = parked.filterNot(_ == s)
  }

  def nextOrTail():QueueEntry = {
    var entry = getNext
    if (entry == null) {
      entry = queue.tail_entry
    }
    entry
  }


  def compareTo(o: QueueEntry) = {
    (seq - o.seq).toInt
  }

  def toQueueEntryRecord = {
    val qer = new QueueEntryRecord
    qer.queueKey = queue.id
    qer.queueSeq = seq
    qer.messageKey = state.messageKey
    qer.size = state.size
    qer
  }

  override def toString = {
    "{seq: "+seq+", prefetch_flags: "+prefetch_flags+", value: "+state+", subscriptions: "+parked+"}"
  }

  /////////////////////////////////////////////////////
  //
  // State delegates..
  //
  /////////////////////////////////////////////////////

  // What state is it in?
  def as_head = state.as_head
  def as_tail = state.as_tail

  def as_flushed = state.as_flushed
  def as_flushed_range = state.as_flushed_range
  def as_loaded = state.as_loaded

  def label = state.label

  def is_tail = this == queue.tail_entry
  def is_head = this == queue.head_entry

  def is_loaded = as_loaded!=null
  def is_flushed = as_flushed!=null
  def is_flushed_range = as_flushed_range!=null

  // These should not change the current state.
  def count = state.count
  def size = state.size
  def messageKey = state.messageKey
  def is_flushed_or_flushing = state.is_flushed_or_flushing
  def dispatch() = state.dispatch

  // These methods may cause a change in the current state.
  def flush(asap:Boolean) = state.flush(asap)
  def load = state.load
  def remove = state.remove

  def flush_range = state.flush_range

  def can_combine_with_prev = {
    getPrevious !=null &&
      getPrevious.is_flushed_range &&
        ( is_flushed || is_flushed_range ) &&
          (getPrevious.count + count  < queue.tune_flush_range_size)
  }

  trait EntryState {

    final def entry:QueueEntry = QueueEntry.this

    def as_tail:Tail = null
    def as_loaded:Loaded = null
    def as_flushed:Flushed = null
    def as_flushed_range:FlushedRange = null
    def as_head:Head = null

    /**
     * Gets the size of this entry in bytes.  The head and tail entries always return 0.
     */
    def size = 0

    /**
     * Gets number of messages that this entry represents
     */
    def count = 0

    /**
     * Retuns a string label used to describe this state.
     */
    def label:String

    /**
     * Gets the message key for the entry.
     * @returns -1 if it is not known.
     */
    def messageKey = -1L

    /**
     * Attempts to dispatch the current entry to the subscriptions position at the entry.
     * @returns true if at least one subscription advanced to the next entry as a result of dispatching.
     */
    def dispatch() = false

    /**
     * @returns true if the entry is either flushed or flushing.
     */
    def is_flushed_or_flushing = false

    /**
     * Triggers the entry to get loaded if it's not already loaded.
     */
    def load = {}

    /**
     * Triggers the entry to get flushed if it's not already flushed.
     */
    def flush(asap:Boolean) = {}

    def flush_range:Unit = throw new AssertionError("should only be called on flushed entries");

    /**
     * Removes the entry from the queue's linked list of entries.  This gets called
     * as a result of an aquired ack.
     */
    def remove = {
      // advance subscriptions that were on this entry..
      advance(parked)
      parked = Nil

      // take the entry of the entries list..
      unlink
      //TODO: perhaps refill subscriptions.
    }

    /**
     * Advances the specified subscriptions to the next entry in
     * the linked list
     */
    def advance(advancing: Seq[Subscription]): Unit = {
      val nextPos = nextOrTail
      nextPos :::= advancing.toList
      advancing.foreach(_.advance(nextPos))
      queue.trigger_swap
    }

  }

  /**
   *  Used for the head entry.  This is the starting point for all new subscriptions.
   */
  class Head extends EntryState {

    def label = "head"
    override  def toString = "head"
    override def as_head = this

    /**
     * New subs get parked here at the Head.  There is nothing to actually dispatch
     * in this entry.. just advance the parked subs onto the next entry.
     */
    override def dispatch() = {
      if( parked != Nil ) {
        advance(parked)
        parked = Nil
        true

      } else {
        false
      }
    }

    override def remove = throw new AssertionError("Head entry cannot be removed")
    override def load = throw new AssertionError("Head entry cannot be loaded")
    override def flush(asap:Boolean) = throw new AssertionError("Head entry cannot be flushed")
  }

  /**
   * This state is used on the last entry of the queue.  It still has not been initialized
   * with a message, but it may be holding subscriptions.  This state transitions to Loaded
   * once a message is received.
   */
  class Tail extends EntryState {

    def label = "tail"
    override  def toString = "tail"
    override def as_tail:Tail = this

    override def remove = throw new AssertionError("Tail entry cannot be removed")
    override def load = throw new AssertionError("Tail entry cannot be loaded")
    override def flush(asap:Boolean) = throw new AssertionError("Tail entry cannot be flushed")

  }

  /**
   * The entry is in this state while a message is loaded in memory.  A message must be in this state
   * before it can be dispatched to a subscription.
   */
  class Loaded(val delivery: Delivery, var stored:Boolean) extends EntryState {

    assert( delivery!=null, "delivery cannot be null")

    var acquired = false
    var flushing = false

    def label = {
      var rc = "loaded"
      if( acquired ) {
        rc += "|aquired"
      }
      if( flushing ) {
        rc += "|flushing"
      }
      rc
    }

    override def toString = { "loaded:{ stored: "+stored+", flushing: "+flushing+", acquired: "+acquired+", size:"+size+"}" }

    override def count = 1
    override def size = delivery.size
    override def messageKey = delivery.storeKey

    override def is_flushed_or_flushing = {
      flushing
    }

    override  def as_loaded = this

    def store = {
      delivery.uow.enqueue(toQueueEntryRecord)
      delivery.uow.onComplete(^{
        queue.store_flush_source.merge(this)
      })
    }

    override def flush(asap:Boolean) = {
      if( queue.tune_swap ) {
        if( stored ) {
          flushing=true
          queue.flushing_size+=size
          flushed
        } else {
          if( !flushing ) {
            flushing=true
            queue.flushing_size+=size

            // The storeBatch is only set when called from the messages.offer method
            if( delivery.uow!=null ) {
              if( asap ) {
                delivery.uow.completeASAP
              }
            } else {

              // Are swapping out a non-persistent message?
              if( delivery.storeKey == -1 ) {
                
                delivery.uow = queue.host.store.createStoreUOW
                val uow = delivery.uow
                delivery.storeKey = uow.store(delivery.createMessageRecord)
                store
                if( asap ) {
                  uow.completeASAP
                }
                uow.release
                delivery.uow = null

              } else {
                  
                if( asap ) {
                  queue.host.store.flushMessage(messageKey) {
                    queue.store_flush_source.merge(this)
                  }
                }

              }

            }
          }
        }
      }
    }

    def flushed() = {
      stored = true
      delivery.uow = null
      if( flushing ) {
        flushing = false
        queue.flushing_size-=size
        queue.capacity_used -= size
        delivery.message.release

        state = new Flushed(delivery.storeKey, size)
        if( can_combine_with_prev ) {
          getPrevious.as_flushed_range.combineNext
        }
      }
    }

    override def load() = {
      if( flushing ) {
        flushing = false
        queue.flushing_size-=size
      }
    }

    override def remove = {
      if( flushing ) {
        flushing = false
        queue.flushing_size-=size
      }
      delivery.message.release
      queue.capacity_used -= size
      super.remove
    }

    override def dispatch():Boolean = {

      // Nothing to dispatch if we don't have subs..
      if( parked.isEmpty ) {
        return false
      }

      var heldBack = ListBuffer[Subscription]()
      var advancing = ListBuffer[Subscription]()

      var acquiringSub: Subscription = null
      parked.foreach{ sub=>

        if( sub.browser ) {
          if (!sub.matches(delivery)) {
            // advance: not interested.
            advancing += sub
          } else {
            if (sub.offer(delivery)) {
              // advance: accepted...
              advancing == sub
            } else {
              // hold back: flow controlled
              heldBack += sub
            }
          }

        } else {
          if( acquired ) {
            // advance: another sub already acquired this entry..
            advancing += sub
          } else {
            if (!sub.matches(delivery)) {
              // advance: not interested.
              advancing += sub
            } else {

              // Is the sub flow controlled?
              if( sub.full ) {
                // hold back: flow controlled
                heldBack += sub
              } else {
                // advance: accepted...
                acquiringSub = sub
                acquired = true

                val acquiredQueueEntry = sub.acquire(entry)
                val acquiredDelivery = delivery.copy
                acquiredDelivery.ack = (consumed, tx)=> {
                  queue.ack_source.merge((acquiredQueueEntry, consumed, tx))
                }

                assert(sub.offer(acquiredDelivery), "sub should have accepted, it had reported not full earlier.")
              }
            }
          }
        }
      }

      // The acquiring sub is added last to the list so that
      // the other competing subs get first dibs at the next entry.
      if( acquiringSub != null ) {
        advancing += acquiringSub
      }

      if ( advancing.isEmpty ) {
        return false
      } else {

        // The held back subs stay on this entry..
        parked = heldBack.toList

        // the advancing subs move on to the next entry...
        advance(advancing)

//        // flush this entry out if it's not going to be needed soon.
//        if( !hasSubs && prefetch_flags==0 ) {
//          // then flush out to make space...
//          var flush_asap = !acquired
//          flush(flush_asap)
//        }
        queue.trigger_swap
        return true
      }
    }
  }

  /**
   * Loaded entries are moved into the Flushed state reduce memory usage.  Once a Loaded
   * entry is persisted, it can move into this state.  This state only holds onto the
   * the massage key so that it can reload the message from the store quickly when needed.
   */
  class Flushed(override val messageKey:Long, override val size:Int) extends EntryState {

    queue.flushed_items += 1

    var loading = false


    override def count = 1

    override def as_flushed = this

    override def is_flushed_or_flushing = true

    def label = {
      var rc = "flushed"
      if( loading ) {
        rc += "|loading"
      }
      rc
    }
    override def toString = { "flushed:{ loading: "+loading+", size:"+size+"}" }

    override def load() = {
      if( !loading ) {
//        trace("Start entry load of message seq: %s", seq)
        // start loading it back...
        loading = true
        queue.loading_size += size
        queue.host.store.loadMessage(messageKey) { delivery =>
          // pass off to a source so it can aggregate multiple
          // loads to reduce cross thread synchronization
          if( delivery.isDefined ) {
            queue.store_load_source.merge((this, delivery.get))
          } else {

            info("Detected store dropped message at seq: %d", seq)

            // Looks like someone else removed the message from the store.. lets just
            // tombstone this entry now.
            queue.dispatchQueue {
              remove
            }
          }
        }
      }
    }

    def loaded(messageRecord:MessageRecord) = {
      if( loading ) {
//        debug("Loaded message seq: ", seq )
        loading = false
        queue.loading_size -= size

        val delivery = new Delivery()
        delivery.message = ProtocolFactory.get(messageRecord.protocol.toString).get.decode(messageRecord)
        delivery.size = messageRecord.size
        delivery.storeKey = messageRecord.key

        queue.capacity_used += delivery.size
        queue.flushed_items -= 1
        state = new Loaded(delivery, true)
      } else {
//        debug("Ignoring store load of: ", messageKey)
      }
    }


    override def remove = {
      if( loading ) {
        loading = false
        queue.loading_size -= size
      }
      queue.flushed_items -= 1
      super.remove
    }

    override def flush_range = {
      if( loading ) {
        loading = false
        queue.loading_size -= size
      }
      queue.flushed_items -= 1
      state = new FlushedRange(seq, 1, size)
    }
  }

  /**
   * A FlushedRange stat is assigned entry is used to represent a rage of flushed entries.
   *
   * Even when entries that are Flushed can us a significant amount of memory if the queue is holding
   * thousands of them.  Multiple entries in the Flushed state can be combined into a single entry in
   * the FlushedRange state thereby conserving even more memory.  A FlushedRange entry only tracks
   * the first, and last sequnce ids of the range.  When the entry needs to be loaded from the range
   * it replaces the FlushedRange entry with all the Flushed entries by querying the store of all the
   * message keys for the entries in the range.
   */
  class FlushedRange(
    /** the last seq id in the range */
    var last:Long,
    /** the number of items in the range */
    var _count:Int,
    /** size in bytes of the range */
    var _size:Int) extends EntryState {

    override def count = _count
    override def size = _size

    var loading = false

    override def as_flushed_range = this

    override def is_flushed_or_flushing = true

    def label = {
      var rc = "flushed_range"
      if( loading ) {
        rc = "flushed_range|loading"
      }
      rc
    }
    override def toString = { "flushed_range:{ loading: "+loading+", count: "+count+", size: "+size+"}" }

    override def load() = {
      if( !loading ) {
        loading = true
        queue.host.store.listQueueEntries(queue.id, seq, last) { records =>
          queue.dispatchQueue {

            var item_count=0
            var size_count=0

            val tmpList = new LinkedNodeList[QueueEntry]()
            records.foreach { record =>
              val entry = new QueueEntry(queue, record.queueSeq).init(record)
              tmpList.addLast(entry)
              item_count += 1
              size_count += record.size
            }

            // we may need to adjust the enqueue count if entries
            // were dropped at the store level
            var item_delta = (count - item_count)
            val size_delta: Int = size - size_count

            if ( item_delta!=0 || size_delta!=0 ) {
              info("Detected store change in range %d to %d. %d message(s) and %d bytes", seq, last, item_delta, size_delta)
              queue.enqueue_item_counter += item_delta
              queue.enqueue_size_counter += size_delta
            }

            linkAfter(tmpList)
            val next = getNext

            // move the subs to the first entry that we just loaded.
            parked.foreach(_.advance(next))
            next :::= parked
            queue.trigger_swap

            unlink

            // TODO: refill prefetches
          }
        }
      }
    }

    /**
     * Combines this queue entry with the next queue entry.
     */
    def combineNext():Unit = {
      val value = getNext
      assert(value!=null)
      assert(value.is_flushed || value.is_flushed_range)
      if( value.is_flushed ) {
        assert(last < value.seq )
        last = value.seq
        _count += 1
        _size += value.size
        value.remove
      } else if( value.is_flushed_range ) {
        assert(last < value.seq )
        last = value.as_flushed_range.last
        _count += value.as_flushed_range.count
        _size += value.size
        value.remove
      }
    }

  }

}

/**
 * Interfaces a DispatchConsumer with a Queue.  Tracks current position of the consumer
 * on the queue, and the delivery rate so that slow consumers can be detected.  It also
 * tracks the entries which the consumer has acquired.
 *
 */
class Subscription(val queue:Queue, val consumer:DeliveryConsumer) extends DeliveryProducer with DispatchLogging {
  override protected def log = Queue

  def dispatchQueue = queue.dispatchQueue

  val id = Queue.subcsription_counter.incrementAndGet
  var acquired = new LinkedNodeList[AcquiredQueueEntry]
  var session: DeliverySession = null
  var pos:QueueEntry = null

  var acquired_size = 0L
  def acquired_count = acquired.size()

  var total_advanced_size = 0L
  var advanced_size = 0
  var advanced_sizes = ListBuffer[Int]() // use circular buffer instead.

  var avg_advanced_size = queue.tune_consumer_buffer
  var tail_parkings = 1

  var total_dispatched_count = 0L
  var total_dispatched_size = 0L

  var total_ack_count = 0L
  var total_nack_count = 0L

  override def toString = {
    def seq(entry:QueueEntry) = if(entry==null) null else entry.seq
    "{ id: "+id+", acquired_size: "+acquired_size+", pos: "+seq(pos)+"}"
  }

  def browser = session.consumer.browser

  // This opens up the consumer
  pos = queue.head_entry;
  session = consumer.connect(this)
  session.refiller = pos
  queue.head_entry ::= this

  if( queue.serviceState.isStarted ) {
    // kick off the initial dispatch.
    refill_prefetch
    queue.dispatchQueue << queue.head_entry
  }

  def close() = {
    pos -= this
    pos = null

    // nack all the acquired entries.
    var next = acquired.getHead
    while( next !=null ) {
      val cur = next;
      next = next.getNext
      cur.nack // this unlinks the entry.
    }

    session.refiller = NOOP
    session.close
    session = null

    queue.trigger_swap
  }

  /**
   * Advances the subscriptions position to the specified
   * queue entry.
   */
  def advance(value:QueueEntry):Unit = {

    assert(value!=null)
    if( pos == null ) {
      assert(pos!=null)
    }

    advanced_size += pos.size

    pos = value
    session.refiller = pos

    if( tail_parked ) {
      tail_parkings += 0
    }
  }

  /**
   * Rewinds to a previously seen location.. Happens when
   * a nack occurs from another consumer.
   */
  def rewind(value:QueueEntry):Unit = {
    assert(value!=null)
    pos -= this
    value ::= this
    pos = value
    session.refiller = value
    queue.dispatchQueue << value // queue up the entry to get dispatched..
  }

  def tail_parked = pos eq queue.tail_entry

  def matches(entry:Delivery) = session.consumer.matches(entry)
  def full = session.full
  def offer(delivery:Delivery) = {
    if( session.offer(delivery) ) {
      total_dispatched_count += 1
      total_dispatched_size += delivery.size
      true
    } else {
      false
    }
  }

  def acquire(entry:QueueEntry) = new AcquiredQueueEntry(entry)

  def refill_prefetch = {

    var next = if( pos.is_tail ) {
      null // can't prefetch the tail..
    } else if( pos.is_head ) {
      pos.getNext // can't prefetch the head.
    } else {
      pos // start prefetching from the current position.
    }

    var remaining = queue.tune_consumer_buffer - acquired_size
    while( remaining>0 && next!=null ) {
      remaining -= next.size
      next.prefetch_flags = (next.prefetch_flags | PREFTCH_LOAD_FLAG).toByte
      next.load
      next = next.getNext
    }

    remaining = avg_advanced_size
    while( remaining>0 && next!=null ) {
      remaining -= next.size
      next.prefetch_flags = (next.prefetch_flags | PREFTCH_HOLD_FLAG).toByte
      next = next.getNext
    }

  }

  class AcquiredQueueEntry(val entry:QueueEntry) extends LinkedNode[AcquiredQueueEntry] {

    acquired.addLast(this)
    acquired_size += entry.size

    def ack(sb:StoreUOW):Unit = {
      // The session may have already been closed..
      if( session == null ) {
        return;
      }
      total_ack_count += 1
      if (entry.messageKey != -1) {
        val storeBatch = if( sb == null ) {
          queue.host.store.createStoreUOW
        } else {
          sb
        }
        storeBatch.dequeue(entry.toQueueEntryRecord)
        if( sb == null ) {
          storeBatch.release
        }
      }
      if( sb != null ) {
        sb.release
      }

      queue.dequeue_item_counter += 1
      queue.dequeue_size_counter += entry.size

      // removes this entry from the acquired list.
      unlink()

      // we may now be able to prefetch some messages..
      acquired_size -= entry.size

      val next = entry.nextOrTail
      entry.remove // entry size changes to 0

      queue.trigger_swap
      next.run
    }

    def nack:Unit = {
      // The session may have already been closed..
      if( session == null ) {
        return;
      }

      total_nack_count += 1
      entry.as_loaded.acquired = false
      acquired_size -= entry.size

      // track for stats
      queue.nack_item_counter += 1
      queue.nack_size_counter += entry.size

      // rewind all the matching competing subs past the entry.. back to the entry
      queue.all_subscriptions.valuesIterator.foreach{ sub=>
        if( !sub.browser && entry.seq < sub.pos.seq && sub.matches(entry.as_loaded.delivery)) {
          sub.rewind(entry)
        }
      }
      unlink()
    }
  }

}

