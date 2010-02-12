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
import org.apache.activemq.amqp.protocol.types.AmqpSequenceNo;
import org.apache.activemq.amqp.protocol.types.AmqpUint;
import org.apache.activemq.util.buffer.Buffer;

/**
 * Represents a 32-bit RFC-1982 serial number
 * <p>
 * A sequence-no encodes a serial number as defined in RFC-1982. The arithmetic, and
 * operators for these numbers are defined by RFC-1982.
 * </p>
 */
public interface AmqpSequenceNo extends AmqpUint {


    public static class AmqpSequenceNoBean implements AmqpSequenceNo{

        private AmqpSequenceNoBuffer buffer;
        private AmqpSequenceNoBean bean = this;
        private Long value;

        AmqpSequenceNoBean(Long value) {
            this.value = value;
        }

        AmqpSequenceNoBean(AmqpSequenceNo.AmqpSequenceNoBean other) {
            this.bean = other;
        }

        public final AmqpSequenceNoBean copy() {
            return bean;
        }

        public final AmqpSequenceNo.AmqpSequenceNoBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            if(buffer == null) {
                buffer = new AmqpSequenceNoBuffer(marshaller.encode(this));
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

            if(o == null || !(o instanceof AmqpSequenceNo)) {
                return false;
            }

            return equals((AmqpSequenceNo) o);
        }

        public boolean equals(AmqpSequenceNo b) {
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
                return AmqpSequenceNo.AmqpSequenceNoBean.class.hashCode();
            }
            return getValue().hashCode();
        }
    }

    public static class AmqpSequenceNoBuffer extends AmqpUint.AmqpUintBuffer implements AmqpSequenceNo{

        private AmqpSequenceNoBean bean;

        protected AmqpSequenceNoBuffer() {
            super();
        }

        protected AmqpSequenceNoBuffer(Encoded<Long> encoded) {
            super(encoded);
        }

        public Long getValue() {
            return bean().getValue();
        }

        public AmqpSequenceNo.AmqpSequenceNoBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpSequenceNo bean() {
            if(bean == null) {
                bean = new AmqpSequenceNo.AmqpSequenceNoBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public boolean equals(Object o){
            return bean().equals(o);
        }

        public boolean equals(AmqpSequenceNo o){
            return bean().equals(o);
        }

        public int hashCode() {
            return bean().hashCode();
        }

        public static AmqpSequenceNo.AmqpSequenceNoBuffer create(Encoded<Long> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpSequenceNo.AmqpSequenceNoBuffer(encoded);
        }

        public static AmqpSequenceNo.AmqpSequenceNoBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpUint(in));
        }

        public static AmqpSequenceNo.AmqpSequenceNoBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpUint(buffer, offset));
        }
    }
}