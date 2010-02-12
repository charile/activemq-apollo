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
import java.util.Iterator;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpMarshaller;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.amqp.protocol.types.IAmqpList;
import org.apache.activemq.util.buffer.Buffer;

/**
 * Represents a transport footers for a Message
 */
public interface AmqpFooter extends AmqpList {



    /**
     * message attributes
     * <p>
     * command and
     * dispositions which allow for updates to the message-attrs.
     * </p>
     */
    public void setMessageAttrs(AmqpMessageAttributes messageAttrs);

    /**
     * message attributes
     * <p>
     * command and
     * dispositions which allow for updates to the message-attrs.
     * </p>
     */
    public AmqpMessageAttributes getMessageAttrs();

    /**
     * delivery attributes
     * <p>
     * command and dispositions which allow for updates
     * to the delivery-attrs.
     * </p>
     */
    public void setDeliveryAttrs(AmqpMessageAttributes deliveryAttrs);

    /**
     * delivery attributes
     * <p>
     * command and dispositions which allow for updates
     * to the delivery-attrs.
     * </p>
     */
    public AmqpMessageAttributes getDeliveryAttrs();

    public static class AmqpFooterBean implements AmqpFooter{

        private AmqpFooterBuffer buffer;
        private AmqpFooterBean bean = this;
        private AmqpMessageAttributes messageAttrs;
        private AmqpMessageAttributes deliveryAttrs;

        AmqpFooterBean() {
        }

        AmqpFooterBean(IAmqpList value) {

        for(int i = 0; i < value.getListCount(); i++) {
            set(i, value.get(i));
        }
    }

    AmqpFooterBean(AmqpFooter.AmqpFooterBean other) {
        this.bean = other;
    }

    public final AmqpFooterBean copy() {
        return new AmqpFooter.AmqpFooterBean(bean);
    }

    public final AmqpFooter.AmqpFooterBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
        if(buffer == null) {
            buffer = new AmqpFooterBuffer(marshaller.encode(this));
        }
        return buffer;
    }

    public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
        getBuffer(marshaller).marshal(out, marshaller);
    }


    public final void setMessageAttrs(AmqpMessageAttributes messageAttrs) {
        copyCheck();
        bean.messageAttrs = messageAttrs;
    }

    public final AmqpMessageAttributes getMessageAttrs() {
        return bean.messageAttrs;
    }

    public final void setDeliveryAttrs(AmqpMessageAttributes deliveryAttrs) {
        copyCheck();
        bean.deliveryAttrs = deliveryAttrs;
    }

    public final AmqpMessageAttributes getDeliveryAttrs() {
        return bean.deliveryAttrs;
    }

    public void set(int index, AmqpType<?, ?> value) {
        switch(index) {
        case 0: {
            setMessageAttrs((AmqpMessageAttributes) value);
            break;
        }
        case 1: {
            setDeliveryAttrs((AmqpMessageAttributes) value);
            break;
        }
        default : {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        }
    }

    public AmqpType<?, ?> get(int index) {
        switch(index) {
        case 0: {
            return bean.messageAttrs;
        }
        case 1: {
            return bean.deliveryAttrs;
        }
        default : {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        }
    }

    public int getListCount() {
        return 2;
    }

    public IAmqpList getValue() {
        return bean;
    }

    public Iterator<AmqpType<?, ?>> iterator() {
        return new AmqpListIterator(bean);
    }


    private final void copyCheck() {
        if(buffer != null) {;
            throw new IllegalStateException("unwriteable");
        }
        if(bean != this) {;
            copy(bean);
        }
    }

    private final void copy(AmqpFooter.AmqpFooterBean other) {
        bean = this;
    }

    public boolean equals(Object o){
        if(this == o) {
            return true;
        }

        if(o == null || !(o instanceof AmqpFooter)) {
            return false;
        }

        return equals((AmqpFooter) o);
    }

    public boolean equals(AmqpFooter b) {

        if(b.getMessageAttrs() == null ^ getMessageAttrs() == null) {
            return false;
        }
        if(b.getMessageAttrs() != null && !b.getMessageAttrs().equals(getMessageAttrs())){ 
            return false;
        }

        if(b.getDeliveryAttrs() == null ^ getDeliveryAttrs() == null) {
            return false;
        }
        if(b.getDeliveryAttrs() != null && !b.getDeliveryAttrs().equals(getDeliveryAttrs())){ 
            return false;
        }
        return true;
    }

    public int hashCode() {
        return AbstractAmqpList.hashCodeFor(this);
    }
}

    public static class AmqpFooterBuffer extends AmqpList.AmqpListBuffer implements AmqpFooter{

        private AmqpFooterBean bean;

        protected AmqpFooterBuffer(Encoded<IAmqpList> encoded) {
            super(encoded);
        }

        public final void setMessageAttrs(AmqpMessageAttributes messageAttrs) {
            bean().setMessageAttrs(messageAttrs);
        }

        public final AmqpMessageAttributes getMessageAttrs() {
            return bean().getMessageAttrs();
        }

        public final void setDeliveryAttrs(AmqpMessageAttributes deliveryAttrs) {
            bean().setDeliveryAttrs(deliveryAttrs);
        }

        public final AmqpMessageAttributes getDeliveryAttrs() {
            return bean().getDeliveryAttrs();
        }

        public void set(int index, AmqpType<?, ?> value) {
            bean().set(index, value);
        }

        public AmqpType<?, ?> get(int index) {
            return bean().get(index);
        }

        public int getListCount() {
            return bean().getListCount();
        }

        public Iterator<AmqpType<?, ?>> iterator() {
            return bean().iterator();
        }

        public IAmqpList getValue() {
            return bean().getValue();
        }

        public AmqpFooter.AmqpFooterBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpFooter bean() {
            if(bean == null) {
                bean = new AmqpFooter.AmqpFooterBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public boolean equals(Object o){
            return bean().equals(o);
        }

        public boolean equals(AmqpFooter o){
            return bean().equals(o);
        }

        public int hashCode() {
            return bean().hashCode();
        }

        public static AmqpFooter.AmqpFooterBuffer create(Encoded<IAmqpList> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpFooter.AmqpFooterBuffer(encoded);
        }

        public static AmqpFooter.AmqpFooterBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpFooter(in));
        }

        public static AmqpFooter.AmqpFooterBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpFooter(buffer, offset));
        }
    }
}