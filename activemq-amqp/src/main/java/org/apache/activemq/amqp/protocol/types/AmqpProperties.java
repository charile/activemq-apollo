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
import java.math.BigInteger;
import java.util.Iterator;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpMarshaller;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.amqp.protocol.types.IAmqpList;
import org.apache.activemq.util.buffer.Buffer;

/**
 * Represents a immutable properties of the Message
 * <p>
 * Message properties carry information about the Message.
 * </p>
 */
public interface AmqpProperties extends AmqpList {



    /**
     * application Message identifier
     * <p>
     * Message-id is an optional property which uniquely identifies a Message within the
     * Message system. The Message producer is usually responsible for setting the message-id
     * in such a way that it is assured to be globally unique. The server MAY discard a Message
     * as a duplicate if the value of the message-id matches that of a previously received
     * Message sent to the same Node.
     * </p>
     */
    public void setMessageId(Buffer messageId);

    /**
     * application Message identifier
     * <p>
     * Message-id is an optional property which uniquely identifies a Message within the
     * Message system. The Message producer is usually responsible for setting the message-id
     * in such a way that it is assured to be globally unique. The server MAY discard a Message
     * as a duplicate if the value of the message-id matches that of a previously received
     * Message sent to the same Node.
     * </p>
     */
    public void setMessageId(AmqpBinary messageId);

    /**
     * application Message identifier
     * <p>
     * Message-id is an optional property which uniquely identifies a Message within the
     * Message system. The Message producer is usually responsible for setting the message-id
     * in such a way that it is assured to be globally unique. The server MAY discard a Message
     * as a duplicate if the value of the message-id matches that of a previously received
     * Message sent to the same Node.
     * </p>
     */
    public Buffer getMessageId();

    /**
     * creating user id
     * <p>
     * The identity of the user responsible for producing the Message. The client sets this
     * value, and it MAY be authenticated by intermediaries.
     * </p>
     */
    public void setUserId(Buffer userId);

    /**
     * creating user id
     * <p>
     * The identity of the user responsible for producing the Message. The client sets this
     * value, and it MAY be authenticated by intermediaries.
     * </p>
     */
    public void setUserId(AmqpBinary userId);

    /**
     * creating user id
     * <p>
     * The identity of the user responsible for producing the Message. The client sets this
     * value, and it MAY be authenticated by intermediaries.
     * </p>
     */
    public Buffer getUserId();

    /**
     * the name of the Node the Message is destined for
     * <p>
     * The to field identifies the Node that is the intended destination of the Message. On any
     * given transfer this may not be the Node at the receiving end of the Link.
     * </p>
     */
    public void setTo(String to);

    /**
     * the name of the Node the Message is destined for
     * <p>
     * The to field identifies the Node that is the intended destination of the Message. On any
     * given transfer this may not be the Node at the receiving end of the Link.
     * </p>
     */
    public void setTo(AmqpString to);

    /**
     * the name of the Node the Message is destined for
     * <p>
     * The to field identifies the Node that is the intended destination of the Message. On any
     * given transfer this may not be the Node at the receiving end of the Link.
     * </p>
     */
    public String getTo();

    /**
     * the Node to send replies to
     * <p>
     * The name of the Node to send replies to.
     * </p>
     */
    public void setReplyTo(String replyTo);

    /**
     * the Node to send replies to
     * <p>
     * The name of the Node to send replies to.
     * </p>
     */
    public void setReplyTo(AmqpString replyTo);

    /**
     * the Node to send replies to
     * <p>
     * The name of the Node to send replies to.
     * </p>
     */
    public String getReplyTo();

    /**
     * application correlation identifier
     * <p>
     * This is a client-specific id that may be used to mark or identify Messages between
     * clients. The server ignores this field.
     * </p>
     */
    public void setCorrelationId(Buffer correlationId);

