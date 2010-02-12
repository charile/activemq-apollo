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
import java.math.BigInteger;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpMarshaller;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.util.buffer.Buffer;

/**
 * Represents a integer in the range 0 to 2^64 - 1
 */
public interface AmqpUlong extends AmqpType<AmqpUlong.AmqpUlongBean, AmqpUlong.AmqpUlongBuffer> {


    public BigInteger getValue();

    public static class AmqpUlongBean implements AmqpUlong{

        private AmqpUlongBuffer buffer;
        private AmqpUlongBean bean = this;
        private BigInteger value;

        AmqpUlongBean(BigInteger value) {
            this.value = value;
        }

        AmqpUlongBean(AmqpUlong.AmqpUlongBean other) {
            this.bean = other;
        }

        public final AmqpUlongBean copy() {
            return bean;
        }

        public final AmqpUlong.AmqpUlongBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            if(buffer == null) {
                buffer = new AmqpUlongBuffer(marshaller.encode(this));
            }
            return buffer;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            getBuffer(marshaller).marshal(out, marshaller);
        }


        public BigInteger getValue() {
            return bean.value;
        }


        public boolean equals(Object o){
            if(this == o) {
                return true;
            }

            if(o == null || !(o instanceof AmqpUlong)) {
                return false;
            }

            return equals((AmqpUlong) o);
        }

        public boolean equals(AmqpUlong b) {
            if(b == null) {
                return false;
            }

            if(b.getValue() == null ^ getValue() == null) {
                return false;
            }

            return b.getValue() == null || b.getValue().equals(getValue());
        }

        public int hashCode() {
            if(getValue() == null) {
                return AmqpUlong.AmqpUlongBean.class.hashCode();
            }
            return getValue().hashCode();
        }
    }

    public static class AmqpUlongBuffer implements AmqpUlong, AmqpBuffer< BigInteger> {

        private AmqpUlongBean bean;
        protected Encoded<BigInteger> encoded;

        protected AmqpUlongBuffer() {
        }

        protected AmqpUlongBuffer(Encoded<BigInteger> encoded) {
            this.encoded = encoded;
        }

        public final Encoded<BigInteger> getEncoded() throws AmqpEncodingError{
            return encoded;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            encoded.marshal(out);
        }

        public BigInteger getValue() {
            return bean().getValue();
        }

        public AmqpUlong.AmqpUlongBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpUlong bean() {
            if(bean == null) {
                bean = new AmqpUlong.AmqpUlongBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public boolean equals(Object o){
            return bean().equals(o);
        }

        public boolean equals(AmqpUlong o){
            return bean().equals(o);
        }

        public int hashCode() {
            return bean().hashCode();
        }

        public static AmqpUlong.AmqpUlongBuffer create(Encoded<BigInteger> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpUlong.AmqpUlongBuffer(encoded);
        }

        public static AmqpUlong.AmqpUlongBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpUlong(in));
        }

        public static AmqpUlong.AmqpUlongBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpUlong(buffer, offset));
        }
    }
}