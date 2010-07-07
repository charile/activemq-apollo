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
package org.apache.activemq.apollo.broker

import _root_.java.beans.ExceptionListener
import _root_.java.io.{IOException}
import _root_.org.apache.activemq.filter.{BooleanExpression}
import _root_.org.apache.activemq.transport._
import _root_.org.apache.activemq.Service
import _root_.java.lang.{String}
import _root_.org.apache.activemq.util.{FactoryFinder}
import _root_.org.apache.activemq.wireformat.WireFormat
import _root_.org.fusesource.hawtdispatch.ScalaDispatch._
import java.util.concurrent.atomic.AtomicLong
import org.fusesource.hawtdispatch.Dispatch
import protocol.{ProtocolFactory, ProtocolHandler}

/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
object Connection extends Log {
  val id_generator = new AtomicLong()
  def next_id = "connection:"+id_generator.incrementAndGet
}

/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
abstract class Connection() extends DefaultTransportListener with BaseService  {

  override protected def log = Connection

  import Connection._
  val id = next_id
  val dispatchQueue = createQueue(id)
  var stopped = true
  var transport:Transport = null
  var transportSink:TransportSink = null 

  override def toString = id

  override protected def _start(onCompleted:Runnable) = {
    stopped = false
    transportSink = new TransportSink(transport)
    transport.setDispatchQueue(dispatchQueue);
    transport.setTransportListener(Connection.this);
    transport.start(onCompleted)
  }

  override protected def _stop(onCompleted:Runnable) = {
    stopped = true
    transport.stop(onCompleted)
  }


  override def onTransportFailure(error:IOException) = {
    if (!stopped) {
        onFailure(error);
    }
  }

  def onFailure(error:Exception) = {
    warn(error)
    transport.stop
  }

  override def onRefill = {
    if( transportSink.refiller !=null ) {
      transportSink.refiller.run
    }
  }

}

/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
class BrokerConnection(val connector: Connector) extends Connection {

  var protocol = "stomp"
  var protocolHandler: ProtocolHandler = null;

  override protected  def _start(onCompleted:Runnable) = {
    connector.dispatchQueue.retain
    protocolHandler = ProtocolFactory.get(protocol).createProtocolHandler
    protocolHandler.setConnection(this);
    super._start(onCompleted)
  }

  override protected def _stop(onCompleted:Runnable) = {
    connector.stopped(this)
    connector.dispatchQueue.release
    super._stop(onCompleted)
  }

  override def onTransportConnected() = protocolHandler.onTransportConnected

  override def onTransportDisconnected() = protocolHandler.onTransportDisconnected

  override def onTransportCommand(command: Object) = {
    try {
      protocolHandler.onTransportCommand(command);
    } catch {
      case e:Exception =>
        onFailure(e)
    }
  }

  override def onTransportFailure(error: IOException) = protocolHandler.onTransportFailure(error)

  override def onRefill = {
    super.onRefill
    protocolHandler.onRefill
  }
}

/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
class ProtocolException(message:String, e:Throwable=null) extends Exception(message, e)


/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
trait ConsumerContext { // extends ClientContext, Subscription<MessageDelivery>, IFlowSink<MessageDelivery> {

    def getConsumerId() : String

    def getDestination(): Destination

    def getSelector() : String

    def getSelectorExpression() : BooleanExpression

    def isDurable() : Boolean

    def getSubscriptionName() : String

    /**
     * If the destination does not exist, should it automatically be
     * created?
     *
     * @return
     */
    def autoCreateDestination():Boolean

    def isPersistent() : Boolean

}

