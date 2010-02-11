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
import java.lang.String;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpMarshaller;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.util.buffer.Buffer;

/**
 * Represents a a sequence of unicode characters
 */
public interface AmqpString extends AmqpType<AmqpString.AmqpStringBean, AmqpString.AmqpStringBuffer> {


    public String getValue();

    public static class AmqpStringBean implements AmqpString{

        private AmqpStringBuffer buffer;
        private AmqpStringBean bean = this;
        private String value;

        protected AmqpStringBean() {
        }

        public AmqpStringBean(String value) {
            this.value = value;
        }

        public AmqpStringBean(AmqpString.AmqpStringBean other) {
            this.bean = other;
        }

        public final AmqpStringBean copy() {
            return bean;
        }

        public final AmqpString.AmqpStringBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            if(buffer == null) {
                buffer = new AmqpStringBuffer(marshaller.encode(this));
            }
            return buffer;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            getBuffer(marshaller).marshal(out, marshaller);
        }


        public String getValue() {
            return bean.value;
        }


        public boolean equals(Object o){
            if(this == o) {
                return true;
            }

            if(o == null || !(o instanceof AmqpString)) {
                return false;
            }

            return equivalent((AmqpString) o);
        }

        public int hashCode() {
            if(getValue() == null) {
                return AmqpString.AmqpStringBean.class.hashCode();
            }
            return getValue().hashCode();
        }

        public boolean equivalent(AmqpType<?,?> t){
            if(this == t) {
                return true;
            }

            if(t == null || !(t instanceof AmqpString)) {
                return false;
            }

            return equivalent((AmqpString) t);
        }

        public boolean equivalent(AmqpString b) {
            if(b == null) {
                return false;
            }

            if(b.getValue() == null ^ getValue() == null) {
                return false;
            }

            return b.getValue() == null || b.getValue().equals(getValue());
        }
    }

    public static class AmqpStringBuffer implements AmqpString, AmqpBuffer< String> {

        private AmqpStringBean bean;
        protected Encoded<String> encoded;

        protected AmqpStringBuffer() {
        }

        protected AmqpStringBuffer(Encoded<String> encoded) {
            this.encoded = encoded;
        }

        public final Encoded<String> getEncoded() throws AmqpEncodingError{
            return encoded;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            encoded.marshal(out);
        }

        public String getValue() {
            return bean().getValue();
        }

        public AmqpString.AmqpStringBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpString bean() {
            if(bean == null) {
                bean = new AmqpString.AmqpStringBean(encoded.getValue());
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

        public static AmqpString.AmqpStringBuffer create(Encoded<String> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpString.AmqpStringBuffer(encoded);
        }

        public static AmqpString.AmqpStringBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpString(in));
        }

        public static AmqpString.AmqpStringBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpString(buffer, offset));
        }
    }
}