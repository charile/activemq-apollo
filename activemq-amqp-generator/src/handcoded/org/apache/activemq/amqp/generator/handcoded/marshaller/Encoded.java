/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * his work for additional information regarding copyright ownership.
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
package org.apache.activemq.amqp.generator.handcoded.marshaller;

import java.io.DataOutput;
import java.io.DataInput;
import java.io.IOException;

import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.util.buffer.Buffer;

public interface Encoded<E> extends Encoding {

    public int getEncodedSize() throws AmqpEncodingError;

    public int getDataSize() throws AmqpEncodingError;

    public int getDataCount() throws AmqpEncodingError;

    public E getValue() throws AmqpEncodingError;
    
    public Buffer getBuffer() throws AmqpEncodingError;

    public void encode(Buffer target, int offset) throws AmqpEncodingError;
    
    public void marshal(DataOutput out) throws IOException;

    public void marshalData(DataOutput out) throws IOException;

    public void marshalConstructor(DataOutput out) throws IOException;
}
