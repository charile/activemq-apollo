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
package org.apache.activemq.dispatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.activemq.dispatch.PooledDispatcher.PooledDispatchContext;

public class SimpleLoadBalancer<D extends IDispatcher> implements ExecutionLoadBalancer<D> {

    private final boolean DEBUG = false;

    //TODO: Added plumbing for periodic rebalancing which we should
    //consider implementing
    private static final boolean ENABLE_UPDATES = false;
    private final ArrayList<D> dispatchers = new ArrayList<D>();

    private AtomicBoolean running = new AtomicBoolean(false);
    private boolean needsUpdate = false;
    private static final int FREQUENCY = 5000;
    private boolean scheduled = false;
    private final Runnable timerCallback;

    public SimpleLoadBalancer() {

        timerCallback = new Runnable() {
            public final void run() {
                if (running.get()) {
                    rebalance();
                    synchronized (dispatchers) {
                        scheduled = false;
                        scheduleNext();
                    }
                }
            }
        };

    }

    private void rebalance() {
        if (!needsUpdate) {
            return;
        }
        // TODO Auto-generated method stub
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            scheduleNext();
        }
    }

    private void scheduleNext() {
        if (!ENABLE_UPDATES) {
            return;
        }
        synchronized (dispatchers) {
            if (!scheduled) {
                if (!dispatchers.isEmpty()) {
                    dispatchers.get(0).schedule(timerCallback, FREQUENCY, TimeUnit.MILLISECONDS);
                    scheduled = true;
                }
            }
        }
    }

    public void stop() {
        running.compareAndSet(true, false);
    }

    public synchronized final void onDispatcherStarted(D dispatcher) {
        dispatchers.add(dispatcher);
        scheduleNext();
    }

    /**
     * A Dispatcher must call this when exiting it's dispatch loop
     */
    public void onDispatcherStopped(D dispatcher) {
        dispatchers.remove(dispatcher);
    }

    public ExecutionTracker<D> createExecutionTracker(PooledDispatchContext<D> context) {
        return new SimpleExecutionTracker(context);
    }

    private static class ExecutionStats<D extends IDispatcher> {
        final PooledDispatchContext<D> target;
        final PooledDispatchContext<D> source;
        int count;

        ExecutionStats(PooledDispatchContext<D> source, PooledDispatchContext<D> target) {
            this.target = target;
            this.source = source;
        }

        public String toString() {
            return "Connection from: " + source + " to " + target;
        }
    }

    private class SimpleExecutionTracker implements ExecutionTracker<D> {
        private final HashMap<PooledDispatchContext<D>, ExecutionStats<D>> sources = new HashMap<PooledDispatchContext<D>, ExecutionStats<D>>();
        private final PooledDispatchContext<D> context;
        private final AtomicInteger work = new AtomicInteger(0);

        private PooledDispatchContext<D> singleSource;
        private IDispatcher currentOwner;

        SimpleExecutionTracker(PooledDispatchContext<D> context) {
            this.context = context;
            currentOwner = context.getDispatcher();
        }

        /**
         * This method is called to track which dispatch contexts are requesting
         * dispatch for the target context represented by this node.
         * 
         * This method is not threadsafe, the caller must ensure serialized
         * access to this method.
         * 
         * @param callngDispatcher
         *            The calling dispatcher.
         * @param context
         *            the originating dispatch context
         * @return True if this method resulted in the dispatch request being
         *         assigned to another dispatcher.
         */
        public void onDispatchRequest(D callingDispatcher, PooledDispatchContext<D> callingContext) {

            if (callingContext != null) {
                // Make sure we are being called by another node:
                if (callingContext == null || callingContext == context) {
                    return;
                }

                // Optimize for single source case:
                if (singleSource != callingContext) {
                    if (singleSource == null && sources.isEmpty()) {
                        singleSource = callingContext;
                        ExecutionStats<D> stats = new ExecutionStats<D>(callingContext, context);
                        stats.count++;
                        sources.put(callingContext, stats);

                        // If this context only has a single source
                        // assign it to that source to minimize contention:
                        if (callingDispatcher != currentOwner) {
                            if (DEBUG)
                                System.out.println("Assigning: " + context + " to " + callingContext + "'s  dispatcher: " + callingDispatcher + " From: " + currentOwner);

                            currentOwner = callingDispatcher;
                            context.assignToNewDispatcher(callingDispatcher);
                        }

                    } else {

                        ExecutionStats<D> stats = sources.get(callingContext);
                        if (stats == null) {
                            stats = new ExecutionStats<D>(callingContext, context);
                            sources.put(callingContext, stats);
                        }
                        stats.count++;

                        if (singleSource != null) {
                            singleSource = null;
                        }
                    }
                }
                work.incrementAndGet();
            }
        }

        public void close() {
        }
    }
}
