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
import org.apache.activemq.amqp.protocol.types.AmqpAddress;
import org.apache.activemq.amqp.protocol.types.AmqpBinary;
import org.apache.activemq.util.buffer.Buffer;

/**
 * Represents a name of the source or target for a Message
 * <p>
 * Specifies the name for a source or target to which Messages are to be transferred to or
 * from. Addresses are expected to be human readable, but are intentionally considered
 * opaque. The format of an address is not defined by this specification.
 * </p>
 */
public interface AmqpAddress extends AmqpBinary {


    public static class AmqpAddressBean implements AmqpAddress{

        private AmqpAddressBuffer buffer;
        private AmqpAddressBean bean = this;
        private Buffer value;

        protected AmqpAddressBean() {
        }

        public AmqpAddressBean(Buffer value) {
            this.value = value;
        }

        public AmqpAddressBean(AmqpAddress.AmqpAddressBean other) {
            this.bean = other;
        }

        public final AmqpAddressBean copy() {
            return bean;
        }

        public final AmqpAddress.AmqpAddressBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            if(buffer == null) {
                buffer = new AmqpAddressBuffer(marshaller.encode(this));
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

            if(o == null || !(o instanceof AmqpAddress)) {
                return false;
            }

            return equivalent((AmqpAddress) o);
        }

        public int hashCode() {
            if(getValue() == null) {
                return AmqpAddress.AmqpAddressBean.class.hashCode();
            }
            return getValue().hashCode();
        }

        public boolean equivalent(AmqpType<?,?> t){
            if(this == t) {
                return true;
            }

            if(t == null || !(t instanceof AmqpAddress)) {
                return false;
            }

            return equivalent((AmqpAddress) t);
        }

        public boolean equivalent(AmqpAddress b) {
            if(b == null) {
                return false;
            }

            if(b.getValue() == null ^ getValue() == null) {
                return false;
            }

            return b.getValue() == null || b.getValue().equals(getValue());
        }
    }

    public static class AmqpAddressBuffer extends AmqpBinary.AmqpBinaryBuffer implements AmqpAddress{

        private AmqpAddressBean bean;

        protected AmqpAddressBuffer() {
            super();
        }

        protected AmqpAddressBuffer(Encoded<Buffer> encoded) {
            super(encoded);
        }

        public Buffer getValue() {
            return bean().getValue();
        }

        public AmqpAddress.AmqpAddressBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpAddress bean() {
            if(bean == null) {
                bean = new AmqpAddress.AmqpAddressBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public boolean equivalent(AmqpType<?, ?> t) {
            return bean().equivalent(t);
        }

        public static AmqpAddress.AmqpAddressBuffer create(Encoded<Buffer> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpAddress.AmqpAddressBuffer(encoded);
        }

        public static AmqpAddress.AmqpAddressBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpBinary(in));
        }

        public static AmqpAddress.AmqpAddressBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpBinary(buffer, offset));
        }
    }
}