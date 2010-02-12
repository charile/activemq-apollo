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
import org.apache.activemq.amqp.protocol.AmqpCommand;
import org.apache.activemq.amqp.protocol.AmqpCommandHandler;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpMarshaller;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.amqp.protocol.types.IAmqpList;
import org.apache.activemq.util.buffer.Buffer;

/**
 * Represents a alter disposition of transfers on a Link
 * <p>
 * Establish a non-default disposition for a set of Link transfers. Normally the default
 * disposition of a transfer is communicated to the sending Node when a Link transfer is
 * acknowledged. The disposition command may be used to explicitly communicate a non-default
 * disposition prior to the transfer being acknowledged. The behavior is undefined if more
 * than one disposition is supplied for the same delivery-tag. Dispositions for acknowledged
 * transfers are ignored.
 * </p>
 */
public interface AmqpDisposition extends AmqpList, AmqpCommand {



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
     * Specifies the Link to which the barrier applies.
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
     * Specifies the Link to which the barrier applies.
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
     * Specifies the Link to which the barrier applies.
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
     * Specifies the Link to which the barrier applies.
     * </p>
     * <p>
     * command and subsequently used
     * by endpoints as a shorthand to refer to the Link in all outgoing commands. The two
     * endpoints may potentially use different handles to refer to the same Link. Link handles
     * may be reused once a Link is closed for both send and receive.
     * </p>
     */
    public AmqpHandle getHandle();

    public void setDisposition(AmqpMap disposition);

    public IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> getDisposition();

    public void setFirst(Buffer first);

    public void setFirst(AmqpDeliveryTag first);

    public AmqpDeliveryTag getFirst();

    public void setLast(Buffer last);

    public void setLast(AmqpDeliveryTag last);

    public AmqpDeliveryTag getLast();

    public static class AmqpDispositionBean implements AmqpDisposition{

        private AmqpDispositionBuffer buffer;
        private AmqpDispositionBean bean = this;
        private AmqpOptions options;
        private AmqpHandle handle;
        private AmqpMap disposition;
        private AmqpDeliveryTag first;
        private AmqpDeliveryTag last;

        AmqpDispositionBean() {
        }

        AmqpDispositionBean(IAmqpList value) {

        for(int i = 0; i < value.getListCount(); i++) {
            set(i, value.get(i));
        }
    }

    AmqpDispositionBean(AmqpDisposition.AmqpDispositionBean other) {
        this.bean = other;
    }

    public final AmqpDispositionBean copy() {
        return new AmqpDisposition.AmqpDispositionBean(bean);
    }

    public final void handle(AmqpCommandHandler handler) throws Exception {
        handler.handleDisposition(this);
    }

    public final AmqpDisposition.AmqpDispositionBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
        if(buffer == null) {
            buffer = new AmqpDispositionBuffer(marshaller.encode(this));
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

    public final void setDisposition(AmqpMap disposition) {
        copyCheck();
        bean.disposition = disposition;
    }

    public final IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> getDisposition() {
        return bean.disposition.getValue();
    }

    public void setFirst(Buffer first) {
        setFirst(TypeFactory.createAmqpDeliveryTag(first));
    }


    public final void setFirst(AmqpDeliveryTag first) {
        copyCheck();
        bean.first = first;
    }

    public final AmqpDeliveryTag getFirst() {
        return bean.first;
    }

    public void setLast(Buffer last) {
        setLast(TypeFactory.createAmqpDeliveryTag(last));
    }


    public final void setLast(AmqpDeliveryTag last) {
        copyCheck();
        bean.last = last;
    }

    public final AmqpDeliveryTag getLast() {
        return bean.last;
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
            setDisposition((AmqpMap) value);
            break;
        }
        case 3: {
            setFirst((AmqpDeliveryTag) value);
            break;
        }
        case 4: {
            setLast((AmqpDeliveryTag) value);
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
            return bean.disposition;
        }
        case 3: {
            return bean.first;
        }
        case 4: {
            return bean.last;
        }
        default : {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        }
    }

    public int getListCount() {
        return 5;
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

    private final void copy(AmqpDisposition.AmqpDispositionBean other) {
        bean = this;
    }

    public boolean equals(Object o){
        if(this == o) {
            return true;
        }

        if(o == null || !(o instanceof AmqpDisposition)) {
            return false;
        }

        return equals((AmqpDisposition) o);
    }

    public boolean equals(AmqpDisposition b) {

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

        if(b.getDisposition() == null ^ getDisposition() == null) {
            return false;
        }
        if(b.getDisposition() != null && !b.getDisposition().equals(getDisposition())){ 
            return false;
        }

        if(b.getFirst() == null ^ getFirst() == null) {
            return false;
        }
        if(b.getFirst() != null && !b.getFirst().equals(getFirst())){ 
            return false;
        }

        if(b.getLast() == null ^ getLast() == null) {
            return false;
        }
        if(b.getLast() != null && !b.getLast().equals(getLast())){ 
            return false;
        }
        return true;
    }

    public int hashCode() {
        return AbstractAmqpList.hashCodeFor(this);
    }
}

    public static class AmqpDispositionBuffer extends AmqpList.AmqpListBuffer implements AmqpDisposition{

        private AmqpDispositionBean bean;

        protected AmqpDispositionBuffer(Encoded<IAmqpList> encoded) {
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

        public final void setDisposition(AmqpMap disposition) {
            bean().setDisposition(disposition);
        }

        public final IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> getDisposition() {
            return bean().getDisposition();
        }

        public void setFirst(Buffer first) {
            bean().setFirst(first);
        }

        public final void setFirst(AmqpDeliveryTag first) {
            bean().setFirst(first);
        }

        public final AmqpDeliveryTag getFirst() {
            return bean().getFirst();
        }

        public void setLast(Buffer last) {
            bean().setLast(last);
        }

        public final void setLast(AmqpDeliveryTag last) {
            bean().setLast(last);
        }

        public final AmqpDeliveryTag getLast() {
            return bean().getLast();
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

        public AmqpDisposition.AmqpDispositionBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpDisposition bean() {
            if(bean == null) {
                bean = new AmqpDisposition.AmqpDispositionBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public final void handle(AmqpCommandHandler handler) throws Exception {
            handler.handleDisposition(this);
        }

        public boolean equals(Object o){
            return bean().equals(o);
        }

        public boolean equals(AmqpDisposition o){
            return bean().equals(o);
        }

        public int hashCode() {
            return bean().hashCode();
        }

        public static AmqpDisposition.AmqpDispositionBuffer create(Encoded<IAmqpList> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpDisposition.AmqpDispositionBuffer(encoded);
        }

        public static AmqpDisposition.AmqpDispositionBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpDisposition(in));
        }

        public static AmqpDisposition.AmqpDispositionBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpDisposition(buffer, offset));
        }
    }
}