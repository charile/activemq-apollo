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
 * Represents a signal a Connection close
 * <p>
 * Sending a close signals that the sender will not be sending any more commands or controls
 * on the Connection. This control MUST be the last command or control written to a
 * Connection by the sender.
 * </p>
 */
public interface AmqpClose extends AmqpList, AmqpCommand {



    /**
     * options map
     */
    public void setOptions(AmqpOptions options);

    /**
     * options map
     */
    public AmqpOptions getOptions();

    /**
     * error causing the close
     * <p>
     * If set, this field indicates that the Connection is being closed due to an exceptional
     * condition. The value of the field should contain details on the cause of the exception.
     * </p>
     */
    public void setException(AmqpConnectionError exception);

    /**
     * error causing the close
     * <p>
     * If set, this field indicates that the Connection is being closed due to an exceptional
     * condition. The value of the field should contain details on the cause of the exception.
     * </p>
     */
    public AmqpConnectionError getException();

    public static class AmqpCloseBean implements AmqpClose{

        private AmqpCloseBuffer buffer;
        private AmqpCloseBean bean = this;
        private AmqpOptions options;
        private AmqpConnectionError exception;

        AmqpCloseBean() {
        }

        AmqpCloseBean(IAmqpList value) {

        for(int i = 0; i < value.getListCount(); i++) {
            set(i, value.get(i));
        }
    }

    AmqpCloseBean(AmqpClose.AmqpCloseBean other) {
        this.bean = other;
    }

    public final AmqpCloseBean copy() {
        return new AmqpClose.AmqpCloseBean(bean);
    }

    public final void handle(AmqpCommandHandler handler) throws Exception {
        handler.handleClose(this);
    }

    public final AmqpClose.AmqpCloseBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
        if(buffer == null) {
            buffer = new AmqpCloseBuffer(marshaller.encode(this));
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

    public final void setException(AmqpConnectionError exception) {
        copyCheck();
        bean.exception = exception;
    }

    public final AmqpConnectionError getException() {
        return bean.exception;
    }

    public void set(int index, AmqpType<?, ?> value) {
        switch(index) {
        case 0: {
            setOptions((AmqpOptions) value);
            break;
        }
        case 1: {
            setException((AmqpConnectionError) value);
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
            return bean.exception;
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

    private final void copy(AmqpClose.AmqpCloseBean other) {
        bean = this;
    }

    public boolean equals(Object o){
        if(this == o) {
            return true;
        }

        if(o == null || !(o instanceof AmqpClose)) {
            return false;
        }

        return equals((AmqpClose) o);
    }

    public boolean equals(AmqpClose b) {

        if(b.getOptions() == null ^ getOptions() == null) {
            return false;
        }
        if(b.getOptions() != null && !b.getOptions().equals(getOptions())){ 
            return false;
        }

        if(b.getException() == null ^ getException() == null) {
            return false;
        }
        if(b.getException() != null && !b.getException().equals(getException())){ 
            return false;
        }
        return true;
    }

    public int hashCode() {
        return AbstractAmqpList.hashCodeFor(this);
    }
}

    public static class AmqpCloseBuffer extends AmqpList.AmqpListBuffer implements AmqpClose{

        private AmqpCloseBean bean;

        protected AmqpCloseBuffer(Encoded<IAmqpList> encoded) {
            super(encoded);
        }

        public final void setOptions(AmqpOptions options) {
            bean().setOptions(options);
        }

        public final AmqpOptions getOptions() {
            return bean().getOptions();
        }

        public final void setException(AmqpConnectionError exception) {
            bean().setException(exception);
        }

        public final AmqpConnectionError getException() {
            return bean().getException();
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

        public AmqpClose.AmqpCloseBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpClose bean() {
            if(bean == null) {
                bean = new AmqpClose.AmqpCloseBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public final void handle(AmqpCommandHandler handler) throws Exception {
            handler.handleClose(this);
        }

        public boolean equals(Object o){
            return bean().equals(o);
        }

        public boolean equals(AmqpClose o){
            return bean().equals(o);
        }

        public int hashCode() {
            return bean().hashCode();
        }

        public static AmqpClose.AmqpCloseBuffer create(Encoded<IAmqpList> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpClose.AmqpCloseBuffer(encoded);
        }

        public static AmqpClose.AmqpCloseBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpClose(in));
        }

        public static AmqpClose.AmqpCloseBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpClose(buffer, offset));
        }
    }
}