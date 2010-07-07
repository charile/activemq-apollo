/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
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
package org.apache.activemq.ng

import _root_.java.util.concurrent.atomic.AtomicInteger
import _root_.java.util.concurrent.TimeUnit
import _root_.java.util.LinkedList
import _root_.org.apache.activemq.util.buffer._
import _root_.org.fusesource.hawtdispatch._
import _root_.org.fusesource.hawtdispatch.ScalaDispatch._
import java.nio.channels.SelectionKey._

import java.net.{InetAddress, InetSocketAddress}

import java.nio.channels.{ServerSocketChannel}

object Delivery {
  type HeaderMap = LinkedList[(AsciiBuffer, AsciiBuffer)]
  def apply(frame:StompFrame) = new Delivery(frame.headers, frame.content, frame.size)
  def apply(d:Delivery) = new Delivery(d.headers, d.content, d.size)
}
  
case class Delivery(headers:Delivery.HeaderMap, content:Buffer, size:Int) extends BaseRetained {
}

trait Producer {
  def collocate(queue:DispatchQueue):Unit
}

trait Consumer extends Retained {
  val queue:DispatchQueue;
  def open_session(producer_queue:DispatchQueue):ConsumerSession

}

trait ConsumerSession {
  val consumer:Consumer
  def deliver(delivery:Delivery)
  def close:Unit
}

/**
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
object StompBroker {


  def main(args:Array[String]) = {
    println("Starting stomp broker...")
    val broker = new StompBroker();
    println("Startup complete.")
    System.in.read
    println("Shutting down...")
    broker.close
    println("Shutdown complete.")
  }
}

/**
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
class StompBroker {
  import StompBroker._

  val router = new Router(createQueue("router"))
  val queue = createQueue("broker")

    // Create the nio server socket...
  val channel = ServerSocketChannel.open();
  channel.configureBlocking(false);
  channel.socket().bind(address("0.0.0.0", 61613), 500);

  // Create a source attached to the server socket to deal with new connections...
  val accept_source = createSource(channel, OP_ACCEPT, queue);
  accept_source.setEventHandler(^{

    // Accept a new socket connection
    var socket = channel.accept();
    try {
      socket.configureBlocking(false);
      socket.socket.setSoLinger(true,0);
      var connection = new StompConnection(socket, router)
    } catch {
      case e:Exception=>
        socket.close
    }

  });
  accept_source.setCancelHandler(^{
    channel.close();
  });

  // Start listening for accept events..
  accept_source.resume();

  def close = {
    accept_source.cancel
    accept_source.release
    queue.release
  }

  private  def address(host: String, port: Int): InetSocketAddress = {
    return new InetSocketAddress(ip(host), port)
  }

  private def ip(host: String): InetAddress = {
    return InetAddress.getByName(host)
  }

  // Try to periodically re-balance connections so that consumers/producers
  // are on the same thread.
  val reblance = ^{
    router.each { (destination,node)=>
      // for now just collocate the producer to the consumer's thread..
      if( !node.targets.isEmpty ) {
        val target =  node.targets.head.queue
        if( node.isInstanceOf[Router#QueueDestinationNode] ) {
          node.asInstanceOf[Router#QueueDestinationNode].queue.collocate(target)
        }
        for( route <- node.routes ) {
          route.producer.collocate( target )
        }
      }
    }
    schedualRebalance
  }
  
  def schedualRebalance:Unit = router.queue.dispatchAfter(1000, TimeUnit.MILLISECONDS, reblance)
  schedualRebalance
}


