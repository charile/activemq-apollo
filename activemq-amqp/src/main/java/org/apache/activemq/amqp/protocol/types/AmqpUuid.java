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
import java.util.UUID;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpMarshaller;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.util.buffer.Buffer;

/**
 * Represents a a universally unique id as defined by RFC-4122 section 4.1.2
 */
public interface AmqpUuid extends AmqpType<AmqpUuid.AmqpUuidBean, AmqpUuid.AmqpUuidBuffer> {


    public UUID getValue();

    public static class AmqpUuidBean implements AmqpUuid{

        private AmqpUuidBuffer buffer;
        private AmqpUuidBean bean = this;
        private UUID value;

        protected AmqpUuidBean() {
        }

        public AmqpUuidBean(UUID value) {
            this.value = value;
        }

        public AmqpUuidBean(AmqpUuid.AmqpUuidBean other) {
            this.bean = other;
        }

        public final AmqpUuidBean copy() {
            return bean;
        }

        public final AmqpUuid.AmqpUuidBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            if(buffer == null) {
                buffer = new AmqpUuidBuffer(marshaller.encode(this));
            }
            return buffer;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            getBuffer(marshaller).marshal(out, marshaller);
        }


        public UUID getValue() {
            return bean.value;
        }


        public boolean equals(Object o){
            if(this == o) {
                return true;
            }

            if(o == null || !(o instanceof AmqpUuid)) {
                return false;
            }

            return equivalent((AmqpUuid) o);
        }

        public int hashCode() {
            if(getValue() == null) {
                return AmqpUuid.AmqpUuidBean.class.hashCode();
            }
            return getValue().hashCode();
        }

        public boolean equivalent(AmqpType<?,?> t){
            if(this == t) {
                return true;
            }

            if(t == null || !(t instanceof AmqpUuid)) {
                return false;
            }

            return equivalent((AmqpUuid) t);
        }

        public boolean equivalent(AmqpUuid b) {
            if(b == null) {
                return false;
            }

            if(b.getValue() == null ^ getValue() == null) {
                return false;
            }

            return b.getValue() == null || b.getValue().equals(getValue());
        }
    }

    public static class AmqpUuidBuffer implements AmqpUuid, AmqpBuffer< UUID> {

        private AmqpUuidBean bean;
        protected Encoded<UUID> encoded;

        protected AmqpUuidBuffer() {
        }

        protected AmqpUuidBuffer(Encoded<UUID> encoded) {
            this.encoded = encoded;
        }

        public final Encoded<UUID> getEncoded() throws AmqpEncodingError{
            return encoded;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            encoded.marshal(out);
        }

        public UUID getValue() {
            return bean().getValue();
        }

        public AmqpUuid.AmqpUuidBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpUuid bean() {
            if(bean == null) {
                bean = new AmqpUuid.AmqpUuidBean(encoded.getValue());
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

        public static AmqpUuid.AmqpUuidBuffer create(Encoded<UUID> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpUuid.AmqpUuidBuffer(encoded);
        }

        public static AmqpUuid.AmqpUuidBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpUuid(in));
        }

        public static AmqpUuid.AmqpUuidBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpUuid(buffer, offset));
        }
    }
}