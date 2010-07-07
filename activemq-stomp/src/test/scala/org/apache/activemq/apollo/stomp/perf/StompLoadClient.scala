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
package org.apache.activemq.apollo.stomp.perf

import _root_.java.io._
import _root_.java.util.concurrent.atomic.{AtomicBoolean, AtomicLong}
import _root_.org.fusesource.hawtbuf.AsciiBuffer
import _root_.org.fusesource.hawtbuf.{ByteArrayOutputStream => BAOS}

import java.net.{ProtocolException, InetSocketAddress, URI, Socket}

import java.lang.String._
import java.util.concurrent.TimeUnit._
import collection.mutable.Map
import org.apache.activemq.apollo.stomp.Stomp

/**
 *
 * Simulates load on the a stomp broker.
 *
 */
object StompLoadClient {

  val NANOS_PER_SECOND = NANOSECONDS.convert(1, SECONDS);
  import StompLoadClient._
  implicit def toAsciiBuffer(value: String) = new AsciiBuffer(value)

  var producerSleep = 0;
  var consumerSleep = 0;
  var producers = 1;
  var consumers = 1;
  var sampleInterval = 5 * 1000;
  var uri = "stomp://127.0.0.1:61613";
  var bufferSize = 64*1204
  var messageSize = 1024;
  var useContentLength=true
  var persistent = false;
  var syncSend = false;
  var headers = List[String]()
  var ack = "client";
  var selector:String = null

  var destinationType = "queue";
  var destinationCount = 1;

  val producerCounter = new AtomicLong();
  val consumerCounter = new AtomicLong();
  val done = new AtomicBoolean()

  def main(args:Array[String]) = run

  def run() = {

    println("=======================")
    println("Press ENTER to shutdown");
    println("=======================")
    println("")


    done.set(false)
    var producerThreads = List[ProducerThread]()
    for (i <- 0 until producers) {
      val producerThread = new ProducerThread(i);
      producerThreads = producerThread :: producerThreads
      producerThread.start();
    }

    var consumerThreads = List[ConsumerThread]()
    for (i <- 0 until consumers) {
      val consumerThread = new ConsumerThread(i);
      consumerThreads = consumerThread :: consumerThreads
      consumerThread.start();
    }

    // start a sampling thread...
    val sampleThread = new Thread() {
      override def run() = {
        try {
          var start = System.nanoTime();
          while( !done.get ) {
            Thread.sleep(sampleInterval)
            val end = System.nanoTime();
            printRate("Producer", producerCounter, end - start);
            printRate("Consumer", consumerCounter, end - start);
            start = end;
          }
        } catch {
          case e:InterruptedException =>
        }
      }
    }
    sampleThread.start()


    System.in.read()
    done.set(true)

    // wait for the threads to finish..
    for( thread <- consumerThreads ) {
      thread.client.close
      thread.interrupt
      thread.join
    }
    for( thread <- producerThreads ) {
      thread.client.close
      thread.interrupt
      thread.join
    }
    sampleThread.interrupt
    sampleThread.join

    println("=======================")
    println("Shutdown");
    println("=======================")

  }

  override def toString() = {
    "--------------------------------------\n"+
    "StompLoadClient Properties\n"+
    "--------------------------------------\n"+
    "uri              = "+uri+"\n"+
    "destinationType  = "+destinationType+"\n"+
    "destinationCount = "+destinationCount+"\n" +
    "sampleInterval   = "+sampleInterval+"\n" +
    "\n"+
    "--- Producer Properties ---\n"+
    "producers        = "+producers+"\n"+
    "messageSize      = "+messageSize+"\n"+
    "persistent       = "+persistent+"\n"+
    "syncSend         = "+syncSend+"\n"+
    "useContentLength = "+useContentLength+"\n"+
    "producerSleep    = "+producerSleep+"\n"+
    "headers          = "+headers+"\n"+
    "\n"+
    "--- Consumer Properties ---\n"+
    "consumers        = "+consumers+"\n"+
    "consumerSleep    = "+consumerSleep+"\n"+
    "ack              = "+ack+"\n"+
    "selector         = "+selector+"\n"+
    ""

  }

  def printRate(name: String, counter: AtomicLong, nanos: Long) = {
    val c = counter.getAndSet(0);
    val rate_per_second: java.lang.Float = ((1.0f * c / nanos) * NANOS_PER_SECOND);
    println(format("%s rate: %,.3f per second", name, rate_per_second));
  }

  def destination(i:Int) = "/"+destinationType+"/load-"+(i%destinationCount)


