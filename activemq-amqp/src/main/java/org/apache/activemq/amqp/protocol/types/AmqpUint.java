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
import java.lang.Long;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpMarshaller;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.util.buffer.Buffer;

/**
 * Represents a integer in the range 0 to 2^32 - 1
 */
public interface AmqpUint extends AmqpType<AmqpUint.AmqpUintBean, AmqpUint.AmqpUintBuffer> {


    public Long getValue();

    public static class AmqpUintBean implements AmqpUint{

        private AmqpUintBuffer buffer;
        private AmqpUintBean bean = this;
        private Long value;

        protected AmqpUintBean() {
        }

        public AmqpUintBean(Long value) {
            this.value = value;
        }

        public AmqpUintBean(AmqpUint.AmqpUintBean other) {
            this.bean = other;
        }

        public final AmqpUintBean copy() {
            return bean;
        }

        public final AmqpUint.AmqpUintBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            if(buffer == null) {
                buffer = new AmqpUintBuffer(marshaller.encode(this));
            }
            return buffer;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            getBuffer(marshaller).marshal(out, marshaller);
        }


        public Long getValue() {
            return bean.value;
        }


        public boolean equals(Object o){
            if(this == o) {
                return true;
            }

            if(o == null || !(o instanceof AmqpUint)) {
                return false;
            }

            return equivalent((AmqpUint) o);
        }

        public int hashCode() {
            if(getValue() == null) {
                return AmqpUint.AmqpUintBean.class.hashCode();
            }
            return getValue().hashCode();
        }

        public boolean equivalent(AmqpType<?,?> t){
            if(this == t) {
                return true;
            }

            if(t == null || !(t instanceof AmqpUint)) {
                return false;
            }

            return equivalent((AmqpUint) t);
        }

        public boolean equivalent(AmqpUint b) {
            if(b == null) {
                return false;
            }

            if(b.getValue() == null ^ getValue() == null) {
                return false;
            }

            return b.getValue() == null || b.getValue().equals(getValue());
        }
    }

    public static class AmqpUintBuffer implements AmqpUint, AmqpBuffer< Long> {

        private AmqpUintBean bean;
        protected Encoded<Long> encoded;

        protected AmqpUintBuffer() {
        }

        protected AmqpUintBuffer(Encoded<Long> encoded) {
            this.encoded = encoded;
        }

        public final Encoded<Long> getEncoded() throws AmqpEncodingError{
            return encoded;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            encoded.marshal(out);
        }

        public Long getValue() {
            return bean().getValue();
        }

        public AmqpUint.AmqpUintBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpUint bean() {
            if(bean == null) {
                bean = new AmqpUint.AmqpUintBean(encoded.getValue());
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

        public static AmqpUint.AmqpUintBuffer create(Encoded<Long> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpUint.AmqpUintBuffer(encoded);
        }

        public static AmqpUint.AmqpUintBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpUint(in));
        }

        public static AmqpUint.AmqpUintBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpUint(buffer, offset));
        }
    }
}