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

import _root_.java.util.LinkedList
import _root_.org.apache.activemq.util.buffer._
import collection.mutable.Map
import collection.mutable.HashMap

object StompFrame{
  var NO_DATA = new Buffer(0);
}

/**
 * Represents all the data in a STOMP frame.
 *
 * @author <a href="http://hiramchirino.com">chirino</a>
 */
case class StompFrame(action:AsciiBuffer, headers:LinkedList[(AsciiBuffer, AsciiBuffer)]=new LinkedList(), content:Buffer=StompFrame.NO_DATA) {
  def headerSize = {
    if( headers.isEmpty ) {
      0
    } else {
      // if all the headers were part of the same input buffer.. size can be calculated by
      // subtracting positions in the buffer.
      val firstBuffer = headers.getFirst._1
      val lastBuffer =  headers.getLast._2
      if( firstBuffer.data eq lastBuffer.data ) {
        (lastBuffer.offset-firstBuffer.offset)+lastBuffer.length+1
      } else {
        // gota do it the hard way
        var rc = 0;
        val i = headers.iterator
        while( i.hasNext ) {
          val (key, value) = i.next
          rc += key.length + value.length +2
        }
        rc
      }
    }
  }

  def size = {
     if( action.data eq content.data ) {
        (content.offset-action.offset)+content.length
     } else {
       action.length + 1 +
       headerSize + 1 + content.length
     }
  }

//    public StompFrame(AsciiBuffer command) {
//    	this(command, null, null);
//    }
//
//    public StompFrame(AsciiBuffer command, Map<AsciiBuffer, AsciiBuffer> headers) {
//    	this(command, headers, null);
//    }
//
//    public StompFrame(AsciiBuffer command, Map<AsciiBuffer, AsciiBuffer> headers, Buffer data) {
//        this.action = command;
//        if (headers != null)
//        	this.headers = headers;
//        if (data != null)
//        	this.content = data;
//    }
//
//    public StompFrame() {
//    }


//    public String toString() {
//        StringBuffer buffer = new StringBuffer();
//        buffer.append(getAction());
//        buffer.append("\n");
//
//        for (Entry<AsciiBuffer, AsciiBuffer> entry : headers.entrySet()) {
//            buffer.append(entry.getKey());
//            buffer.append(":");
//            buffer.append(entry.getValue());
//            buffer.append("\n");
//        }
//
//        buffer.append("\n");
//        if (getContent() != null) {
//            try {
//                buffer.append(getContent());
//            } catch (Throwable e) {
//            }
//        }
//        return buffer.toString();
//    }

}
