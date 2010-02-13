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
import org.apache.activemq.amqp.protocol.types.AmqpFilterSet;
import org.apache.activemq.amqp.protocol.types.AmqpMap;
import org.apache.activemq.amqp.protocol.types.IAmqpMap;
import org.apache.activemq.util.buffer.Buffer;

/**
 * <p>
 * . A message will pass through a filter-set if and only
 * if it passes through each of the named filters
 * </p>
 */
public interface AmqpFilterSet extends AmqpMap {


    public static class AmqpFilterSetBean implements AmqpFilterSet{

        private AmqpFilterSetBuffer buffer;
        private AmqpFilterSetBean bean = this;
        private IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> value;

        AmqpFilterSetBean() {
            this.value = new IAmqpMap.AmqpWrapperMap<AmqpType<?,?>,AmqpType<?,?>>(new HashMap<AmqpType<?,?>,AmqpType<?,?>>());
        }

        AmqpFilterSetBean(IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> value) {
            this.value = value;
        }

        AmqpFilterSetBean(AmqpFilterSet.AmqpFilterSetBean other) {
            this.bean = other;
        }

        public final AmqpFilterSetBean copy() {
            return new AmqpFilterSet.AmqpFilterSetBean(bean);
        }

        public final AmqpFilterSet.AmqpFilterSetBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            if(buffer == null) {
                buffer = new AmqpFilterSetBuffer(marshaller.encode(this));
            }
            return buffer;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            getBuffer(marshaller).marshal(out, marshaller);
        }

        public void put(AmqpType<?,?> key, AmqpType<?,?> value) {
            copyCheck();
            bean.value.put(key, value);
        }

        public AmqpType<?,?> get(Object key) {
            return bean.value.get(key);
        }

        public int getEntryCount() {
            return bean.value.getEntryCount();
        }

        public Iterator<Map.Entry<AmqpType<?,?>, AmqpType<?,?>>> iterator() {
            return bean.value.iterator();
        }


        private final void copyCheck() {
            if(buffer != null) {;
                throw new IllegalStateException("unwriteable");
            }
            if(bean != this) {;
                copy(bean);
            }
        }

        private final void copy(AmqpFilterSet.AmqpFilterSetBean other) {
            this.value = other.value;
            bean = this;
        }

        public boolean equals(Object o){
            if(this == o) {
                return true;
            }

            if(o == null || !(o instanceof AmqpFilterSet)) {
                return false;
            }

            return equals((AmqpFilterSet) o);
        }

        public boolean equals(AmqpFilterSet b) {
            return AbstractAmqpMap.checkEqual(this, b);
        }

        public int hashCode() {
            return AbstractAmqpMap.hashCodeFor(this);
        }
    }

    public static class AmqpFilterSetBuffer extends AmqpMap.AmqpMapBuffer implements AmqpFilterSet{

        private AmqpFilterSetBean bean;

        protected AmqpFilterSetBuffer() {
            super();
        }

        protected AmqpFilterSetBuffer(Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> encoded) {
            super(encoded);
        }
        public void put(AmqpType<?,?> key, AmqpType<?,?> value) {
            bean().put(key, value);
        }

        public AmqpType<?,?> get(Object key) {
            return bean().get(key);
        }

        public int getEntryCount() {
            return bean().getEntryCount();
        }

        public Iterator<Map.Entry<AmqpType<?,?>, AmqpType<?,?>>> iterator() {
            return bean().iterator();
        }

        public AmqpFilterSet.AmqpFilterSetBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpFilterSet bean() {
            if(bean == null) {
                bean = new AmqpFilterSet.AmqpFilterSetBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public boolean equals(Object o){
            return bean().equals(o);
        }

        public boolean equals(AmqpFilterSet o){
            return bean().equals(o);
        }

        public int hashCode() {
            return bean().hashCode();
        }

        public static AmqpFilterSet.AmqpFilterSetBuffer create(Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpFilterSet.AmqpFilterSetBuffer(encoded);
        }

        public static AmqpFilterSet.AmqpFilterSetBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpMap(in));
        }

        public static AmqpFilterSet.AmqpFilterSetBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpMap(buffer, offset));
        }
    }
}