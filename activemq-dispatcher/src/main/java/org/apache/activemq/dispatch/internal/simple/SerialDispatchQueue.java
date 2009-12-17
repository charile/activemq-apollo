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

package org.apache.activemq.dispatch.internal.simple;

import java.util.concurrent.TimeUnit;

import org.apache.activemq.dispatch.DispatchOption;
import org.apache.activemq.dispatch.DispatchPriority;
import org.apache.activemq.dispatch.DispatchQueue;
import org.apache.activemq.dispatch.internal.AbstractSerialDispatchQueue;

/**
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public final class SerialDispatchQueue extends AbstractSerialDispatchQueue implements SimpleQueue {

    private final SimpleDispatcher dispatcher;
    private volatile boolean stickToThreadOnNextDispatch; 
    private volatile boolean stickToThreadOnNextDispatchRequest; 
    
    SerialDispatchQueue(SimpleDispatcher dispatcher, String label, DispatchOption...options) {
        super(label, options);
        this.dispatcher = dispatcher;
        if( getOptions().contains(DispatchOption.STICK_TO_DISPATCH_THREAD) ) {
            stickToThreadOnNextDispatch=true;
        }
    }

    @Override
    public void setTargetQueue(DispatchQueue targetQueue) {
        GlobalDispatchQueue global = ((SimpleQueue)targetQueue).isGlobalDispatchQueue(); 
        if( getOptions().contains(DispatchOption.STICK_TO_CALLER_THREAD) && global!=null ) {
            stickToThreadOnNextDispatchRequest=true;
        }
        super.setTargetQueue(targetQueue);
    }
    
    @Override
    public void dispatchAsync(Runnable runnable) {
        
        if( stickToThreadOnNextDispatchRequest ) {
            SimpleQueue current = SimpleDispatcher.CURRENT_QUEUE.get();
            if( current!=null ) {
                SimpleQueue parent;
                while( (parent = current.getTargetQueue()) !=null ) {
                    current = parent;
                }
                if( current.isThreadDispatchQueue()==null ) {
                    System.out.println("crap");
                }
                super.setTargetQueue(current);
                stickToThreadOnNextDispatchRequest=false;
            }
        }

        super.dispatchAsync(runnable);
    }
    
    public void run() {
        SimpleQueue current = SimpleDispatcher.CURRENT_QUEUE.get();
        SimpleDispatcher.CURRENT_QUEUE.set(this);
        
        try {
            if( stickToThreadOnNextDispatch ) {
                stickToThreadOnNextDispatch=false;
                GlobalDispatchQueue global = current.isGlobalDispatchQueue();
                if( global!=null ) {
                    setTargetQueue(global.getTargetQueue());
                }
            }
            
            DispatcherThread thread = DispatcherThread.currentDispatcherThread();
            dispatch(thread.executionCounter);
        } finally {
            SimpleDispatcher.CURRENT_QUEUE.set(current);
        }

    }
    
    public void dispatchAfter(Runnable runnable, long delay, TimeUnit unit) {
        dispatcher.timerThread.addRelative(runnable, this, delay, unit);
    }

    public DispatchPriority getPriority() {
        throw new UnsupportedOperationException();
    }

    public Runnable poll() {
        throw new UnsupportedOperationException();
    }

    public GlobalDispatchQueue isGlobalDispatchQueue() {
        return null;
    }

    public SerialDispatchQueue isSerialDispatchQueue() {
        return this;
    }

    public ThreadDispatchQueue isThreadDispatchQueue() {
        return null;
    }
    
    public SimpleQueue getTargetQueue() {
        return (SimpleQueue) targetQueue;
    }

}