  object StompClient {
    def connect(proc: StompClient=>Unit ) = {
      val client = new StompClient();
      try {
        val connectUri = new URI(uri);
        client.open(connectUri.getHost(), connectUri.getPort());
        client.send("""CONNECT

""")
        client.flush
        client.receive("CONNECTED")

        proc(client)
      } catch {
        case e: Throwable =>
          if(!done.get) {
            println("failure occured: "+e);
            Thread.sleep(1000);
          }
      } finally {
        try {
          client.close();
        } catch {
          case ignore: Throwable =>
        }
      }
    }
  }

  class StompClient {

    var socket:Socket = null
    var out:OutputStream = null;
    var in:InputStream = null

    def open(host: String, port: Int) = {
      socket = new Socket
      socket.connect(new InetSocketAddress(host, port))
      socket.setSoLinger(true, 0);
      out = new BufferedOutputStream(socket.getOutputStream, bufferSize)
      in = new BufferedInputStream(socket.getInputStream, bufferSize)
    }

    def close() = {
      if( socket!=null ) {
        socket.close
        socket = null
        out = null
        in = null
      }
    }

    def flush() = {
      out.flush
    }

    def send(frame:String) = {
      out.write(frame.getBytes("UTF-8"))
      out.write(0)
      out.write('\n')
    }

    def send(frame:Array[Byte]) = {
      out.write(frame)
      out.write(0)
      out.write('\n')
    }

    def skip():Unit = {
      var c = in.read;
      while( c >= 0 ) {
        if( c==0 ) {
          return;
        }
        c = in.read()
      }
      throw new EOFException()
    }

    def receive():String = {
      val buffer = new ByteArrayOutputStream(messageSize+200)
      var c = in.read;
      while( c >= 0 ) {
        if( c==0 ) {
          return new String(buffer.toByteArray, "UTF-8")
        }
        buffer.write(c);
        c = in.read()
      }
      throw new EOFException()
    }

    def receiveAscii():AsciiBuffer = {
      val buffer = new BAOS(messageSize+200)
      var c = in.read;
      while( c >= 0 ) {
        if( c==0 ) {
          return buffer.toBuffer.ascii
        }
        buffer.write(c);
        c = in.read()
      }
      throw new EOFException()
    }

    def receive(expect:String):String = {
      val rc = receive()
      if( !rc.trimFront.startsWith(expect) ) {
        throw new ProtocolException("Expected "+expect)
      }
      rc
    }

  }

  class ProducerThread(val id: Int) extends Thread {
    val name: String = "producer " + id;
    var client:StompClient=null
    val content = ("SEND\n" +
              "destination:"+destination(id)+"\n"+
               { if(persistent) "persistent:true\n" else "" } +
               { if(syncSend) "receipt:xxx\n" else "" } +
               { headers.foldLeft("") { case (sum, v)=> sum+v+"\n" } } +
               { if(useContentLength) "content-length:"+messageSize+"\n" else "" } +
              "\n"+message(name)).getBytes("UTF-8")

    override def run() {
      while (!done.get) {
        StompClient.connect { client =>
          this.client=client
          var i =0;
          while (!done.get) {
            client.send(content)
            if( syncSend ) {
              // waits for the reply..
              client.flush
              client.skip
            }
            producerCounter.incrementAndGet();
            if(producerSleep > 0) {
              client.flush
              Thread.sleep(producerSleep);
            }
            i += 1
          }
        }
      }
    }
  }

  def message(name:String) = {
    val buffer = new StringBuffer(messageSize)
    buffer.append("Message from " + name+"\n");
    for( i <- buffer.length to messageSize ) {
      buffer.append(('a'+(i%26)).toChar)
    }
    var rc = buffer.toString
    if( rc.length > messageSize ) {
      rc.substring(0, messageSize)
    } else {
      rc
    }
  }

  class ConsumerThread(val id: Int) extends Thread {
    val name: String = "producer " + id;
    var client:StompClient=null

    override def run() {
      while (!done.get) {
        StompClient.connect { client =>
          this.client=client
          val headers = Map[AsciiBuffer, AsciiBuffer]();
          client.send("""
SUBSCRIBE""" + (if(selector==null) {""} else {
"""
selector: """+selector
}) + """
ack:"""+ack+"""
destination:"""+destination(id)+"""

""")
          client.flush
          receiveLoop
        }
      }
    }

    def receiveLoop() = {
      val clientAck = ack == "client"
      while (!done.get) {
        if( clientAck ) {
          val msg = client.receiveAscii()
          val start = msg.indexOf(Stomp.Headers.Message.MESSAGE_ID)
          assert( start >= 0 )
          val end = msg.indexOf("\n", start)
          val msgId = msg.slice(start+Stomp.Headers.Message.MESSAGE_ID.length+1, end).ascii
          client.send("""
ACK
message-id:"""+msgId+"""

""")
          client.flush

        } else {
          client.skip
        }
        consumerCounter.incrementAndGet();
        Thread.sleep(consumerSleep);
      }
    }
  }

}
