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
import org.apache.activemq.amqp.protocol.types.IAmqpMap;
import org.apache.activemq.util.buffer.Buffer;

/**
 * Represents a a polymorphic mapping from distinct keys to values
 */
public interface AmqpMap extends AmqpType<AmqpMap.AmqpMapBean, AmqpMap.AmqpMapBuffer>, IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> {

    /**
     * Represents a a polymorphic mapping from distinct keys to values
     */
    public void put(AmqpType<?, ?> key, AmqpType<?, ?> value);
    public AmqpType<?, ?> get(Object key);

    public IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> getValue();

    public static class AmqpMapBean implements AmqpMap{

        private AmqpMapBuffer buffer;
        private AmqpMapBean bean = this;
        private IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> value;

        AmqpMapBean() {
            this.value = new IAmqpMap.AmqpWrapperMap<AmqpType<?,?>, AmqpType<?,?>>(new HashMap<AmqpType<?,?>, AmqpType<?,?>>());
        }

        AmqpMapBean(IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> value) {
            this.value = value;
        }

        AmqpMapBean(AmqpMap.AmqpMapBean other) {
            this.bean = other;
        }

        public final AmqpMapBean copy() {
            return new AmqpMap.AmqpMapBean(bean);
        }

        public final AmqpMap.AmqpMapBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            if(buffer == null) {
                buffer = new AmqpMapBuffer(marshaller.encode(this));
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

        private final void copy(AmqpMap.AmqpMapBean other) {
            this.value = other.value;
            bean = this;
        }

        public boolean equals(Object o){
            if(this == o) {
                return true;
            }

            if(o == null || !(o instanceof AmqpMap)) {
                return false;
            }

            return equals((AmqpMap) o);
        }

        public boolean equals(AmqpMap b) {
            return AbstractAmqpMap.checkEqual(this, b);
        }

        public int hashCode() {
            return AbstractAmqpMap.hashCodeFor(this);
        }
    }

    public static class AmqpMapBuffer implements AmqpMap, AmqpBuffer< IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> {

        private AmqpMapBean bean;
        protected Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> encoded;

        protected AmqpMapBuffer() {
        }

        protected AmqpMapBuffer(Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> encoded) {
            this.encoded = encoded;
        }

        public final Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> getEncoded() throws AmqpEncodingError{
            return encoded;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            encoded.marshal(out);
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

        public AmqpMap.AmqpMapBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpMap bean() {
            if(bean == null) {
                bean = new AmqpMap.AmqpMapBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public boolean equals(Object o){
            return bean().equals(o);
        }

        public boolean equals(AmqpMap o){
            return bean().equals(o);
        }

        public int hashCode() {
            return bean().hashCode();
        }

        public static AmqpMap.AmqpMapBuffer create(Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpMap.AmqpMapBuffer(encoded);
        }

        public static AmqpMap.AmqpMapBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpMap(in));
        }

        public static AmqpMap.AmqpMapBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpMap(buffer, offset));
        }
    }
}