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
 * Represents a transfer a Message
 * <p>
 * The transfer command is used to send Messages across a Link. Messages may be carried by a
 * single transfer command up to the maximum negotiated frame size for the Connection. Larger
 * Messages may be split across several consecutive transfer commands.
 * </p>
 */
public interface AmqpTransfer extends AmqpList, AmqpCommand {



    /**
     * options map
     */
    public void setOptions(AmqpOptions options);

    /**
     * options map
     */
    public AmqpOptions getOptions();

    /**
     * <p>
     * Specifies the Link on which the Message is transferred.
     * </p>
     * <p>
     * command and subsequently used
     * by endpoints as a shorthand to refer to the Link in all outgoing commands. The two
     * endpoints may potentially use different handles to refer to the same Link. Link handles
     * may be reused once a Link is closed for both send and receive.
     * </p>
     */
    public void setHandle(Long handle);

    /**
     * <p>
     * Specifies the Link on which the Message is transferred.
     * </p>
     * <p>
     * command and subsequently used
     * by endpoints as a shorthand to refer to the Link in all outgoing commands. The two
     * endpoints may potentially use different handles to refer to the same Link. Link handles
     * may be reused once a Link is closed for both send and receive.
     * </p>
     */
    public void setHandle(long handle);

    /**
     * <p>
     * Specifies the Link on which the Message is transferred.
     * </p>
     * <p>
     * command and subsequently used
     * by endpoints as a shorthand to refer to the Link in all outgoing commands. The two
     * endpoints may potentially use different handles to refer to the same Link. Link handles
     * may be reused once a Link is closed for both send and receive.
     * </p>
     */
    public void setHandle(AmqpHandle handle);

    /**
     * <p>
     * Specifies the Link on which the Message is transferred.
     * </p>
     * <p>
     * command and subsequently used
     * by endpoints as a shorthand to refer to the Link in all outgoing commands. The two
     * endpoints may potentially use different handles to refer to the same Link. Link handles
     * may be reused once a Link is closed for both send and receive.
     * </p>
     */
    public AmqpHandle getHandle();

    /**
     * <p>
     * Uniquely identifies the delivery attempt for a given Message on this Link.
     * </p>
     */
    public void setDeliveryTag(Buffer deliveryTag);

    /**
     * <p>
     * Uniquely identifies the delivery attempt for a given Message on this Link.
     * </p>
     */
    public void setDeliveryTag(AmqpDeliveryTag deliveryTag);

    /**
     * <p>
     * Uniquely identifies the delivery attempt for a given Message on this Link.
     * </p>
     */
    public AmqpDeliveryTag getDeliveryTag();

    /**
     * indicates that the Message has more content
     */
    public void setMore(Boolean more);

    /**
     * indicates that the Message has more content
     */
    public void setMore(boolean more);

    /**
     * indicates that the Message has more content
     */
    public void setMore(AmqpBoolean more);

    /**
     * indicates that the Message has more content
     */
    public Boolean getMore();

    /**
     * indicates that the Message is aborted
     * <p>
     * Aborted Messages should be discarded by the recipient.
     * </p>
     */
    public void setAborted(Boolean aborted);

    /**
     * indicates that the Message is aborted
     * <p>
     * Aborted Messages should be discarded by the recipient.
     * </p>
     */
    public void setAborted(boolean aborted);

    /**
     * indicates that the Message is aborted
     * <p>
     * Aborted Messages should be discarded by the recipient.
     * </p>
     */
    public void setAborted(AmqpBoolean aborted);

    /**
     * indicates that the Message is aborted
     * <p>
     * Aborted Messages should be discarded by the recipient.
     * </p>
     */
    public Boolean getAborted();

    public void setFragments(AmqpList fragments);

    public IAmqpList getFragments();

    public static class AmqpTransferBean implements AmqpTransfer{

        private AmqpTransferBuffer buffer;
        private AmqpTransferBean bean = this;
        private AmqpOptions options;
        private AmqpHandle handle;
        private AmqpDeliveryTag deliveryTag;
        private AmqpBoolean more;
        private AmqpBoolean aborted;
        private AmqpList fragments;