    /**
     * application correlation identifier
     * <p>
     * This is a client-specific id that may be used to mark or identify Messages between
     * clients. The server ignores this field.
     * </p>
     */
    public void setCorrelationId(AmqpBinary correlationId);

    /**
     * application correlation identifier
     * <p>
     * This is a client-specific id that may be used to mark or identify Messages between
     * clients. The server ignores this field.
     * </p>
     */
    public Buffer getCorrelationId();

    /**
     * length of the combined payload in bytes
     * <p>
     * The total size in octets of the combined payload of all transfer commands that together
     * make the Message.
     * </p>
     */
    public void setContentLength(BigInteger contentLength);

    /**
     * length of the combined payload in bytes
     * <p>
     * The total size in octets of the combined payload of all transfer commands that together
     * make the Message.
     * </p>
     */
    public void setContentLength(AmqpUlong contentLength);

    /**
     * length of the combined payload in bytes
     * <p>
     * The total size in octets of the combined payload of all transfer commands that together
     * make the Message.
     * </p>
     */
    public BigInteger getContentLength();

    /**
     * MIME content type
     * <p>
     * The RFC-2046 MIME type for the Message content (such as "text/plain"). This is set by
     * the originating client. As per RFC-2046 this may contain a charset parameter defining
     * the character encoding used: e.g. 'text/plain; charset="utf-8"'.
     * </p>
     * <p>
     * Symbols are values from a constrained domain. Although the set of possible domains is
     * open-ended, typically the both number and size of symbols in use for any given application
     * will be small, e.g. small enough that it is reasonable to cache all the distinct values.
     * </p>
     */
    public void setContentType(String contentType);

    /**
     * MIME content type
     * <p>
     * The RFC-2046 MIME type for the Message content (such as "text/plain"). This is set by
     * the originating client. As per RFC-2046 this may contain a charset parameter defining
     * the character encoding used: e.g. 'text/plain; charset="utf-8"'.
     * </p>
     * <p>
     * Symbols are values from a constrained domain. Although the set of possible domains is
     * open-ended, typically the both number and size of symbols in use for any given application
     * will be small, e.g. small enough that it is reasonable to cache all the distinct values.
     * </p>
     */
    public void setContentType(AmqpSymbol contentType);

    /**
     * MIME content type
     * <p>
     * The RFC-2046 MIME type for the Message content (such as "text/plain"). This is set by
     * the originating client. As per RFC-2046 this may contain a charset parameter defining
     * the character encoding used: e.g. 'text/plain; charset="utf-8"'.
     * </p>
     * <p>
     * Symbols are values from a constrained domain. Although the set of possible domains is
     * open-ended, typically the both number and size of symbols in use for any given application
     * will be small, e.g. small enough that it is reasonable to cache all the distinct values.
     * </p>
     */
    public String getContentType();

    public static class AmqpPropertiesBean implements AmqpProperties{

        private AmqpPropertiesBuffer buffer;
        private AmqpPropertiesBean bean = this;
        private AmqpBinary messageId;
        private AmqpBinary userId;
        private AmqpString to;
        private AmqpString replyTo;
        private AmqpBinary correlationId;
        private AmqpUlong contentLength;
        private AmqpSymbol contentType;

        AmqpPropertiesBean() {
        }

        AmqpPropertiesBean(IAmqpList value) {

        for(int i = 0; i < value.getListCount(); i++) {
            set(i, value.get(i));
        }
    }

    AmqpPropertiesBean(AmqpProperties.AmqpPropertiesBean other) {
        this.bean = other;
    }

    public final AmqpPropertiesBean copy() {
        return new AmqpProperties.AmqpPropertiesBean(bean);
    }

    public final AmqpProperties.AmqpPropertiesBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
        if(buffer == null) {
            buffer = new AmqpPropertiesBuffer(marshaller.encode(this));
        }
        return buffer;
    }

