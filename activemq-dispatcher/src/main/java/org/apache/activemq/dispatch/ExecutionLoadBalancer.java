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


import org.apache.activemq.dispatch.IDispatcher.DispatchContext;
import org.apache.activemq.dispatch.PooledDispatcher.PooledDispatchContext;

public interface ExecutionLoadBalancer<D extends IDispatcher> {

    public interface ExecutionTracker<D extends IDispatcher> {
        
        /**
         * Should be called when a {@link DispatchContext#requestDispatch()} is called.
         * This assists the load balancer in determining relationships between {@link DispatchContext}s
         * @param caller The calling dispatcher
         * @param context The context from which the dispatch is requested.
         */
        public void onDispatchRequest(D caller, PooledDispatchContext<D> context);

        /**
         * Must be called by the dispatcher when a {@link DispatchContext} is closed.
         */
        public void close();
    }
    
    /**
     * Must be called by a dispatch thread when it starts
     * @param dispatcher The dispatcher
     */
    public void onDispatcherStarted(D dispatcher);

    /**
     * Must be called by a dispatch thread when it stops
     * @param dispatcher The dispatcher
     */
    public void onDispatcherStopped(D dispatcher);

    /**
     * Gets an {@link ExecutionTracker} for the dispatch context. 
     * @param context
     * @return
     */
    public ExecutionTracker<D> createExecutionTracker(PooledDispatchContext<D> context);

    /**
     * Starts execution tracking
     */
    public void start();

    /**
     * Stops execution tracking
     */
    public void stop();
}
