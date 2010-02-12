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
import java.util.Iterator;
import org.apache.activemq.amqp.protocol.AmqpCommand;
import org.apache.activemq.amqp.protocol.AmqpCommandHandler;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpMarshaller;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.amqp.protocol.types.IAmqpList;
import org.apache.activemq.util.buffer.Buffer;

/**
 * Represents a associate the current transactional work with a distributed transaction
 * <p>
 * Associates the current transactional unit of work with the distributed transaction
 * identified by the supplied xid.
 * </p>
 * <p>
 * This command is only used by the peer acting as the Transaction Controller for a Session
 * that is in the distributed or promotable transaction mode. When used on a Session that is
 * in the distributed transaction mode, this command must be issued before any transactional
 * work is performed within a given transactional unit. When used on a Session that is in the
 * promotable transaction mode, this command may be used at any time. In either case, at most
 * one enlist command may appear within any given unit of transactional work.
 * </p>
 */
public interface AmqpEnlist extends AmqpList, AmqpCommand {



    /**
     * options map
     */
    public void setOptions(AmqpOptions options);

    /**
     * options map
     */
    public AmqpOptions getOptions();

    /**
     * Transaction xid
     * <p>
     * Specifies the xid of the transaction branch in which to enlist.
     * </p>
     */
    public void setXid(AmqpXid xid);

    /**
     * Transaction xid
     * <p>
     * Specifies the xid of the transaction branch in which to enlist.
     * </p>
     */
    public AmqpXid getXid();

    /**
     * Join with existing xid flag
     * <p>
     * Indicate whether this is joining an already associated xid. Indicate that the enlist
     * applies to joining a transaction previously seen.
     * </p>
     */
    public void setJoin(Boolean join);

    /**
     * Join with existing xid flag
     * <p>
     * Indicate whether this is joining an already associated xid. Indicate that the enlist
     * applies to joining a transaction previously seen.
     * </p>
     */
    public void setJoin(AmqpBoolean join);

    /**
     * Join with existing xid flag
     * <p>
     * Indicate whether this is joining an already associated xid. Indicate that the enlist
     * applies to joining a transaction previously seen.
     * </p>
     */
    public Boolean getJoin();

    /**
     * Resume flag
     * <p>
     * Indicate that the enlist applies to resuming a suspended transaction branch.
     * </p>
     */
    public void setResume(Boolean resume);

    /**
     * Resume flag
     * <p>
     * Indicate that the enlist applies to resuming a suspended transaction branch.
     * </p>
     */
    public void setResume(AmqpBoolean resume);

    /**
     * Resume flag
     * <p>
     * Indicate that the enlist applies to resuming a suspended transaction branch.
     * </p>
     */
    public Boolean getResume();

    public static class AmqpEnlistBean implements AmqpEnlist{

        private AmqpEnlistBuffer buffer;
        private AmqpEnlistBean bean = this;
        private AmqpOptions options;
        private AmqpXid xid;
        private AmqpBoolean join;
        private AmqpBoolean resume;

        public AmqpEnlistBean() {
        }

        public AmqpEnlistBean(IAmqpList value) {
            //TODO we should defer decoding of the described type:
            for(int i = 0; i < value.getListCount(); i++) {
                set(i, value.get(i));
            }
        }

        public AmqpEnlistBean(AmqpEnlist.AmqpEnlistBean other) {
            this.bean = other;
        }

        public final AmqpEnlistBean copy() {
            return new AmqpEnlist.AmqpEnlistBean(bean);
        }

        public final void handle(AmqpCommandHandler handler) throws Exception {
            handler.handleEnlist(this);
        }

        public final AmqpEnlist.AmqpEnlistBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            if(buffer == null) {
                buffer = new AmqpEnlistBuffer(marshaller.encode(this));
            }
            return buffer;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            getBuffer(marshaller).marshal(out, marshaller);
        }


        public final void setOptions(AmqpOptions options) {
            copyCheck();
            bean.options = options;
        }

        public final AmqpOptions getOptions() {
            return bean.options;
        }

        public final void setXid(AmqpXid xid) {
            copyCheck();
            bean.xid = xid;
        }

        public final AmqpXid getXid() {
            return bean.xid;
        }

        public void setJoin(Boolean join) {
            setJoin(new AmqpBoolean.AmqpBooleanBean(join));
        }


