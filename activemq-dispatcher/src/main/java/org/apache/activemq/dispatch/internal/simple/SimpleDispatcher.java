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

import java.nio.channels.SelectableChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.activemq.dispatch.DispatchQueue;
import org.apache.activemq.dispatch.DispatchPriority;
import org.apache.activemq.dispatch.Dispatcher;
import org.apache.activemq.dispatch.DispatchSource;
import org.apache.activemq.dispatch.DispatcherConfig;
import org.apache.activemq.dispatch.DispatchOption;
import org.apache.activemq.dispatch.internal.BaseRetained;
import org.apache.activemq.dispatch.internal.AbstractSerialDispatchQueue;

import static org.apache.activemq.dispatch.DispatchPriority.*;



/**
 * Implements a simple dispatch system.
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
final public class SimpleDispatcher extends BaseRetained implements Dispatcher {
        
    public final static ThreadLocal<SimpleQueue> CURRENT_QUEUE = new ThreadLocal<SimpleQueue>();

    final SerialDispatchQueue mainQueue = new SerialDispatchQueue(this, "main");
    final GlobalDispatchQueue globalQueues[]; 
    final DispatcherThread dispatchers[];
    final AtomicLong globalQueuedRunnables = new AtomicLong();
    
    final ConcurrentLinkedQueue<DispatcherThread> waitingDispatchers = new ConcurrentLinkedQueue<DispatcherThread>();
    final AtomicInteger waitingDispatcherCount = new AtomicInteger();
    private final String label;
    TimerThread timerThread;
    
    public SimpleDispatcher(DispatcherConfig config) {
        this.label = config.getLabel();
        globalQueues = new GlobalDispatchQueue[3];
        for (int i = 0; i < 3; i++) {
            globalQueues[i] = new GlobalDispatchQueue(this, DispatchPriority.values()[i] );
        }
        dispatchers = new DispatcherThread[config.getThreads()];
    }

    public DispatchQueue getMainQueue() {
        return mainQueue;
    }
    
    public DispatchQueue getGlobalQueue() {
        return getGlobalQueue(DEFAULT);
    }

    public DispatchQueue getGlobalQueue(DispatchPriority priority) {
        return globalQueues[priority.ordinal()];
    }
    
    public DispatchQueue createSerialQueue(String label, DispatchOption... options) {
        AbstractSerialDispatchQueue rc = new SerialDispatchQueue(this, label, options);
        rc.setTargetQueue(getGlobalQueue());
        return rc;
    }
    
    public void dispatchMain() {
        mainQueue.run();
    }

    public DispatchSource createSource(SelectableChannel channel, int interestOps, DispatchQueue queue) {
        return null;
    }

    public void addWaitingDispatcher(DispatcherThread dispatcher) {
        waitingDispatcherCount.incrementAndGet();
        waitingDispatchers.add(dispatcher);
    }
    
    public void wakeup() {
        int value = waitingDispatcherCount.get();
        if( value!=0 ) {
            DispatcherThread dispatcher = waitingDispatchers.poll();
            if( dispatcher!=null ) {
                waitingDispatcherCount.decrementAndGet();
                dispatcher.globalWakeup();
            }
        }
    }

    protected void startup() {
        for (int i = 0; i < dispatchers.length; i++) {
            dispatchers[i] = new DispatcherThread(this, i);
            dispatchers[i].start();
        }
        timerThread = new TimerThread(this);
        timerThread.start();
    }

    public void shutdown() {
        
        Runnable countDown = new Runnable() {
            AtomicInteger shutdownCountDown = new AtomicInteger(dispatchers.length);
            public void run() {
                if( shutdownCountDown.decrementAndGet()==0 ) {
                    // Notify any registered shutdown watchers.
                    SimpleDispatcher.super.shutdown();
                }
                throw new DispatcherThread.Shutdown();
            }
        };

        timerThread.shutdown(null);
        for (int i = 0; i < dispatchers.length; i++) {
            ThreadDispatchQueue queue = dispatchers[i].threadQueues[LOW.ordinal()];
            queue.dispatchAsync(countDown);
        }
    }

    public String getLabel() {
        return label;
    }

    public DispatchQueue getCurrentQueue() {
        return CURRENT_QUEUE.get();
    }
    
    public DispatchQueue getCurrentThreadQueue() {
        DispatcherThread thread = DispatcherThread.currentDispatcherThread();
        if( thread == null ) {
            return null;
        }
        return thread.currentThreadQueue;
    }

}
