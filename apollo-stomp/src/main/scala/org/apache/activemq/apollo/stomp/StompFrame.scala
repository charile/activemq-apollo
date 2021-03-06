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
package org.apache.activemq.apollo.stomp

import _root_.java.util.LinkedList
import _root_.org.apache.activemq.apollo.filter.{Expression, Filterable}
import _root_.org.fusesource.hawtbuf._
import collection.mutable.ListBuffer
import java.lang.{String, Class}
import org.apache.activemq.apollo.broker._
import org.apache.activemq.apollo.util._
import org.fusesource.hawtdispatch.BaseRetained
import java.io.{OutputStream, DataOutput}

/**
 *
 * @author <a href="http://hiramchirino.com">chirino</a>
 */
object StompFrameConstants {
  type HeaderMap = List[(AsciiBuffer, AsciiBuffer)]
  type HeaderMapBuffer = ListBuffer[(AsciiBuffer, AsciiBuffer)]
  val NO_DATA = new Buffer(0);

}

import StompFrameConstants._
import StompConstants._;
import BufferConversions._
import Buffer._

/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
case class StompFrameMessage(frame:StompFrame) extends Message {
  
  def protocol = StompProtocol

  /**
   * the globally unique id of the message
   */
  var id: AsciiBuffer = null

  /**
   * the globally unique id of the producer
   */
  var producer: AsciiBuffer = null

  /**
   *  the message priority.
   */
  var priority:Byte = 4;

  /**
   * a positive value indicates that the delivery has an expiration
   * time.
   */
  var expiration: Long = -1;

  /**
   * true if the delivery is persistent
   */
  var persistent = false

  /**
   * where the message was sent to.
   */
  var destination: Destination = null

  for( header <- (frame.updated_headers ::: frame.headers).reverse ) {
    header match {
      case (Stomp.Headers.Message.MESSAGE_ID, value) =>
        id = value
      case (Stomp.Headers.Send.PRIORITY, value) =>
        priority = java.lang.Integer.parseInt(value).toByte
      case (Stomp.Headers.Send.DESTINATION, value) =>
        destination = value
      case (Stomp.Headers.Send.EXPIRATION_TIME, value) =>
        expiration = java.lang.Long.parseLong(value)
      case (Stomp.Headers.Send.PERSISTENT, value) =>
        persistent = java.lang.Boolean.parseBoolean(value)
      case _ =>
    }
  }

  def getBodyAs[T](toType : Class[T]) = {
    (frame.content match {
      case x:BufferContent =>
        if( toType == classOf[String] ) {
          x.content.utf8
        } else if (toType == classOf[Buffer]) {
          x.content
        } else if (toType == classOf[AsciiBuffer]) {
          x.content.ascii
        } else if (toType == classOf[UTF8Buffer]) {
          x.content.utf8
        } else {
          null
        }
      case x:DirectContent =>
        null
      case NilContent =>
        if( toType == classOf[String] ) {
          ""
        } else if (toType == classOf[Buffer]) {
          new Buffer(0)
        } else if (toType == classOf[AsciiBuffer]) {
          new AsciiBuffer("")
        } else if (toType == classOf[UTF8Buffer]) {
          new UTF8Buffer("")
        } else {
          null
        }
    }).asInstanceOf[T]
  }

  def getLocalConnectionId = {
    val pos = id.indexOf(':')
    assert(pos >0 )
    id.slice(id.offset, pos).toString
  }

  /* avoid paying the price of creating the header index. lots of times we don't need it */
  lazy val headerIndex: Map[AsciiBuffer, AsciiBuffer] =  {
    var rc = Map[AsciiBuffer, AsciiBuffer]()
    for( header <- (frame.updated_headers ::: frame.headers).reverse ) {
      rc += (header._1 -> header._2)
    }
    rc
  }

  def getProperty(name: String):AnyRef = {
    (name match {
      // TODO: handle more of the JMS Types that ActiveMQ 5 supports.
      case "JMSMessageID" =>
        Some(id)
      case "JMSType" =>
        headerIndex.get(ascii("type"))
      case _=>
        headerIndex.get(ascii(name))
    }) match {
      case Some(rc) => rc.utf8.toString
      case None => null
    }
  }


  def setDisposer(disposer: Runnable) = throw new UnsupportedOperationException
  def retained = throw new UnsupportedOperationException
  def retain = frame.retain
  def release = frame.release
}



