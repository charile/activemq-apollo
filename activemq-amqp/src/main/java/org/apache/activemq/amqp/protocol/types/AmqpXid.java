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
import java.util.Iterator;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpMarshaller;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.amqp.protocol.types.IAmqpList;
import org.apache.activemq.util.buffer.Buffer;

/**
 * Represents a dtx branch identifier
 * <p>
 * An xid uniquely identifies a transaction branch.
 * </p>
 */
public interface AmqpXid extends AmqpList {



    /**
     * implementation specific format code
     */
    public void setFormat(Long format);

    /**
     * implementation specific format code
     */
    public void setFormat(long format);

    /**
     * implementation specific format code
     */
    public void setFormat(AmqpUint format);

    /**
     * implementation specific format code
     */
    public Long getFormat();

    /**
     * global transaction id
     */
    public void setGlobalId(Buffer globalId);

    /**
     * global transaction id
     */
    public void setGlobalId(AmqpBinary globalId);

    /**
     * global transaction id
     */
    public Buffer getGlobalId();

    /**
     * branch qualifier
     */
    public void setBranchId(Buffer branchId);

    /**
     * branch qualifier
     */
    public void setBranchId(AmqpBinary branchId);

    /**
     * branch qualifier
     */
    public Buffer getBranchId();

    public static class AmqpXidBean implements AmqpXid{

        private AmqpXidBuffer buffer;
        private AmqpXidBean bean = this;
        private AmqpUint format;
        private AmqpBinary globalId;
        private AmqpBinary branchId;

        AmqpXidBean() {
        }

        AmqpXidBean(IAmqpList value) {

        for(int i = 0; i < value.getListCount(); i++) {
            set(i, value.get(i));
        }
    }

    AmqpXidBean(AmqpXid.AmqpXidBean other) {
        this.bean = other;
    }

    public final AmqpXidBean copy() {
        return new AmqpXid.AmqpXidBean(bean);
    }

    public final AmqpXid.AmqpXidBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
        if(buffer == null) {
            buffer = new AmqpXidBuffer(marshaller.encode(this));
        }
        return buffer;
    }

    public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
        getBuffer(marshaller).marshal(out, marshaller);
    }


    public void setFormat(Long format) {
        setFormat(TypeFactory.createAmqpUint(format));
    }


    public void setFormat(long format) {
        setFormat(TypeFactory.createAmqpUint(format));
    }


    public final void setFormat(AmqpUint format) {
        copyCheck();
        bean.format = format;
    }

    public final Long getFormat() {
        return bean.format.getValue();
    }

    public void setGlobalId(Buffer globalId) {
        setGlobalId(TypeFactory.createAmqpBinary(globalId));
    }


    public final void setGlobalId(AmqpBinary globalId) {
        copyCheck();
        bean.globalId = globalId;
    }

    public final Buffer getGlobalId() {
        return bean.globalId.getValue();
    }

    public void setBranchId(Buffer branchId) {
        setBranchId(TypeFactory.createAmqpBinary(branchId));
    }


    public final void setBranchId(AmqpBinary branchId) {
        copyCheck();
        bean.branchId = branchId;
    }

    public final Buffer getBranchId() {
        return bean.branchId.getValue();
    }

    public void set(int index, AmqpType<?, ?> value) {
        switch(index) {
        case 0: {
            setFormat((AmqpUint) value);
            break;
        }
        case 1: {
            setGlobalId((AmqpBinary) value);
            break;
        }
        case 2: {
            setBranchId((AmqpBinary) value);
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
            return bean.format;
        }
        case 1: {
            return bean.globalId;
        }
        case 2: {
            return bean.branchId;
        }
        default : {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        }
    }

    public int getListCount() {
        return 3;
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

    private final void copy(AmqpXid.AmqpXidBean other) {
        bean = this;
    }

    public boolean equals(Object o){
        if(this == o) {
            return true;
        }

        if(o == null || !(o instanceof AmqpXid)) {
            return false;
        }

        return equals((AmqpXid) o);
    }

    public boolean equals(AmqpXid b) {

        if(b.getFormat() == null ^ getFormat() == null) {
            return false;
        }
        if(b.getFormat() != null && !b.getFormat().equals(getFormat())){ 
            return false;
        }

        if(b.getGlobalId() == null ^ getGlobalId() == null) {
            return false;
        }
        if(b.getGlobalId() != null && !b.getGlobalId().equals(getGlobalId())){ 
            return false;
        }

        if(b.getBranchId() == null ^ getBranchId() == null) {
            return false;
        }
        if(b.getBranchId() != null && !b.getBranchId().equals(getBranchId())){ 
            return false;
        }
        return true;
    }

    public int hashCode() {
        return AbstractAmqpList.hashCodeFor(this);
    }
}

    public static class AmqpXidBuffer extends AmqpList.AmqpListBuffer implements AmqpXid{

        private AmqpXidBean bean;

        protected AmqpXidBuffer(Encoded<IAmqpList> encoded) {
            super(encoded);
        }

        public void setFormat(Long format) {
            bean().setFormat(format);
        }

        public void setFormat(long format) {
            bean().setFormat(format);
        }


        public final void setFormat(AmqpUint format) {
            bean().setFormat(format);
        }

        public final Long getFormat() {
            return bean().getFormat();
        }

        public void setGlobalId(Buffer globalId) {
            bean().setGlobalId(globalId);
        }

        public final void setGlobalId(AmqpBinary globalId) {
            bean().setGlobalId(globalId);
        }

        public final Buffer getGlobalId() {
            return bean().getGlobalId();
        }

        public void setBranchId(Buffer branchId) {
            bean().setBranchId(branchId);
        }

        public final void setBranchId(AmqpBinary branchId) {
            bean().setBranchId(branchId);
        }

        public final Buffer getBranchId() {
            return bean().getBranchId();
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

        public AmqpXid.AmqpXidBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpXid bean() {
            if(bean == null) {
                bean = new AmqpXid.AmqpXidBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public boolean equals(Object o){
            return bean().equals(o);
        }

        public boolean equals(AmqpXid o){
            return bean().equals(o);
        }

        public int hashCode() {
            return bean().hashCode();
        }

        public static AmqpXid.AmqpXidBuffer create(Encoded<IAmqpList> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpXid.AmqpXidBuffer(encoded);
        }

        public static AmqpXid.AmqpXidBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpXid(in));
        }

        public static AmqpXid.AmqpXidBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpXid(buffer, offset));
        }
    }
}