        public final void setJoin(AmqpBoolean join) {
            copyCheck();
            bean.join = join;
        }

        public final Boolean getJoin() {
            return bean.join.getValue();
        }

        public void setResume(Boolean resume) {
            setResume(new AmqpBoolean.AmqpBooleanBean(resume));
        }


        public final void setResume(AmqpBoolean resume) {
            copyCheck();
            bean.resume = resume;
        }

        public final Boolean getResume() {
            return bean.resume.getValue();
        }

        public void set(int index, AmqpType<?, ?> value) {
            switch(index) {
            case 0: {
                setOptions((AmqpOptions) value);
                break;
            }
            case 1: {
                setXid((AmqpXid) value);
                break;
            }
            case 2: {
                setJoin((AmqpBoolean) value);
                break;
            }
            case 3: {
                setResume((AmqpBoolean) value);
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
                return bean.options;
            }
            case 1: {
                return bean.xid;
            }
            case 2: {
                return bean.join;
            }
            case 3: {
                return bean.resume;
            }
            default : {
                throw new IndexOutOfBoundsException(String.valueOf(index));
            }
            }
        }

        public int getListCount() {
            return 4;
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

        private final void copy(AmqpEnlist.AmqpEnlistBean other) {
            this.options= other.options;
            this.xid= other.xid;
            this.join= other.join;
            this.resume= other.resume;
            bean = this;
        }

        public boolean equivalent(AmqpType<?,?> t){
            if(this == t) {
                return true;
            }

            if(t == null || !(t instanceof AmqpEnlist)) {
                return false;
            }

            return equivalent((AmqpEnlist) t);
        }

        public boolean equivalent(AmqpEnlist b) {

            if(b.getOptions() == null ^ getOptions() == null) {
                return false;
            }
            if(b.getOptions() != null && !b.getOptions().equals(getOptions())){ 
                return false;
            }

            if(b.getXid() == null ^ getXid() == null) {
                return false;
            }
            if(b.getXid() != null && !b.getXid().equivalent(getXid())){ 
                return false;
            }

            if(b.getJoin() == null ^ getJoin() == null) {
                return false;
            }
            if(b.getJoin() != null && !b.getJoin().equals(getJoin())){ 
                return false;
            }

            if(b.getResume() == null ^ getResume() == null) {
                return false;
            }
            if(b.getResume() != null && !b.getResume().equals(getResume())){ 
                return false;
            }
            return true;
        }
    }

    public static class AmqpEnlistBuffer extends AmqpList.AmqpListBuffer implements AmqpEnlist{

        private AmqpEnlistBean bean;

        protected AmqpEnlistBuffer(Encoded<IAmqpList> encoded) {
            super(encoded);
        }

        public final void setOptions(AmqpOptions options) {
            bean().setOptions(options);
        }

        public final AmqpOptions getOptions() {
            return bean().getOptions();
        }

        public final void setXid(AmqpXid xid) {
            bean().setXid(xid);
        }

        public final AmqpXid getXid() {
            return bean().getXid();
        }

    public void setJoin(Boolean join) {
            bean().setJoin(join);
        }

        public final void setJoin(AmqpBoolean join) {
            bean().setJoin(join);
        }

        public final Boolean getJoin() {
            return bean().getJoin();
        }

    public void setResume(Boolean resume) {
            bean().setResume(resume);
        }

        public final void setResume(AmqpBoolean resume) {
            bean().setResume(resume);
        }

        public final Boolean getResume() {
            return bean().getResume();
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

        public AmqpEnlist.AmqpEnlistBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpEnlist bean() {
            if(bean == null) {
                bean = new AmqpEnlist.AmqpEnlistBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public final void handle(AmqpCommandHandler handler) throws Exception {
            handler.handleEnlist(this);
        }

        public boolean equivalent(AmqpType<?, ?> t) {
            return bean().equivalent(t);
        }

        public static AmqpEnlist.AmqpEnlistBuffer create(Encoded<IAmqpList> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpEnlist.AmqpEnlistBuffer(encoded);
        }

        public static AmqpEnlist.AmqpEnlistBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpEnlist(in));
        }

        public static AmqpEnlist.AmqpEnlistBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpEnlist(buffer, offset));
        }
    }
}