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

import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.activemq.dispatch.DispatchOption;
import org.apache.activemq.dispatch.DispatchPriority;
import org.apache.activemq.dispatch.DispatchQueue;
import org.apache.activemq.dispatch.internal.QueueSupport;

/**
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
final public class ThreadDispatchQueue implements SimpleQueue {

    final String label;
    final LinkedList<Runnable> localRunnables = new LinkedList<Runnable>();
    final ConcurrentLinkedQueue<Runnable> globalRunnables = new ConcurrentLinkedQueue<Runnable>();
    final DispatcherThread dispatcher;
    final AtomicLong counter;
    final GlobalDispatchQueue globalQueue;
    
    public ThreadDispatchQueue(DispatcherThread dispatcher, GlobalDispatchQueue globalQueue) {
        this.dispatcher = dispatcher;
        this.globalQueue = globalQueue;
        this.label="thread local "+globalQueue.getLabel();
        this.counter = dispatcher.threadQueuedRunnables;
    }

    public String getLabel() {
        return label;
    }

    public void execute(Runnable runnable) {
        dispatchAsync(runnable);
    }
    
    public void dispatchAsync(Runnable runnable) {
        // We don't have to take the synchronization hit 
        // if the current thread is the dispatcher since we know it's not
        // waiting.
        if( Thread.currentThread()!=dispatcher ) {
            globalEnqueue(runnable);
        } else {
            localEnqueue(runnable);
        }
    }

    void localEnqueue(Runnable runnable) {
        localRunnables.add(runnable);
    }

    void globalEnqueue(Runnable runnable) {
        counter.incrementAndGet();
        globalRunnables.add(runnable);
        dispatcher.wakeup();
    }

    public Runnable pollShared() {
        Runnable rc = globalRunnables.poll();
        if( rc !=null ) {
            counter.decrementAndGet();
        }
        return rc;
    }

    public Runnable pollLocal() {
        return localRunnables.poll();
    }

    public void dispatchAfter(Runnable runnable, long delay, TimeUnit unit) {
        throw new RuntimeException("TODO: implement me.");
    }

    public void dispatchSync(final Runnable runnable) throws InterruptedException {
        dispatchApply(1, runnable);
    }
    
    public void dispatchApply(int iterations, final Runnable runnable) throws InterruptedException {
        QueueSupport.dispatchApply(this, iterations, runnable);
    }

    public void resume() {
        throw new UnsupportedOperationException();
    }

    public void suspend() {
        throw new UnsupportedOperationException();
    }

    public <Context> Context getContext() {
        throw new UnsupportedOperationException();
    }

    public <Context> void setContext(Context context) {
        throw new UnsupportedOperationException();
    }

    public void addShutdownWatcher(Runnable finalizer) {
        throw new UnsupportedOperationException();
    }

    public void setTargetQueue(DispatchQueue queue) {
        throw new UnsupportedOperationException();
    }
    
    public SimpleQueue getTargetQueue() {
        return null;
    }
    
    public DispatchPriority getPriority() {
        return globalQueue.getPriority();
    }

    public void release() {
    }

    public void retain() {
    }

    public Set<DispatchOption> getOptions() {
        return Collections.emptySet();
    }

    public GlobalDispatchQueue isGlobalDispatchQueue() {
        return null;
    }

    public SerialDispatchQueue isSerialDispatchQueue() {
        return null;
    }

    public ThreadDispatchQueue isThreadDispatchQueue() {
        return this;
    }

}
