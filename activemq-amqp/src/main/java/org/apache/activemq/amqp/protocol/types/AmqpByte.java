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
import java.lang.Byte;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpMarshaller;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.util.buffer.Buffer;

/**
 * Represents a integer in the range -(2^7) to 2^7 - 1
 */
public interface AmqpByte extends AmqpType<AmqpByte.AmqpByteBean, AmqpByte.AmqpByteBuffer> {


    public Byte getValue();

    public static class AmqpByteBean implements AmqpByte{

        private AmqpByteBuffer buffer;
        private AmqpByteBean bean = this;
        private Byte value;

        protected AmqpByteBean() {
        }

        public AmqpByteBean(Byte value) {
            this.value = value;
        }

        public AmqpByteBean(AmqpByte.AmqpByteBean other) {
            this.bean = other;
        }

        public final AmqpByteBean copy() {
            return bean;
        }

        public final AmqpByte.AmqpByteBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            if(buffer == null) {
                buffer = new AmqpByteBuffer(marshaller.encode(this));
            }
            return buffer;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            getBuffer(marshaller).marshal(out, marshaller);
        }


        public Byte getValue() {
            return bean.value;
        }


        public boolean equals(Object o){
            if(this == o) {
                return true;
            }

            if(o == null || !(o instanceof AmqpByte)) {
                return false;
            }

            return equivalent((AmqpByte) o);
        }

        public int hashCode() {
            if(getValue() == null) {
                return AmqpByte.AmqpByteBean.class.hashCode();
            }
            return getValue().hashCode();
        }

        public boolean equivalent(AmqpType<?,?> t){
            if(this == t) {
                return true;
            }

            if(t == null || !(t instanceof AmqpByte)) {
                return false;
            }

            return equivalent((AmqpByte) t);
        }

        public boolean equivalent(AmqpByte b) {
            if(b == null) {
                return false;
            }

            if(b.getValue() == null ^ getValue() == null) {
                return false;
            }

            return b.getValue() == null || b.getValue().equals(getValue());
        }
    }

    public static class AmqpByteBuffer implements AmqpByte, AmqpBuffer< Byte> {

        private AmqpByteBean bean;
        protected Encoded<Byte> encoded;

        protected AmqpByteBuffer() {
        }

        protected AmqpByteBuffer(Encoded<Byte> encoded) {
            this.encoded = encoded;
        }

        public final Encoded<Byte> getEncoded() throws AmqpEncodingError{
            return encoded;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            encoded.marshal(out);
        }

        public Byte getValue() {
            return bean().getValue();
        }

        public AmqpByte.AmqpByteBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpByte bean() {
            if(bean == null) {
                bean = new AmqpByte.AmqpByteBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public boolean equals(Object o){
            return bean().equals(o);
        }

        public int hashCode() {
            return bean().hashCode();
        }

        public boolean equivalent(AmqpType<?, ?> t) {
            return bean().equivalent(t);
        }

        public static AmqpByte.AmqpByteBuffer create(Encoded<Byte> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpByte.AmqpByteBuffer(encoded);
        }

        public static AmqpByte.AmqpByteBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpByte(in));
        }

        public static AmqpByte.AmqpByteBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpByte(buffer, offset));
        }
    }
}