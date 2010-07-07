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
package org.apache.hawtdb.internal.index;

import org.apache.activemq.util.buffer.Buffer;
import org.apache.activemq.util.marshaller.FixedBufferMarshaller;
import org.apache.activemq.util.marshaller.LongMarshaller;
import org.apache.hawtdb.api.HashIndexFactory;
import org.apache.hawtdb.api.Index;
import org.apache.hawtdb.api.Transaction;


/**
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class HashIndexBenchmark extends IndexBenchmark {

    public HashIndexBenchmark() {
        this.benchmark.setSamples(5);
    }
    
    protected Index<Long, Buffer> createIndex(Transaction tx) {
        HashIndexFactory<Long, Buffer> factory = new HashIndexFactory<Long, Buffer>();
        factory.setKeyMarshaller(LongMarshaller.INSTANCE);
        factory.setValueMarshaller(new FixedBufferMarshaller(DATA.length));
        factory.setFixedCapacity(500);
        return factory.create(tx, tx.allocator().alloc(1));
    }
    
}