    public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
        getBuffer(marshaller).marshal(out, marshaller);
    }


    public void setMessageId(Buffer messageId) {
        setMessageId(TypeFactory.createAmqpBinary(messageId));
    }


    public final void setMessageId(AmqpBinary messageId) {
        copyCheck();
        bean.messageId = messageId;
    }

    public final Buffer getMessageId() {
        return bean.messageId.getValue();
    }

    public void setUserId(Buffer userId) {
        setUserId(TypeFactory.createAmqpBinary(userId));
    }


    public final void setUserId(AmqpBinary userId) {
        copyCheck();
        bean.userId = userId;
    }

    public final Buffer getUserId() {
        return bean.userId.getValue();
    }

    public void setTo(String to) {
        setTo(TypeFactory.createAmqpString(to));
    }


    public final void setTo(AmqpString to) {
        copyCheck();
        bean.to = to;
    }

    public final String getTo() {
        return bean.to.getValue();
    }

    public void setReplyTo(String replyTo) {
        setReplyTo(TypeFactory.createAmqpString(replyTo));
    }


    public final void setReplyTo(AmqpString replyTo) {
        copyCheck();
        bean.replyTo = replyTo;
    }

    public final String getReplyTo() {
        return bean.replyTo.getValue();
    }

    public void setCorrelationId(Buffer correlationId) {
        setCorrelationId(TypeFactory.createAmqpBinary(correlationId));
    }


    public final void setCorrelationId(AmqpBinary correlationId) {
        copyCheck();
        bean.correlationId = correlationId;
    }

    public final Buffer getCorrelationId() {
        return bean.correlationId.getValue();
    }

    public void setContentLength(BigInteger contentLength) {
        setContentLength(TypeFactory.createAmqpUlong(contentLength));
    }


    public final void setContentLength(AmqpUlong contentLength) {
        copyCheck();
        bean.contentLength = contentLength;
    }

    public final BigInteger getContentLength() {
        return bean.contentLength.getValue();
    }

    public void setContentType(String contentType) {
        setContentType(TypeFactory.createAmqpSymbol(contentType));
    }


    public final void setContentType(AmqpSymbol contentType) {
        copyCheck();
        bean.contentType = contentType;
    }

    public final String getContentType() {
        return bean.contentType.getValue();
    }

    public void set(int index, AmqpType<?, ?> value) {
        switch(index) {
        case 0: {
            setMessageId((AmqpBinary) value);
            break;
        }
        case 1: {
            setUserId((AmqpBinary) value);
            break;
        }
        case 2: {
            setTo((AmqpString) value);
            break;
        }
        case 3: {
            setReplyTo((AmqpString) value);
            break;
        }
        case 4: {
            setCorrelationId((AmqpBinary) value);
            break;
        }
        case 5: {
            setContentLength((AmqpUlong) value);
            break;
        }
        case 6: {
            setContentType((AmqpSymbol) value);
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
            return bean.messageId;
        }
        case 1: {
            return bean.userId;
        }
        case 2: {
            return bean.to;
        }
        case 3: {
            return bean.replyTo;
        }
        case 4: {
            return bean.correlationId;
        }
        case 5: {
            return bean.contentLength;
        }
        case 6: {
            return bean.contentType;
        }
        default : {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        }
    }

    public int getListCount() {
        return 7;
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

    private final void copy(AmqpProperties.AmqpPropertiesBean other) {
        bean = this;
    }

    public boolean equals(Object o){
        if(this == o) {
            return true;
        }

        if(o == null || !(o instanceof AmqpProperties)) {
            return false;
        }

        return equals((AmqpProperties) o);
    }

    public boolean equals(AmqpProperties b) {

        if(b.getMessageId() == null ^ getMessageId() == null) {
            return false;
        }
        if(b.getMessageId() != null && !b.getMessageId().equals(getMessageId())){ 
            return false;
        }

        if(b.getUserId() == null ^ getUserId() == null) {
            return false;
        }
        if(b.getUserId() != null && !b.getUserId().equals(getUserId())){ 
            return false;
        }

        if(b.getTo() == null ^ getTo() == null) {
            return false;
        }
        if(b.getTo() != null && !b.getTo().equals(getTo())){ 
            return false;
        }

        if(b.getReplyTo() == null ^ getReplyTo() == null) {
            return false;
        }
        if(b.getReplyTo() != null && !b.getReplyTo().equals(getReplyTo())){ 
            return false;
        }

        if(b.getCorrelationId() == null ^ getCorrelationId() == null) {
            return false;
        }
        if(b.getCorrelationId() != null && !b.getCorrelationId().equals(getCorrelationId())){ 
            return false;
        }

        if(b.getContentLength() == null ^ getContentLength() == null) {
            return false;
        }
        if(b.getContentLength() != null && !b.getContentLength().equals(getContentLength())){ 
            return false;
        }

        if(b.getContentType() == null ^ getContentType() == null) {
            return false;
        }
        if(b.getContentType() != null && !b.getContentType().equals(getContentType())){ 
            return false;
        }
        return true;
    }

    public int hashCode() {
        return AbstractAmqpList.hashCodeFor(this);
    }
}

    public static class AmqpPropertiesBuffer extends AmqpList.AmqpListBuffer implements AmqpProperties{

        private AmqpPropertiesBean bean;

        protected AmqpPropertiesBuffer(Encoded<IAmqpList> encoded) {
            super(encoded);
        }

        public void setMessageId(Buffer messageId) {
            bean().setMessageId(messageId);
        }

        public final void setMessageId(AmqpBinary messageId) {
            bean().setMessageId(messageId);
        }

        public final Buffer getMessageId() {
            return bean().getMessageId();
        }

        public void setUserId(Buffer userId) {
            bean().setUserId(userId);
        }

        public final void setUserId(AmqpBinary userId) {
            bean().setUserId(userId);
        }

        public final Buffer getUserId() {
            return bean().getUserId();
        }

        public void setTo(String to) {
            bean().setTo(to);
        }

        public final void setTo(AmqpString to) {
            bean().setTo(to);
        }

        public final String getTo() {
            return bean().getTo();
        }

        public void setReplyTo(String replyTo) {
            bean().setReplyTo(replyTo);
        }

        public final void setReplyTo(AmqpString replyTo) {
            bean().setReplyTo(replyTo);
        }

        public final String getReplyTo() {
            return bean().getReplyTo();
        }

        public void setCorrelationId(Buffer correlationId) {
            bean().setCorrelationId(correlationId);
        }

        public final void setCorrelationId(AmqpBinary correlationId) {
            bean().setCorrelationId(correlationId);
        }

        public final Buffer getCorrelationId() {
            return bean().getCorrelationId();
        }

        public void setContentLength(BigInteger contentLength) {
            bean().setContentLength(contentLength);
        }

        public final void setContentLength(AmqpUlong contentLength) {
            bean().setContentLength(contentLength);
        }

        public final BigInteger getContentLength() {
            return bean().getContentLength();
        }

        public void setContentType(String contentType) {
            bean().setContentType(contentType);
        }

        public final void setContentType(AmqpSymbol contentType) {
            bean().setContentType(contentType);
        }

        public final String getContentType() {
            return bean().getContentType();
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

        public AmqpProperties.AmqpPropertiesBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpProperties bean() {
            if(bean == null) {
                bean = new AmqpProperties.AmqpPropertiesBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public boolean equals(Object o){
            return bean().equals(o);
        }

        public boolean equals(AmqpProperties o){
            return bean().equals(o);
        }

        public int hashCode() {
            return bean().hashCode();
        }

        public static AmqpProperties.AmqpPropertiesBuffer create(Encoded<IAmqpList> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpProperties.AmqpPropertiesBuffer(encoded);
        }

        public static AmqpProperties.AmqpPropertiesBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpProperties(in));
        }

        public static AmqpProperties.AmqpPropertiesBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpProperties(buffer, offset));
        }
    }
}