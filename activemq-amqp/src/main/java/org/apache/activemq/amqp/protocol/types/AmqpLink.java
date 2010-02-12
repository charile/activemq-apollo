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
import org.apache.activemq.amqp.protocol.AmqpCommand;
import org.apache.activemq.amqp.protocol.AmqpCommandHandler;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpMarshaller;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.amqp.protocol.types.IAmqpList;
import org.apache.activemq.util.buffer.Buffer;

/**
 * Represents a carries the local state and desired remote state of a Link
 * <p>
 * command confirming the state update.
 * </p>
 */
public interface AmqpLink extends AmqpList, AmqpCommand {



    /**
     * options map
     */
    public void setOptions(AmqpOptions options);

    /**
     * options map
     */
    public AmqpOptions getOptions();

    /**
     * the name of the Link
     * <p>
     * field. Link commands with the same name but differing scope refer to
     * distinct Links.
     * </p>
     */
    public void setName(String name);

    /**
     * the name of the Link
     * <p>
     * field. Link commands with the same name but differing scope refer to
     * distinct Links.
     * </p>
     */
    public void setName(AmqpString name);

    /**
     * the name of the Link
     * <p>
     * field. Link commands with the same name but differing scope refer to
     * distinct Links.
     * </p>
     */
    public String getName();

    /**
     * indicates the scope and lifespan of the Link
     * <p>
     * Container-scoped Links must be established with a container-unique name. When a
     * container-scoped Link's Session is closed, the Link endpoints will continue to exist
     * while the source and/or target continues to exist. A session-scoped Link will be closed
     * automatically when the Session terminates. A session-scoped Link name need only be
     * unique within the Session.
     * </p>
     */
    public void setScope(AmqpScope scope);

    /**
     * indicates the scope and lifespan of the Link
     * <p>
     * Container-scoped Links must be established with a container-unique name. When a
     * container-scoped Link's Session is closed, the Link endpoints will continue to exist
     * while the source and/or target continues to exist. A session-scoped Link will be closed
     * automatically when the Session terminates. A session-scoped Link name need only be
     * unique within the Session.
     * </p>
     */
    public AmqpScope getScope();

    /**
     * the Link handle
     * <p>
     * This field establishes the handle this endpoint will use to refer to the Link in all
     * subsequent outgoing commands.
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
     * the Link handle
     * <p>
     * This field establishes the handle this endpoint will use to refer to the Link in all
     * subsequent outgoing commands.
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
     * the Link handle
     * <p>
     * This field establishes the handle this endpoint will use to refer to the Link in all
     * subsequent outgoing commands.
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
     * the Link handle
     * <p>
     * This field establishes the handle this endpoint will use to refer to the Link in all
     * subsequent outgoing commands.
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
     * direction of the Link
     */
    public void setDirection(AmqpDirection direction);

    /**
     * direction of the Link
     */
    public AmqpDirection getDirection();

    /**
     * the source for Messages
     * <p>
     * If no source is specified on an outgoing Link, then there is no source currently
     * attached to the Link. A Link with no source will never produce outgoing Messages.
     * </p>
     */
    public void setSource(AmqpMap source);

    /**
     * the source for Messages
     * <p>
     * If no source is specified on an outgoing Link, then there is no source currently
     * attached to the Link. A Link with no source will never produce outgoing Messages.
     * </p>
     */
    public IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> getSource();

    /**
     * the target for Messages
     * <p>
     * If no target is specified on an incoming Link, then there is no target currently
     * attached to the Link. A Link with no target will never permit incoming Messages.
     * </p>
     */
    public void setTarget(AmqpMap target);

    /**
     * the target for Messages
     * <p>
     * If no target is specified on an incoming Link, then there is no target currently
     * attached to the Link. A Link with no target will never permit incoming Messages.
     * </p>
     */
    public IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> getTarget();