        AmqpTransferBean() {
        }

        AmqpTransferBean(IAmqpList value) {

        for(int i = 0; i < value.getListCount(); i++) {
            set(i, value.get(i));
        }
    }

    AmqpTransferBean(AmqpTransfer.AmqpTransferBean other) {
        this.bean = other;
    }

    public final AmqpTransferBean copy() {
        return new AmqpTransfer.AmqpTransferBean(bean);
    }

    public final void handle(AmqpCommandHandler handler) throws Exception {
        handler.handleTransfer(this);
    }

    public final AmqpTransfer.AmqpTransferBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
        if(buffer == null) {
            buffer = new AmqpTransferBuffer(marshaller.encode(this));
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

    public void setHandle(Long handle) {
        setHandle(TypeFactory.createAmqpHandle(handle));
    }


    public void setHandle(long handle) {
        setHandle(TypeFactory.createAmqpHandle(handle));
    }


    public final void setHandle(AmqpHandle handle) {
        copyCheck();
        bean.handle = handle;
    }

    public final AmqpHandle getHandle() {
        return bean.handle;
    }

    public void setDeliveryTag(Buffer deliveryTag) {
        setDeliveryTag(TypeFactory.createAmqpDeliveryTag(deliveryTag));
    }


    public final void setDeliveryTag(AmqpDeliveryTag deliveryTag) {
        copyCheck();
        bean.deliveryTag = deliveryTag;
    }

    public final AmqpDeliveryTag getDeliveryTag() {
        return bean.deliveryTag;
    }

    public void setMore(Boolean more) {
        setMore(TypeFactory.createAmqpBoolean(more));
    }


    public void setMore(boolean more) {
        setMore(TypeFactory.createAmqpBoolean(more));
    }


    public final void setMore(AmqpBoolean more) {
        copyCheck();
        bean.more = more;
    }

    public final Boolean getMore() {
        return bean.more.getValue();
    }

    public void setAborted(Boolean aborted) {
        setAborted(TypeFactory.createAmqpBoolean(aborted));
    }


    public void setAborted(boolean aborted) {
        setAborted(TypeFactory.createAmqpBoolean(aborted));
    }


    public final void setAborted(AmqpBoolean aborted) {
        copyCheck();
        bean.aborted = aborted;
    }

    public final Boolean getAborted() {
        return bean.aborted.getValue();
    }

    public final void setFragments(AmqpList fragments) {
        copyCheck();
        bean.fragments = fragments;
    }

    public final IAmqpList getFragments() {
        return bean.fragments.getValue();
    }

    public void set(int index, AmqpType<?, ?> value) {
        switch(index) {
        case 0: {
            setOptions((AmqpOptions) value);
            break;
        }
        case 1: {
            setHandle((AmqpHandle) value);
            break;
        }
        case 2: {
            setDeliveryTag((AmqpDeliveryTag) value);
            break;
        }
        case 3: {
            setMore((AmqpBoolean) value);
            break;
        }
        case 4: {
            setAborted((AmqpBoolean) value);
            break;
        }
        case 5: {
            setFragments((AmqpList) value);
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
            return bean.handle;
        }
        case 2: {
            return bean.deliveryTag;
        }
        case 3: {
            return bean.more;
        }
        case 4: {
            return bean.aborted;
        }
        case 5: {
            return bean.fragments;
        }
        default : {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        }
    }

    public int getListCount() {
        return 6;
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

    private final void copy(AmqpTransfer.AmqpTransferBean other) {
        bean = this;
    }

    public boolean equals(Object o){
        if(this == o) {
            return true;
        }

        if(o == null || !(o instanceof AmqpTransfer)) {
            return false;
        }

        return equals((AmqpTransfer) o);
    }

    public boolean equals(AmqpTransfer b) {

        if(b.getOptions() == null ^ getOptions() == null) {
            return false;
        }
        if(b.getOptions() != null && !b.getOptions().equals(getOptions())){ 
            return false;
        }

        if(b.getHandle() == null ^ getHandle() == null) {
            return false;
        }
        if(b.getHandle() != null && !b.getHandle().equals(getHandle())){ 
            return false;
        }

        if(b.getDeliveryTag() == null ^ getDeliveryTag() == null) {
            return false;
        }
        if(b.getDeliveryTag() != null && !b.getDeliveryTag().equals(getDeliveryTag())){ 
            return false;
        }

        if(b.getMore() == null ^ getMore() == null) {
            return false;
        }
        if(b.getMore() != null && !b.getMore().equals(getMore())){ 
            return false;
        }

        if(b.getAborted() == null ^ getAborted() == null) {
            return false;
        }
        if(b.getAborted() != null && !b.getAborted().equals(getAborted())){ 
            return false;
        }

        if(b.getFragments() == null ^ getFragments() == null) {
            return false;
        }
        if(b.getFragments() != null && !b.getFragments().equals(getFragments())){ 
            return false;
        }
        return true;
    }

    public int hashCode() {
        return AbstractAmqpList.hashCodeFor(this);
    }
}

    public static class AmqpTransferBuffer extends AmqpList.AmqpListBuffer implements AmqpTransfer{

        private AmqpTransferBean bean;

        protected AmqpTransferBuffer(Encoded<IAmqpList> encoded) {
            super(encoded);
        }

        public final void setOptions(AmqpOptions options) {
            bean().setOptions(options);
        }

        public final AmqpOptions getOptions() {
            return bean().getOptions();
        }

        public void setHandle(Long handle) {
            bean().setHandle(handle);
        }

        public void setHandle(long handle) {
            bean().setHandle(handle);
        }


        public final void setHandle(AmqpHandle handle) {
            bean().setHandle(handle);
        }

        public final AmqpHandle getHandle() {
            return bean().getHandle();
        }

        public void setDeliveryTag(Buffer deliveryTag) {
            bean().setDeliveryTag(deliveryTag);
        }

        public final void setDeliveryTag(AmqpDeliveryTag deliveryTag) {
            bean().setDeliveryTag(deliveryTag);
        }

        public final AmqpDeliveryTag getDeliveryTag() {
            return bean().getDeliveryTag();
        }

        public void setMore(Boolean more) {
            bean().setMore(more);
        }

        public void setMore(boolean more) {
            bean().setMore(more);
        }


        public final void setMore(AmqpBoolean more) {
            bean().setMore(more);
        }

        public final Boolean getMore() {
            return bean().getMore();
        }

        public void setAborted(Boolean aborted) {
            bean().setAborted(aborted);
        }

        public void setAborted(boolean aborted) {
            bean().setAborted(aborted);
        }


        public final void setAborted(AmqpBoolean aborted) {
            bean().setAborted(aborted);
        }

        public final Boolean getAborted() {
            return bean().getAborted();
        }

        public final void setFragments(AmqpList fragments) {
            bean().setFragments(fragments);
        }

        public final IAmqpList getFragments() {
            return bean().getFragments();
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

        public AmqpTransfer.AmqpTransferBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpTransfer bean() {
            if(bean == null) {
                bean = new AmqpTransfer.AmqpTransferBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public final void handle(AmqpCommandHandler handler) throws Exception {
            handler.handleTransfer(this);
        }

        public boolean equals(Object o){
            return bean().equals(o);
        }

        public boolean equals(AmqpTransfer o){
            return bean().equals(o);
        }

        public int hashCode() {
            return bean().hashCode();
        }

        public static AmqpTransfer.AmqpTransferBuffer create(Encoded<IAmqpList> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpTransfer.AmqpTransferBuffer(encoded);
        }

        public static AmqpTransfer.AmqpTransferBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpTransfer(in));
        }

        public static AmqpTransfer.AmqpTransferBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpTransfer(buffer, offset));
        }
    }
}