/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
object StompFrame extends Sizer[StompFrame] {
  def size(value:StompFrame) = value.size
}

/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
trait StompContent {
  def length:Int

  def isEmpty = length == 0

  def writeTo(os:OutputStream)

  def utf8:UTF8Buffer

  def retain = {}
  def release = {}
}

/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
object NilContent extends StompContent {
  def length = 0
  def writeTo(os:OutputStream) = {}
  val utf8 = new UTF8Buffer("")
}

/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
case class BufferContent(content:Buffer) extends StompContent {
  def length = content.length
  def writeTo(os:OutputStream) = content.writeTo(os)
  def utf8:UTF8Buffer = content.utf8
}

/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
case class DirectContent(direct_buffer:DirectBuffer) extends StompContent {
  def length = direct_buffer.size-1

  def writeTo(os:OutputStream) = {
    val buff = new Array[Byte](1024*4)
    val source = direct_buffer.buffer.duplicate
    var remaining = direct_buffer.size-1
    while( remaining> 0 ) {
      val c = remaining.min(buff.length)
      source.get(buff, 0, c)
      os.write(buff, 0, c)
      remaining -= c
    }
  }

  def buffer:Buffer = {
    val rc = new DataByteArrayOutputStream(direct_buffer.size-1)
    writeTo(rc)
    rc.toBuffer
  }

  def utf8:UTF8Buffer = {
    buffer.utf8
  }

  override def retain = direct_buffer.retain
  override def release = direct_buffer.release
}

/**
 * Represents all the data in a STOMP frame.
 *
 * @author <a href="http://hiramchirino.com">chirino</a>
 */
case class StompFrame(action:AsciiBuffer, headers:HeaderMap=Nil, content:StompContent=NilContent, updated_headers:HeaderMap=Nil) {

  def size_of_updated_headers = {
    size_of(updated_headers)
  }

  def size_of_original_headers = {
    if( headers.isEmpty ) {
      0
    } else {
      // if all the headers were part of the same input buffer.. size can be calculated by
      // subtracting positions in the buffer.
      val firstBuffer = headers.head._1
      val lastBuffer =  headers.last._2
      if( firstBuffer.data eq lastBuffer.data ) {
        (lastBuffer.offset-firstBuffer.offset)+lastBuffer.length+1
      } else {
        // gota do it the hard way
        size_of(headers)
      }
    }
  }

  private def size_of(headers:HeaderMap): Int = {
    var rc = 0;
    val i = headers.iterator
    while (i.hasNext) {
      val (key, value) = i.next
      rc += (key.length + value.length + 2)
    }
    rc
  }

  def are_headers_in_content_buffer = !headers.isEmpty && 
          content.isInstanceOf[BufferContent] &&
          ( headers.head._1.data eq content.asInstanceOf[BufferContent].content.data )

  def size:Int = {
     content match {
       case x:BufferContent =>
         if( (action.data eq x.content.data) && updated_headers==Nil ) {
            return (x.content.offset-action.offset)+x.content.length
         }
       case _ =>
     }

     action.length + 1 +
     size_of_updated_headers +
     size_of_original_headers + 1 + content.length
  }

  def header(name:AsciiBuffer) = {
    updated_headers.filter( _._1 == name ).headOption.orElse(
      headers.filter( _._1 == name ).headOption
    ).map(_._2).getOrElse(null)
  }

  def retain = content.retain
  def release = content.release
}