    /**
     * the transfer unit
     * <p>
     * The transfer-unit defines how the transfer-count is incremented as message data is sent.
     * </p>
     * <p>
     * the encoded
     * representation of the payload field.)
     * </p>
     * <p>
     * The transfer-count is incremented each time a complete message is sent.
     * </p>
     * <p>
     * The transfer-count is incremented for every transfer-unit bytes of incomplete message
     * payload sent.
     * </p>
     * <p>
     * This means a message with a payload size of S on a link with a transfer-unit of T will
     * increment the transfer-count (when completely transferred) by S/T rounded up to the
     * nearest integer.
     * </p>
     * <p>
     * If the transfer-unit is zero or unset, it is taken to be infinitely large, i.e. the
     * transfer-count is only incremented each time a complete message is sent.
     * </p>
     * <p>
     * The transfer-unit need only be set by the incoming link endpoint. Any value set by an
     * outgoing link endpoint is ignored.
     * </p>
     */
    public void setTransferUnit(BigInteger transferUnit);

    /**
     * the transfer unit
     * <p>
     * The transfer-unit defines how the transfer-count is incremented as message data is sent.
     * </p>
     * <p>
     * the encoded
     * representation of the payload field.)
     * </p>
     * <p>
     * The transfer-count is incremented each time a complete message is sent.
     * </p>
     * <p>
     * The transfer-count is incremented for every transfer-unit bytes of incomplete message
     * payload sent.
     * </p>
     * <p>
     * This means a message with a payload size of S on a link with a transfer-unit of T will
     * increment the transfer-count (when completely transferred) by S/T rounded up to the
     * nearest integer.
     * </p>
     * <p>
     * If the transfer-unit is zero or unset, it is taken to be infinitely large, i.e. the
     * transfer-count is only incremented each time a complete message is sent.
     * </p>
     * <p>
     * The transfer-unit need only be set by the incoming link endpoint. Any value set by an
     * outgoing link endpoint is ignored.
     * </p>
     */
    public void setTransferUnit(AmqpUlong transferUnit);

    /**
     * the transfer unit
     * <p>
     * The transfer-unit defines how the transfer-count is incremented as message data is sent.
     * </p>
     * <p>
     * the encoded
     * representation of the payload field.)
     * </p>
     * <p>
     * The transfer-count is incremented each time a complete message is sent.
     * </p>
     * <p>
     * The transfer-count is incremented for every transfer-unit bytes of incomplete message
     * payload sent.
     * </p>
     * <p>
     * This means a message with a payload size of S on a link with a transfer-unit of T will
     * increment the transfer-count (when completely transferred) by S/T rounded up to the
     * nearest integer.
     * </p>
     * <p>
     * If the transfer-unit is zero or unset, it is taken to be infinitely large, i.e. the
     * transfer-count is only incremented each time a complete message is sent.
     * </p>
     * <p>
     * The transfer-unit need only be set by the incoming link endpoint. Any value set by an
     * outgoing link endpoint is ignored.
     * </p>
     */
    public BigInteger getTransferUnit();

    /**
     * the suggested point to resume delivery
     * <p>
     * On resuming a link, both the outgoing and incoming endpoints may have retained state.
     * The outgoing link endpoint may retain the state of deliveries that have not been
     * finalized, and the incoming link endpoint may retain the state of deliveries that have
     * not been settled. In this case the outgoing endpoint sets the resume-tag to the oldest
     * non-finalized delivery, and the incoming endpoint sets the resume-tag to the newest
     * unsettled delivery.
     * </p>
     * <p>
     * If the outgoing endpoint sets the resume-tag then it MUST also set its barrier to the
     * same value.
     * </p>
     * <p>
     * If the resume-tag supplied by the incoming endpoint is among the non-finalized
     * deliveries which the outgoing endpoint retains, then the outgoing endpoint MUST resume
     * sending at the point after the delivery specified by the incoming resume-tag. Otherwise
     * the outgoing endpoint MUST resume from the delivery indicated by the outgoing
     * resume-tag.
     * </p>
     * <p>
     * The default presumptive disposition MUST be restored before resending deliveries with a
     * non-default presumptive disposition.
     * </p>
     */
    public void setResumeTag(Buffer resumeTag);

