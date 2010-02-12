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
import java.lang.Short;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpMarshaller;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.util.buffer.Buffer;

/**
 * Represents a integer in the range 0 to 2^8 - 1
 */
public interface AmqpUbyte extends AmqpType<AmqpUbyte.AmqpUbyteBean, AmqpUbyte.AmqpUbyteBuffer> {


    public Short getValue();

    public static class AmqpUbyteBean implements AmqpUbyte{

        private AmqpUbyteBuffer buffer;
        private AmqpUbyteBean bean = this;
        private Short value;

        AmqpUbyteBean(Short value) {
            this.value = value;
        }

        AmqpUbyteBean(AmqpUbyte.AmqpUbyteBean other) {
            this.bean = other;
        }

        public final AmqpUbyteBean copy() {
            return bean;
        }

        public final AmqpUbyte.AmqpUbyteBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            if(buffer == null) {
                buffer = new AmqpUbyteBuffer(marshaller.encode(this));
            }
            return buffer;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            getBuffer(marshaller).marshal(out, marshaller);
        }


        public Short getValue() {
            return bean.value;
        }


        public boolean equals(Object o){
            if(this == o) {
                return true;
            }

            if(o == null || !(o instanceof AmqpUbyte)) {
                return false;
            }

            return equals((AmqpUbyte) o);
        }

        public boolean equals(AmqpUbyte b) {
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
                return AmqpUbyte.AmqpUbyteBean.class.hashCode();
            }
            return getValue().hashCode();
        }
    }

    public static class AmqpUbyteBuffer implements AmqpUbyte, AmqpBuffer< Short> {

        private AmqpUbyteBean bean;
        protected Encoded<Short> encoded;

        protected AmqpUbyteBuffer() {
        }

        protected AmqpUbyteBuffer(Encoded<Short> encoded) {
            this.encoded = encoded;
        }

        public final Encoded<Short> getEncoded() throws AmqpEncodingError{
            return encoded;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            encoded.marshal(out);
        }

        public Short getValue() {
            return bean().getValue();
        }

        public AmqpUbyte.AmqpUbyteBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpUbyte bean() {
            if(bean == null) {
                bean = new AmqpUbyte.AmqpUbyteBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public boolean equals(Object o){
            return bean().equals(o);
        }

        public boolean equals(AmqpUbyte o){
            return bean().equals(o);
        }

        public int hashCode() {
            return bean().hashCode();
        }

        public static AmqpUbyte.AmqpUbyteBuffer create(Encoded<Short> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpUbyte.AmqpUbyteBuffer(encoded);
        }

        public static AmqpUbyte.AmqpUbyteBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpUbyte(in));
        }

        public static AmqpUbyte.AmqpUbyteBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpUbyte(buffer, offset));
        }
    }
}