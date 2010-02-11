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
package org.apache.activemq.amqp.protocol.types;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpMarshaller;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.amqp.protocol.types.AmqpBinary;
import org.apache.activemq.amqp.protocol.types.AmqpSessionName;
import org.apache.activemq.util.buffer.Buffer;

/**
 * Represents a opaque Session name
 * <p>
 * The Session name uniquely identifies an interaction between two peers. It is globally
 * unique among all open Sessions. Once a Session is cleanly closed, its name may be reused.
 * </p>
 */
public interface AmqpSessionName extends AmqpBinary {


    public static class AmqpSessionNameBean implements AmqpSessionName{

        private AmqpSessionNameBuffer buffer;
        private AmqpSessionNameBean bean = this;
        private Buffer value;

        protected AmqpSessionNameBean() {
        }

        public AmqpSessionNameBean(Buffer value) {
            this.value = value;
        }

        public AmqpSessionNameBean(AmqpSessionName.AmqpSessionNameBean other) {
            this.bean = other;
        }

        public final AmqpSessionNameBean copy() {
            return bean;
        }

        public final AmqpSessionName.AmqpSessionNameBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            if(buffer == null) {
                buffer = new AmqpSessionNameBuffer(marshaller.encode(this));
            }
            return buffer;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            getBuffer(marshaller).marshal(out, marshaller);
        }


        public Buffer getValue() {
            return bean.value;
        }


        public boolean equals(Object o){
            if(this == o) {
                return true;
            }

            if(o == null || !(o instanceof AmqpSessionName)) {
                return false;
            }

            return equivalent((AmqpSessionName) o);
        }

        public int hashCode() {
            if(getValue() == null) {
                return AmqpSessionName.AmqpSessionNameBean.class.hashCode();
            }
            return getValue().hashCode();
        }

        public boolean equivalent(AmqpType<?,?> t){
            if(this == t) {
                return true;
            }

            if(t == null || !(t instanceof AmqpSessionName)) {
                return false;
            }

            return equivalent((AmqpSessionName) t);
        }

        public boolean equivalent(AmqpSessionName b) {
            if(b == null) {
                return false;
            }

            if(b.getValue() == null ^ getValue() == null) {
                return false;
            }

            return b.getValue() == null || b.getValue().equals(getValue());
        }
    }

    public static class AmqpSessionNameBuffer extends AmqpBinary.AmqpBinaryBuffer implements AmqpSessionName{

        private AmqpSessionNameBean bean;

        protected AmqpSessionNameBuffer() {
            super();
        }

        protected AmqpSessionNameBuffer(Encoded<Buffer> encoded) {
            super(encoded);
        }

        public Buffer getValue() {
            return bean().getValue();
        }

        public AmqpSessionName.AmqpSessionNameBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpSessionName bean() {
            if(bean == null) {
                bean = new AmqpSessionName.AmqpSessionNameBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public boolean equivalent(AmqpType<?, ?> t) {
            return bean().equivalent(t);
        }

        public static AmqpSessionName.AmqpSessionNameBuffer create(Encoded<Buffer> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpSessionName.AmqpSessionNameBuffer(encoded);
        }

        public static AmqpSessionName.AmqpSessionNameBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpBinary(in));
        }

        public static AmqpSessionName.AmqpSessionNameBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpBinary(buffer, offset));
        }
    }
}