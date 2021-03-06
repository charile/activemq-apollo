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
import java.lang.Boolean;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpMarshaller;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.fusesource.hawtbuf.buffer.Buffer;

/**
 * Represents a represents a true or false value
 */
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//
//!!!!!!!!THIS CLASS IS AUTOGENERATED DO NOT MODIFY DIRECTLY!!!!!!!!!!!!//
//!!!!!!Instead, modify the generator in activemq-amqp-generator!!!!!!!!//
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//
public interface AmqpBoolean extends AmqpType<AmqpBoolean.AmqpBooleanBean, AmqpBoolean.AmqpBooleanBuffer> {


    public Boolean getValue();

    public static class AmqpBooleanBean implements AmqpBoolean{

        private AmqpBooleanBuffer buffer;
        private AmqpBooleanBean bean = this;
        private Boolean value;

        AmqpBooleanBean(Boolean value) {
            this.value = value;
        }

        AmqpBooleanBean(AmqpBoolean.AmqpBooleanBean other) {
            this.bean = other;
        }

        public final AmqpBooleanBean copy() {
            return bean;
        }

        public final AmqpBoolean.AmqpBooleanBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            if(buffer == null) {
                buffer = new AmqpBooleanBuffer(marshaller.encode(this));
            }
            return buffer;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            getBuffer(marshaller).marshal(out, marshaller);
        }


        public Boolean getValue() {
            return bean.value;
        }


        public boolean equals(Object o){
            if(this == o) {
                return true;
            }

            if(o == null || !(o instanceof AmqpBoolean)) {
                return false;
            }

            return equals((AmqpBoolean) o);
        }

        public boolean equals(AmqpBoolean b) {
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
                return AmqpBoolean.AmqpBooleanBean.class.hashCode();
            }
            return getValue().hashCode();
        }
    }

    public static class AmqpBooleanBuffer implements AmqpBoolean, AmqpBuffer< Boolean> {

        private AmqpBooleanBean bean;
        protected Encoded<Boolean> encoded;

        protected AmqpBooleanBuffer() {
        }

        protected AmqpBooleanBuffer(Encoded<Boolean> encoded) {
            this.encoded = encoded;
        }

        public final Encoded<Boolean> getEncoded() throws AmqpEncodingError{
            return encoded;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            encoded.marshal(out);
        }

        public Boolean getValue() {
            return bean().getValue();
        }

        public AmqpBoolean.AmqpBooleanBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpBoolean bean() {
            if(bean == null) {
                bean = new AmqpBoolean.AmqpBooleanBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public boolean equals(Object o){
            return bean().equals(o);
        }

        public boolean equals(AmqpBoolean o){
            return bean().equals(o);
        }

        public int hashCode() {
            return bean().hashCode();
        }

        public static AmqpBoolean.AmqpBooleanBuffer create(Encoded<Boolean> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpBoolean.AmqpBooleanBuffer(encoded);
        }

        public static AmqpBoolean.AmqpBooleanBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpBoolean(in));
        }

        public static AmqpBoolean.AmqpBooleanBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpBoolean(buffer, offset));
        }
    }
}