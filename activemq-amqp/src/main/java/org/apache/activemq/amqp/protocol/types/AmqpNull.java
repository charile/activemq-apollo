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
import java.lang.Object;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpMarshaller;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.util.buffer.Buffer;

/**
 * Represents a indicates an empty value
 */
public interface AmqpNull extends AmqpType<AmqpNull.AmqpNullBean, AmqpNull.AmqpNullBuffer> {


    public Object getValue();

    public static class AmqpNullBean implements AmqpNull{

        private AmqpNullBuffer buffer;
        private AmqpNullBean bean = this;
        private Object value;

        AmqpNullBean(Object value) {
            this.value = value;
        }

        AmqpNullBean(AmqpNull.AmqpNullBean other) {
            this.bean = other;
        }

        public final AmqpNullBean copy() {
            return bean;
        }

        public final AmqpNull.AmqpNullBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            if(buffer == null) {
                buffer = new AmqpNullBuffer(marshaller.encode(this));
            }
            return buffer;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            getBuffer(marshaller).marshal(out, marshaller);
        }


        public Object getValue() {
            return bean.value;
        }


        public boolean equals(Object o){
            if(this == o) {
                return true;
            }

            if(o == null || !(o instanceof AmqpNull)) {
                return false;
            }

            return equals((AmqpNull) o);
        }

        public boolean equals(AmqpNull b) {
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
                return AmqpNull.AmqpNullBean.class.hashCode();
            }
            return getValue().hashCode();
        }
    }

    public static class AmqpNullBuffer implements AmqpNull, AmqpBuffer< Object> {

        private AmqpNullBean bean;
        protected Encoded<Object> encoded;

        protected AmqpNullBuffer() {
        }

        protected AmqpNullBuffer(Encoded<Object> encoded) {
            this.encoded = encoded;
        }

        public final Encoded<Object> getEncoded() throws AmqpEncodingError{
            return encoded;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            encoded.marshal(out);
        }

        public Object getValue() {
            return bean().getValue();
        }

        public AmqpNull.AmqpNullBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpNull bean() {
            if(bean == null) {
                bean = new AmqpNull.AmqpNullBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public boolean equals(Object o){
            return bean().equals(o);
        }

        public boolean equals(AmqpNull o){
            return bean().equals(o);
        }

        public int hashCode() {
            return bean().hashCode();
        }

        public static AmqpNull.AmqpNullBuffer create(Encoded<Object> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpNull.AmqpNullBuffer(encoded);
        }

        public static AmqpNull.AmqpNullBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpNull(in));
        }

        public static AmqpNull.AmqpNullBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpNull(buffer, offset));
        }
    }
}