    /**
     * the suggested point to resume delivery
     * <p>
     * On resuming a link, both the outgoing and incoming endpoints may have retained state.
     * The outgoing link endpoint may retain the state of deliveries that have not been
     * finalized, and the incoming link endpoint may retain the state of deliveries that have
     * not been settled. In this case the outgoing endpoint sets the resume-tag to the oldest
     * non-finalized delivery, and the incoming endpoint sets the resume-tag to the newest
     * unsettled delivery.
     * </p>
     * <p>
     * If the outgoing endpoint sets the resume-tag then it MUST also set its barrier to the
     * same value.
     * </p>
     * <p>
     * If the resume-tag supplied by the incoming endpoint is among the non-finalized
     * deliveries which the outgoing endpoint retains, then the outgoing endpoint MUST resume
     * sending at the point after the delivery specified by the incoming resume-tag. Otherwise
     * the outgoing endpoint MUST resume from the delivery indicated by the outgoing
     * resume-tag.
     * </p>
     * <p>
     * The default presumptive disposition MUST be restored before resending deliveries with a
     * non-default presumptive disposition.
     * </p>
     */
    public void setResumeTag(AmqpDeliveryTag resumeTag);

    /**
     * the suggested point to resume delivery
     * <p>
     * On resuming a link, both the outgoing and incoming endpoints may have retained state.
     * The outgoing link endpoint may retain the state of deliveries that have not been
     * finalized, and the incoming link endpoint may retain the state of deliveries that have
     * not been settled. In this case the outgoing endpoint sets the resume-tag to the oldest
     * non-finalized delivery, and the incoming endpoint sets the resume-tag to the newest
     * unsettled delivery.
     * </p>
     * <p>
     * If the outgoing endpoint sets the resume-tag then it MUST also set its barrier to the
     * same value.
     * </p>
     * <p>
     * If the resume-tag supplied by the incoming endpoint is among the non-finalized
     * deliveries which the outgoing endpoint retains, then the outgoing endpoint MUST resume
     * sending at the point after the delivery specified by the incoming resume-tag. Otherwise
     * the outgoing endpoint MUST resume from the delivery indicated by the outgoing
     * resume-tag.
     * </p>
     * <p>
     * The default presumptive disposition MUST be restored before resending deliveries with a
     * non-default presumptive disposition.
     * </p>
     */
    public AmqpDeliveryTag getResumeTag();

    public static class AmqpLinkBean implements AmqpLink{

        private AmqpLinkBuffer buffer;
        private AmqpLinkBean bean = this;
        private AmqpOptions options;
        private AmqpString name;
        private AmqpScope scope;
        private AmqpHandle handle;
        private AmqpDirection direction;
        private AmqpMap source;
        private AmqpMap target;
        private AmqpUlong transferUnit;
        private AmqpDeliveryTag resumeTag;

        AmqpLinkBean() {
        }

        AmqpLinkBean(IAmqpList value) {

        for(int i = 0; i < value.getListCount(); i++) {
            set(i, value.get(i));
        }
    }

    AmqpLinkBean(AmqpLink.AmqpLinkBean other) {
        this.bean = other;
    }

    public final AmqpLinkBean copy() {
        return new AmqpLink.AmqpLinkBean(bean);
    }

    public final void handle(AmqpCommandHandler handler) throws Exception {
        handler.handleLink(this);
    }

