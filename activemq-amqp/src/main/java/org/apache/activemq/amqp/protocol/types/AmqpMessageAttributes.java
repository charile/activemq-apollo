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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpMarshaller;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.amqp.protocol.types.AmqpMap;
import org.apache.activemq.amqp.protocol.types.AmqpMessageAttributes;
import org.apache.activemq.amqp.protocol.types.IAmqpMap;
import org.apache.activemq.util.buffer.Buffer;

/**
 * <p>
 * A map providing an extension point by which annotations on message deliveries. All values
 * used as keys in the map MUST be of type symbol. Further if a key begins with the string
 * "x-req-" then the target MUST reject the message unless it understands how to process the
 * supplied key/value.
 * </p>
 */
public interface AmqpMessageAttributes extends AmqpMap {


    public static class AmqpMessageAttributesBean implements AmqpMessageAttributes{

        private AmqpMessageAttributesBuffer buffer;
        private AmqpMessageAttributesBean bean = this;
        private IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> value;

        AmqpMessageAttributesBean() {
            this.value = new IAmqpMap.AmqpWrapperMap<AmqpType<?,?>, AmqpType<?,?>>(new HashMap<AmqpType<?,?>, AmqpType<?,?>>());
        }

        AmqpMessageAttributesBean(IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> value) {
            this.value = value;
        }

        AmqpMessageAttributesBean(AmqpMessageAttributes.AmqpMessageAttributesBean other) {
            this.bean = other;
        }

        public final AmqpMessageAttributesBean copy() {
            return new AmqpMessageAttributes.AmqpMessageAttributesBean(bean);
        }

        public final AmqpMessageAttributes.AmqpMessageAttributesBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            if(buffer == null) {
                buffer = new AmqpMessageAttributesBuffer(marshaller.encode(this));
            }
            return buffer;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            getBuffer(marshaller).marshal(out, marshaller);
        }

        public void put(AmqpType<?, ?> key, AmqpType<?, ?> value) {
            copyCheck();
            bean.value.put(key, value);
        }

        public AmqpType<?, ?> get(Object key) {
            return bean.value.get(key);
        }

        public int getEntryCount() {
            return bean.value.getEntryCount();
        }

        public Iterator<Map.Entry<AmqpType<?, ?>, AmqpType<?, ?>>> iterator() {
            return bean.value.iterator();
        }

        public IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> getValue() {
            return bean.value;
        }


        private final void copyCheck() {
            if(buffer != null) {;
                throw new IllegalStateException("unwriteable");
            }
            if(bean != this) {;
                copy(bean);
            }
        }

        private final void copy(AmqpMessageAttributes.AmqpMessageAttributesBean other) {
            this.value = other.value;
            bean = this;
        }

        public boolean equals(Object o){
            if(this == o) {
                return true;
            }

            if(o == null || !(o instanceof AmqpMessageAttributes)) {
                return false;
            }

            return equals((AmqpMessageAttributes) o);
        }

        public boolean equals(AmqpMessageAttributes b) {
            return AbstractAmqpMap.checkEqual(this, b);
        }

        public int hashCode() {
            return AbstractAmqpMap.hashCodeFor(this);
        }
    }

    public static class AmqpMessageAttributesBuffer extends AmqpMap.AmqpMapBuffer implements AmqpMessageAttributes{

        private AmqpMessageAttributesBean bean;

        protected AmqpMessageAttributesBuffer() {
            super();
        }

        protected AmqpMessageAttributesBuffer(Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> encoded) {
            super(encoded);
        }
        public void put(AmqpType<?, ?> key, AmqpType<?, ?> value) {
            bean().put(key, value);
        }

        public AmqpType<?, ?> get(Object key) {
            return bean().get(key);
        }

        public int getEntryCount() {
            return bean().getEntryCount();
        }

        public Iterator<Map.Entry<AmqpType<?, ?>, AmqpType<?, ?>>> iterator() {
            return bean().iterator();
        }

        public IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> getValue() {
            return bean().getValue();
        }

        public AmqpMessageAttributes.AmqpMessageAttributesBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpMessageAttributes bean() {
            if(bean == null) {
                bean = new AmqpMessageAttributes.AmqpMessageAttributesBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public boolean equals(Object o){
            return bean().equals(o);
        }

        public boolean equals(AmqpMessageAttributes o){
            return bean().equals(o);
        }

        public int hashCode() {
            return bean().hashCode();
        }

        public static AmqpMessageAttributes.AmqpMessageAttributesBuffer create(Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpMessageAttributes.AmqpMessageAttributesBuffer(encoded);
        }

        public static AmqpMessageAttributes.AmqpMessageAttributesBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpMap(in));
        }

        public static AmqpMessageAttributes.AmqpMessageAttributesBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpMap(buffer, offset));
        }
    }
}