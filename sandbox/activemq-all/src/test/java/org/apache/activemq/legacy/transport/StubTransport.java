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
package org.apache.activemq.legacy.transport;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.activemq.transport.BaseTransport;
import org.apache.activemq.util.ServiceStopper;
import org.apache.activemq.wireformat.WireFormat;

/**
 * 
 */
public class StubTransport extends BaseTransport {

    private Queue<Object> queue = new ConcurrentLinkedQueue<Object>();

    protected void doStop(ServiceStopper stopper) throws Exception {
    }

    protected void doStart() throws Exception {
    }

    public void oneway(Object command) throws IOException {
        queue.add(command);
    }

    public Queue<Object> getQueue() {
        return queue;
    }

    public String getRemoteAddress() {
        return null;
    }


	public WireFormat getWireformat() {
		// TODO Auto-generated method stub
		return null;
	}

}