    public final AmqpLink.AmqpLinkBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
        if(buffer == null) {
            buffer = new AmqpLinkBuffer(marshaller.encode(this));
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

    public void setName(String name) {
        setName(TypeFactory.createAmqpString(name));
    }


    public final void setName(AmqpString name) {
        copyCheck();
        bean.name = name;
    }

    public final String getName() {
        return bean.name.getValue();
    }

    public final void setScope(AmqpScope scope) {
        copyCheck();
        bean.scope = scope;
    }

    public final AmqpScope getScope() {
        return bean.scope;
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

    public final void setDirection(AmqpDirection direction) {
        copyCheck();
        bean.direction = direction;
    }

    public final AmqpDirection getDirection() {
        return bean.direction;
    }

    public final void setSource(AmqpMap source) {
        copyCheck();
        bean.source = source;
    }

    public final IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> getSource() {
        return bean.source.getValue();
    }

    public final void setTarget(AmqpMap target) {
        copyCheck();
        bean.target = target;
    }

    public final IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> getTarget() {
        return bean.target.getValue();
    }

    public void setTransferUnit(BigInteger transferUnit) {
        setTransferUnit(TypeFactory.createAmqpUlong(transferUnit));
    }


    public final void setTransferUnit(AmqpUlong transferUnit) {
        copyCheck();
        bean.transferUnit = transferUnit;
    }

    public final BigInteger getTransferUnit() {
        return bean.transferUnit.getValue();
    }

    public void setResumeTag(Buffer resumeTag) {
        setResumeTag(TypeFactory.createAmqpDeliveryTag(resumeTag));
    }


    public final void setResumeTag(AmqpDeliveryTag resumeTag) {
        copyCheck();
        bean.resumeTag = resumeTag;
    }

    public final AmqpDeliveryTag getResumeTag() {
        return bean.resumeTag;
    }

    public void set(int index, AmqpType<?, ?> value) {
        switch(index) {
        case 0: {
            setOptions((AmqpOptions) value);
            break;
        }
        case 1: {
            setName((AmqpString) value);
            break;
        }
        case 2: {
            setScope(AmqpScope.get((AmqpUbyte)value));
            break;
        }
        case 3: {
            setHandle((AmqpHandle) value);
            break;
        }
        case 4: {
            setDirection(AmqpDirection.get((AmqpUbyte)value));
            break;
        }
        case 5: {
            setSource((AmqpMap) value);
            break;
        }
        case 6: {
            setTarget((AmqpMap) value);
            break;
        }
        case 7: {
            setTransferUnit((AmqpUlong) value);
            break;
        }
        case 8: {
            setResumeTag((AmqpDeliveryTag) value);
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
            return bean.name;
        }
        case 2: {
            if(scope == null) {
                return null;
            }
            return scope.getValue();
        }
        case 3: {
            return bean.handle;
        }
        case 4: {
            if(direction == null) {
                return null;
            }
            return direction.getValue();
        }
        case 5: {
            return bean.source;
        }
        case 6: {
            return bean.target;
        }
        case 7: {
            return bean.transferUnit;
        }
        case 8: {
            return bean.resumeTag;
        }
        default : {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        }
    }

    public int getListCount() {
        return 9;
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

    private final void copy(AmqpLink.AmqpLinkBean other) {
        bean = this;
    }

    public boolean equals(Object o){
        if(this == o) {
            return true;
        }

        if(o == null || !(o instanceof AmqpLink)) {
            return false;
        }

        return equals((AmqpLink) o);
    }

    public boolean equals(AmqpLink b) {

        if(b.getOptions() == null ^ getOptions() == null) {
            return false;
        }
        if(b.getOptions() != null && !b.getOptions().equals(getOptions())){ 
            return false;
        }

        if(b.getName() == null ^ getName() == null) {
            return false;
        }
        if(b.getName() != null && !b.getName().equals(getName())){ 
            return false;
        }

        if(b.getScope() == null ^ getScope() == null) {
            return false;
        }
        if(b.getScope() != null && !b.getScope().equals(getScope())){ 
            return false;
        }

        if(b.getHandle() == null ^ getHandle() == null) {
            return false;
        }
        if(b.getHandle() != null && !b.getHandle().equals(getHandle())){ 
            return false;
        }

        if(b.getDirection() == null ^ getDirection() == null) {
            return false;
        }
        if(b.getDirection() != null && !b.getDirection().equals(getDirection())){ 
            return false;
        }

        if(b.getSource() == null ^ getSource() == null) {
            return false;
        }
        if(b.getSource() != null && !b.getSource().equals(getSource())){ 
            return false;
        }

        if(b.getTarget() == null ^ getTarget() == null) {
            return false;
        }
        if(b.getTarget() != null && !b.getTarget().equals(getTarget())){ 
            return false;
        }

        if(b.getTransferUnit() == null ^ getTransferUnit() == null) {
            return false;
        }
        if(b.getTransferUnit() != null && !b.getTransferUnit().equals(getTransferUnit())){ 
            return false;
        }

        if(b.getResumeTag() == null ^ getResumeTag() == null) {
            return false;
        }
        if(b.getResumeTag() != null && !b.getResumeTag().equals(getResumeTag())){ 
            return false;
        }
        return true;
    }

    public int hashCode() {
        return AbstractAmqpList.hashCodeFor(this);
    }
}

    public static class AmqpLinkBuffer extends AmqpList.AmqpListBuffer implements AmqpLink{

        private AmqpLinkBean bean;

        protected AmqpLinkBuffer(Encoded<IAmqpList> encoded) {
            super(encoded);
        }

        public final void setOptions(AmqpOptions options) {
            bean().setOptions(options);
        }

        public final AmqpOptions getOptions() {
            return bean().getOptions();
        }

        public void setName(String name) {
            bean().setName(name);
        }

        public final void setName(AmqpString name) {
            bean().setName(name);
        }

        public final String getName() {
            return bean().getName();
        }

        public final void setScope(AmqpScope scope) {
            bean().setScope(scope);
        }

        public final AmqpScope getScope() {
            return bean().getScope();
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

        public final void setDirection(AmqpDirection direction) {
            bean().setDirection(direction);
        }

        public final AmqpDirection getDirection() {
            return bean().getDirection();
        }

        public final void setSource(AmqpMap source) {
            bean().setSource(source);
        }

        public final IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> getSource() {
            return bean().getSource();
        }

        public final void setTarget(AmqpMap target) {
            bean().setTarget(target);
        }

        public final IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> getTarget() {
            return bean().getTarget();
        }

        public void setTransferUnit(BigInteger transferUnit) {
            bean().setTransferUnit(transferUnit);
        }

        public final void setTransferUnit(AmqpUlong transferUnit) {
            bean().setTransferUnit(transferUnit);
        }

        public final BigInteger getTransferUnit() {
            return bean().getTransferUnit();
        }

        public void setResumeTag(Buffer resumeTag) {
            bean().setResumeTag(resumeTag);
        }

        public final void setResumeTag(AmqpDeliveryTag resumeTag) {
            bean().setResumeTag(resumeTag);
        }

        public final AmqpDeliveryTag getResumeTag() {
            return bean().getResumeTag();
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

        public AmqpLink.AmqpLinkBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpLink bean() {
            if(bean == null) {
                bean = new AmqpLink.AmqpLinkBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public final void handle(AmqpCommandHandler handler) throws Exception {
            handler.handleLink(this);
        }

        public boolean equals(Object o){
            return bean().equals(o);
        }

        public boolean equals(AmqpLink o){
            return bean().equals(o);
        }

        public int hashCode() {
            return bean().hashCode();
        }

        public static AmqpLink.AmqpLinkBuffer create(Encoded<IAmqpList> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpLink.AmqpLinkBuffer(encoded);
        }

        public static AmqpLink.AmqpLinkBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpLink(in));
        }

        public static AmqpLink.AmqpLinkBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpLink(buffer, offset));
        }
    }
}