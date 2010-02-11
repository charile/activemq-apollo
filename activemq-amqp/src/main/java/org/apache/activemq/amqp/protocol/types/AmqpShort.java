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
 * Represents a integer in the range -(2^15) to 2^15 - 1
 */
public interface AmqpShort extends AmqpType<AmqpShort.AmqpShortBean, AmqpShort.AmqpShortBuffer> {


    public Short getValue();

    public static class AmqpShortBean implements AmqpShort{

        private AmqpShortBuffer buffer;
        private AmqpShortBean bean = this;
        private Short value;

        protected AmqpShortBean() {
        }

        public AmqpShortBean(Short value) {
            this.value = value;
        }

        public AmqpShortBean(AmqpShort.AmqpShortBean other) {
            this.bean = other;
        }

        public final AmqpShortBean copy() {
            return bean;
        }

        public final AmqpShort.AmqpShortBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            if(buffer == null) {
                buffer = new AmqpShortBuffer(marshaller.encode(this));
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

            if(o == null || !(o instanceof AmqpShort)) {
                return false;
            }

            return equivalent((AmqpShort) o);
        }

        public int hashCode() {
            if(getValue() == null) {
                return AmqpShort.AmqpShortBean.class.hashCode();
            }
            return getValue().hashCode();
        }

        public boolean equivalent(AmqpType<?,?> t){
            if(this == t) {
                return true;
            }

            if(t == null || !(t instanceof AmqpShort)) {
                return false;
            }

            return equivalent((AmqpShort) t);
        }

        public boolean equivalent(AmqpShort b) {
            if(b == null) {
                return false;
            }

            if(b.getValue() == null ^ getValue() == null) {
                return false;
            }

            return b.getValue() == null || b.getValue().equals(getValue());
        }
    }

    public static class AmqpShortBuffer implements AmqpShort, AmqpBuffer< Short> {

        private AmqpShortBean bean;
        protected Encoded<Short> encoded;

        protected AmqpShortBuffer() {
        }

        protected AmqpShortBuffer(Encoded<Short> encoded) {
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

        public AmqpShort.AmqpShortBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpShort bean() {
            if(bean == null) {
                bean = new AmqpShort.AmqpShortBean(encoded.getValue());
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

        public static AmqpShort.AmqpShortBuffer create(Encoded<Short> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpShort.AmqpShortBuffer(encoded);
        }

        public static AmqpShort.AmqpShortBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpShort(in));
        }

        public static AmqpShort.AmqpShortBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpShort(buffer, offset));
        }
    }
}