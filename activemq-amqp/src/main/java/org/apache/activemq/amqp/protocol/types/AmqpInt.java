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
import java.lang.Integer;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpMarshaller;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.util.buffer.Buffer;

/**
 * Represents a integer in the range -(2^31) to 2^31 - 1
 */
public interface AmqpInt extends AmqpType<AmqpInt.AmqpIntBean, AmqpInt.AmqpIntBuffer> {


    public Integer getValue();

    public static class AmqpIntBean implements AmqpInt{

        private AmqpIntBuffer buffer;
        private AmqpIntBean bean = this;
        private Integer value;

        AmqpIntBean(Integer value) {
            this.value = value;
        }

        AmqpIntBean(AmqpInt.AmqpIntBean other) {
            this.bean = other;
        }

        public final AmqpIntBean copy() {
            return bean;
        }

        public final AmqpInt.AmqpIntBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            if(buffer == null) {
                buffer = new AmqpIntBuffer(marshaller.encode(this));
            }
            return buffer;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            getBuffer(marshaller).marshal(out, marshaller);
        }


        public Integer getValue() {
            return bean.value;
        }


        public boolean equals(Object o){
            if(this == o) {
                return true;
            }

            if(o == null || !(o instanceof AmqpInt)) {
                return false;
            }

            return equals((AmqpInt) o);
        }

        public boolean equals(AmqpInt b) {
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
                return AmqpInt.AmqpIntBean.class.hashCode();
            }
            return getValue().hashCode();
        }
    }

    public static class AmqpIntBuffer implements AmqpInt, AmqpBuffer< Integer> {

        private AmqpIntBean bean;
        protected Encoded<Integer> encoded;

        protected AmqpIntBuffer() {
        }

        protected AmqpIntBuffer(Encoded<Integer> encoded) {
            this.encoded = encoded;
        }

        public final Encoded<Integer> getEncoded() throws AmqpEncodingError{
            return encoded;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            encoded.marshal(out);
        }

        public Integer getValue() {
            return bean().getValue();
        }

        public AmqpInt.AmqpIntBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpInt bean() {
            if(bean == null) {
                bean = new AmqpInt.AmqpIntBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public boolean equals(Object o){
            return bean().equals(o);
        }

        public boolean equals(AmqpInt o){
            return bean().equals(o);
        }

        public int hashCode() {
            return bean().hashCode();
        }

        public static AmqpInt.AmqpIntBuffer create(Encoded<Integer> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpInt.AmqpIntBuffer(encoded);
        }

        public static AmqpInt.AmqpIntBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpInt(in));
        }

        public static AmqpInt.AmqpIntBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpInt(buffer, offset));
        }
    }
}