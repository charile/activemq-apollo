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
import java.lang.Integer;
import java.lang.Long;
import java.lang.String;
import java.util.Iterator;
import org.apache.activemq.amqp.protocol.AmqpCommand;
import org.apache.activemq.amqp.protocol.AmqpCommandHandler;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpMarshaller;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.amqp.protocol.types.IAmqpList;
import org.apache.activemq.util.buffer.Buffer;

/**
 * Represents a negotiate Connection parameters
 * <p>
 * The open control MUST be the first frame sent in each direction on the Connection. (Note
 * that the Connection header which is sent first on the Connection is *not* a frame.) The
 * fields indicate the capabilities and limitations of the sending peer.
 * </p>
 */
public interface AmqpOpen extends AmqpList, AmqpCommand {



    /**
     * options map
     */
    public void setOptions(AmqpOptions options);

    /**
     * options map
     */
    public AmqpOptions getOptions();

    /**
     * the id of the source container
     */
    public void setContainerId(String containerId);

    /**
     * the id of the source container
     */
    public void setContainerId(AmqpString containerId);

    /**
     * the id of the source container
     */
    public String getContainerId();

    /**
     * the name of the target host
     * <p>
     * The dns name of the host (either fully qualified or relative) to which the sending peer
     * is connecting. It is not mandatory to provide the hostname. If no hostname is provided
     * the receiving peer should select a default based on its own configuration.
     * </p>
     */
    public void setHostname(String hostname);

    /**
     * the name of the target host
     * <p>
     * The dns name of the host (either fully qualified or relative) to which the sending peer
     * is connecting. It is not mandatory to provide the hostname. If no hostname is provided
     * the receiving peer should select a default based on its own configuration.
     * </p>
     */
    public void setHostname(AmqpString hostname);

    /**
     * the name of the target host
     * <p>
     * The dns name of the host (either fully qualified or relative) to which the sending peer
     * is connecting. It is not mandatory to provide the hostname. If no hostname is provided
     * the receiving peer should select a default based on its own configuration.
     * </p>
     */
    public String getHostname();

    /**
     * proposed maximum frame size
     * <p>
     * The largest frame size that the sending peer is able to accept on this Connection. If
     * this field is not set it means that the peer does not impose any specific limit. A peer
     * MUST NOT send frames larger than its partner can handle. A peer that receives an
     * oversized frame MUST close the Connection with the framing-error error-code.
     * </p>
     * <p>
     * octets
     * large.
     * </p>
     */
    public void setMaxFrameSize(Long maxFrameSize);

    /**
     * proposed maximum frame size
     * <p>
     * The largest frame size that the sending peer is able to accept on this Connection. If
     * this field is not set it means that the peer does not impose any specific limit. A peer
     * MUST NOT send frames larger than its partner can handle. A peer that receives an
     * oversized frame MUST close the Connection with the framing-error error-code.
     * </p>
     * <p>
     * octets
     * large.
     * </p>
     */
    public void setMaxFrameSize(long maxFrameSize);

    /**
     * proposed maximum frame size
     * <p>
     * The largest frame size that the sending peer is able to accept on this Connection. If
     * this field is not set it means that the peer does not impose any specific limit. A peer
     * MUST NOT send frames larger than its partner can handle. A peer that receives an
     * oversized frame MUST close the Connection with the framing-error error-code.
     * </p>
     * <p>
     * octets
     * large.
     * </p>
     */
    public void setMaxFrameSize(AmqpUint maxFrameSize);

    /**
     * proposed maximum frame size
     * <p>
     * The largest frame size that the sending peer is able to accept on this Connection. If
     * this field is not set it means that the peer does not impose any specific limit. A peer
     * MUST NOT send frames larger than its partner can handle. A peer that receives an
     * oversized frame MUST close the Connection with the framing-error error-code.
     * </p>
     * <p>
     * octets
     * large.
     * </p>
     */
    public Long getMaxFrameSize();

    /**
     * the maximum channel number that may be used on the Connection
     * <p>
     * The channel-max value is the highest channel number that may be used on the Connection.
     * This value plus one is the maximum number of Sessions that may be simultaneously
     * attached. A peer MUST not use channel numbers outside the range that its partner can
     * handle. A peer that receives a channel number outside the supported range MUST close the
     * Connection with the framing-error error-code.
     * </p>
     */
    public void setChannelMax(Integer channelMax);

    /**
     * the maximum channel number that may be used on the Connection
     * <p>
     * The channel-max value is the highest channel number that may be used on the Connection.
     * This value plus one is the maximum number of Sessions that may be simultaneously
     * attached. A peer MUST not use channel numbers outside the range that its partner can
     * handle. A peer that receives a channel number outside the supported range MUST close the
     * Connection with the framing-error error-code.
     * </p>
     */
    public void setChannelMax(int channelMax);

    /**
     * the maximum channel number that may be used on the Connection
     * <p>
     * The channel-max value is the highest channel number that may be used on the Connection.
     * This value plus one is the maximum number of Sessions that may be simultaneously
     * attached. A peer MUST not use channel numbers outside the range that its partner can
     * handle. A peer that receives a channel number outside the supported range MUST close the
     * Connection with the framing-error error-code.
     * </p>
     */
    public void setChannelMax(AmqpUshort channelMax);

    /**
     * the maximum channel number that may be used on the Connection
     * <p>
     * The channel-max value is the highest channel number that may be used on the Connection.
     * This value plus one is the maximum number of Sessions that may be simultaneously
     * attached. A peer MUST not use channel numbers outside the range that its partner can
     * handle. A peer that receives a channel number outside the supported range MUST close the
     * Connection with the framing-error error-code.
     * </p>
     */
    public Integer getChannelMax();

    /**
     * proposed heartbeat interval
     * <p>
     * The proposed interval, in seconds, of the Connection heartbeat desired by the sender. A
     * value of zero means heartbeats are not supported. If the value is not set, the sender
     * supports all heartbeat intervals. The heartbeat-interval established is the minimum of
     * the two proposed heartbeat-intervals. If neither value is set, there is no heartbeat.
     * </p>
     */
    public void setHeartbeatInterval(Integer heartbeatInterval);

    /**
     * proposed heartbeat interval
     * <p>
     * The proposed interval, in seconds, of the Connection heartbeat desired by the sender. A
     * value of zero means heartbeats are not supported. If the value is not set, the sender
     * supports all heartbeat intervals. The heartbeat-interval established is the minimum of
     * the two proposed heartbeat-intervals. If neither value is set, there is no heartbeat.
     * </p>
     */
    public void setHeartbeatInterval(int heartbeatInterval);

    /**
     * proposed heartbeat interval
     * <p>
     * The proposed interval, in seconds, of the Connection heartbeat desired by the sender. A
     * value of zero means heartbeats are not supported. If the value is not set, the sender
     * supports all heartbeat intervals. The heartbeat-interval established is the minimum of
     * the two proposed heartbeat-intervals. If neither value is set, there is no heartbeat.
     * </p>
     */
    public void setHeartbeatInterval(AmqpUshort heartbeatInterval);

    /**
     * proposed heartbeat interval
     * <p>
     * The proposed interval, in seconds, of the Connection heartbeat desired by the sender. A
     * value of zero means heartbeats are not supported. If the value is not set, the sender
     * supports all heartbeat intervals. The heartbeat-interval established is the minimum of
     * the two proposed heartbeat-intervals. If neither value is set, there is no heartbeat.
     * </p>
     */
    public Integer getHeartbeatInterval();

    /**
     * locales available for outgoing text
     * <p>
     * A list of the locales that the peer supports for sending informational text. This
     * includes Connection, Session and Link exception text. The default is the en_US locale.
     * A peer MUST support at least the en_US locale. Since this value is always supported, it
     * need not be supplied in the outgoing-locales.
     * </p>
     */
    public void setOutgoingLocales(AmqpList outgoingLocales);

    /**
     * locales available for outgoing text
     * <p>
     * A list of the locales that the peer supports for sending informational text. This
     * includes Connection, Session and Link exception text. The default is the en_US locale.
     * A peer MUST support at least the en_US locale. Since this value is always supported, it
     * need not be supplied in the outgoing-locales.
     * </p>
     */
    public IAmqpList<AmqpType<?, ?>> getOutgoingLocales();

    /**
     * desired locales for incoming text in decreasing level of preference
     * <p>
     * A list of locales that the sending peer permits for incoming informational text. This
     * list is ordered in decreasing level of preference. The receiving partner will chose the
     * first (most preferred) incoming locale from those which it supports. If none of the
     * requested locales are supported, en_US will be chosen. Note that en_US need not be
     * supplied in this list as it is always the fallback. A peer may determine which of the
     * permitted incoming locales is chosen by examining the partner's supported locales as
     * specified in the outgoing-locales field.
     * </p>
     */
    public void setIncomingLocales(AmqpList incomingLocales);

    /**
     * desired locales for incoming text in decreasing level of preference
     * <p>
     * A list of locales that the sending peer permits for incoming informational text. This
     * list is ordered in decreasing level of preference. The receiving partner will chose the
     * first (most preferred) incoming locale from those which it supports. If none of the
     * requested locales are supported, en_US will be chosen. Note that en_US need not be
     * supplied in this list as it is always the fallback. A peer may determine which of the
     * permitted incoming locales is chosen by examining the partner's supported locales as
     * specified in the outgoing-locales field.
     * </p>
     */
    public IAmqpList<AmqpType<?, ?>> getIncomingLocales();

    /**
     * peer properties
     * <p>
     * The properties SHOULD contain at least these fields: "product", giving the name of the
     * client product, "version", giving the name of the client version, "platform", giving the
     * name of the operating system, "copyright", if appropriate, and "information", giving
     * other general information.
     * </p>
     */
    public void setPeerProperties(AmqpMap peerProperties);

    /**
     * peer properties
     * <p>
     * The properties SHOULD contain at least these fields: "product", giving the name of the
     * client product, "version", giving the name of the client version, "platform", giving the
     * name of the operating system, "copyright", if appropriate, and "information", giving
     * other general information.
     * </p>
     */
    public IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> getPeerProperties();

    /**
     * the extension capabilities the sender supports
     * <p>
     * If the receiver of the offered-capabilities requires an extension capability which is
     * not present in the offered-capability list then it MUST close the connection.
     * </p>
     */
    public void setOfferedCapabilities(AmqpList offeredCapabilities);

    /**
     * the extension capabilities the sender supports
     * <p>
     * If the receiver of the offered-capabilities requires an extension capability which is
     * not present in the offered-capability list then it MUST close the connection.
     * </p>
     */
    public IAmqpList<AmqpType<?, ?>> getOfferedCapabilities();

    /**
     * the extension capabilities the sender may use if the receiver supports them
     * <p>
     * The desired-capability list defines which extension capabilities the sender MAY use if
     * the receiver offers them (i.e. they are in the offered-capabilities list received by the
     * sender of the desired-capabilities). If the receiver of the desired-capabilities offers
     * extension capabilities which are not present in the desired-capability list it received,
     * then it can be sure those (undesired) capabilities will not be used on the
     * Connection.
     * </p>
     */
    public void setDesiredCapabilities(AmqpList desiredCapabilities);

    /**
     * the extension capabilities the sender may use if the receiver supports them
     * <p>
     * The desired-capability list defines which extension capabilities the sender MAY use if
     * the receiver offers them (i.e. they are in the offered-capabilities list received by the
     * sender of the desired-capabilities). If the receiver of the desired-capabilities offers
     * extension capabilities which are not present in the desired-capability list it received,
     * then it can be sure those (undesired) capabilities will not be used on the
     * Connection.
     * </p>
     */
    public IAmqpList<AmqpType<?, ?>> getDesiredCapabilities();

    public static class AmqpOpenBean implements AmqpOpen{

        private AmqpOpenBuffer buffer;
        private AmqpOpenBean bean = this;
        private AmqpOptions options;
        private AmqpString containerId;
        private AmqpString hostname;
        private AmqpUint maxFrameSize;
        private AmqpUshort channelMax;
        private AmqpUshort heartbeatInterval;
        private AmqpList outgoingLocales;
        private AmqpList incomingLocales;
        private AmqpMap peerProperties;
        private AmqpList offeredCapabilities;
        private AmqpList desiredCapabilities;

        AmqpOpenBean() {
        }

        AmqpOpenBean(IAmqpList<AmqpType<?, ?>> value) {

            for(int i = 0; i < value.getListCount(); i++) {
                set(i, value.get(i));
            }
        }

        AmqpOpenBean(AmqpOpen.AmqpOpenBean other) {
            this.bean = other;
        }

        public final AmqpOpenBean copy() {
            return new AmqpOpen.AmqpOpenBean(bean);
        }

        public final void handle(AmqpCommandHandler handler) throws Exception {
            handler.handleOpen(this);
        }

        public final AmqpOpen.AmqpOpenBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            if(buffer == null) {
                buffer = new AmqpOpenBuffer(marshaller.encode(this));
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

        public void setContainerId(String containerId) {
            setContainerId(TypeFactory.createAmqpString(containerId));
        }


        public final void setContainerId(AmqpString containerId) {
            copyCheck();
            bean.containerId = containerId;
        }

        public final String getContainerId() {
            return bean.containerId.getValue();
        }

        public void setHostname(String hostname) {
            setHostname(TypeFactory.createAmqpString(hostname));
        }


        public final void setHostname(AmqpString hostname) {
            copyCheck();
            bean.hostname = hostname;
        }

        public final String getHostname() {
            return bean.hostname.getValue();
        }

        public void setMaxFrameSize(Long maxFrameSize) {
            setMaxFrameSize(TypeFactory.createAmqpUint(maxFrameSize));
        }


        public void setMaxFrameSize(long maxFrameSize) {
            setMaxFrameSize(TypeFactory.createAmqpUint(maxFrameSize));
        }


        public final void setMaxFrameSize(AmqpUint maxFrameSize) {
            copyCheck();
            bean.maxFrameSize = maxFrameSize;
        }

        public final Long getMaxFrameSize() {
            return bean.maxFrameSize.getValue();
        }

        public void setChannelMax(Integer channelMax) {
            setChannelMax(TypeFactory.createAmqpUshort(channelMax));
        }


        public void setChannelMax(int channelMax) {
            setChannelMax(TypeFactory.createAmqpUshort(channelMax));
        }


        public final void setChannelMax(AmqpUshort channelMax) {
            copyCheck();
            bean.channelMax = channelMax;
        }

        public final Integer getChannelMax() {
            return bean.channelMax.getValue();
        }

        public void setHeartbeatInterval(Integer heartbeatInterval) {
            setHeartbeatInterval(TypeFactory.createAmqpUshort(heartbeatInterval));
        }


        public void setHeartbeatInterval(int heartbeatInterval) {
            setHeartbeatInterval(TypeFactory.createAmqpUshort(heartbeatInterval));
        }


        public final void setHeartbeatInterval(AmqpUshort heartbeatInterval) {
            copyCheck();
            bean.heartbeatInterval = heartbeatInterval;
        }

        public final Integer getHeartbeatInterval() {
            return bean.heartbeatInterval.getValue();
        }

        public final void setOutgoingLocales(AmqpList outgoingLocales) {
            copyCheck();
            bean.outgoingLocales = outgoingLocales;
        }

        public final IAmqpList<AmqpType<?, ?>> getOutgoingLocales() {
            return bean.outgoingLocales;
        }

        public final void setIncomingLocales(AmqpList incomingLocales) {
            copyCheck();
            bean.incomingLocales = incomingLocales;
        }

        public final IAmqpList<AmqpType<?, ?>> getIncomingLocales() {
            return bean.incomingLocales;
        }

        public final void setPeerProperties(AmqpMap peerProperties) {
            copyCheck();
            bean.peerProperties = peerProperties;
        }

        public final IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> getPeerProperties() {
            return bean.peerProperties;
        }

        public final void setOfferedCapabilities(AmqpList offeredCapabilities) {
            copyCheck();
            bean.offeredCapabilities = offeredCapabilities;
        }

        public final IAmqpList<AmqpType<?, ?>> getOfferedCapabilities() {
            return bean.offeredCapabilities;
        }

        public final void setDesiredCapabilities(AmqpList desiredCapabilities) {
            copyCheck();
            bean.desiredCapabilities = desiredCapabilities;
        }

        public final IAmqpList<AmqpType<?, ?>> getDesiredCapabilities() {
            return bean.desiredCapabilities;
        }

        public void set(int index, AmqpType<?, ?> value) {
            switch(index) {
            case 0: {
                setOptions((AmqpOptions) value);
                break;
            }
            case 1: {
                setContainerId((AmqpString) value);
                break;
            }
            case 2: {
                setHostname((AmqpString) value);
                break;
            }
            case 3: {
                setMaxFrameSize((AmqpUint) value);
                break;
            }
            case 4: {
                setChannelMax((AmqpUshort) value);
                break;
            }
            case 5: {
                setHeartbeatInterval((AmqpUshort) value);
                break;
            }
            case 6: {
                setOutgoingLocales((AmqpList) value);
                break;
            }
            case 7: {
                setIncomingLocales((AmqpList) value);
                break;
            }
            case 8: {
                setPeerProperties((AmqpMap) value);
                break;
            }
            case 9: {
                setOfferedCapabilities((AmqpList) value);
                break;
            }
            case 10: {
                setDesiredCapabilities((AmqpList) value);
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
                return bean.containerId;
            }
            case 2: {
                return bean.hostname;
            }
            case 3: {
                return bean.maxFrameSize;
            }
            case 4: {
                return bean.channelMax;
            }
            case 5: {
                return bean.heartbeatInterval;
            }
            case 6: {
                return bean.outgoingLocales;
            }
            case 7: {
                return bean.incomingLocales;
            }
            case 8: {
                return bean.peerProperties;
            }
            case 9: {
                return bean.offeredCapabilities;
            }
            case 10: {
                return bean.desiredCapabilities;
            }
            default : {
                throw new IndexOutOfBoundsException(String.valueOf(index));
            }
            }
        }

        public int getListCount() {
            return 11;
        }

        public IAmqpList<AmqpType<?, ?>> getValue() {
            return bean;
        }

        public Iterator<AmqpType<?, ?>> iterator() {
            return new AmqpListIterator<AmqpType<?, ?>>(bean);
        }


        private final void copyCheck() {
            if(buffer != null) {;
                throw new IllegalStateException("unwriteable");
            }
            if(bean != this) {;
                copy(bean);
            }
        }

        private final void copy(AmqpOpen.AmqpOpenBean other) {
            bean = this;
        }

        public boolean equals(Object o){
            if(this == o) {
                return true;
            }

            if(o == null || !(o instanceof AmqpOpen)) {
                return false;
            }

            return equals((AmqpOpen) o);
        }

        public boolean equals(AmqpOpen b) {

            if(b.getOptions() == null ^ getOptions() == null) {
                return false;
            }
            if(b.getOptions() != null && !b.getOptions().equals(getOptions())){ 
                return false;
            }

            if(b.getContainerId() == null ^ getContainerId() == null) {
                return false;
            }
            if(b.getContainerId() != null && !b.getContainerId().equals(getContainerId())){ 
                return false;
            }

            if(b.getHostname() == null ^ getHostname() == null) {
                return false;
            }
            if(b.getHostname() != null && !b.getHostname().equals(getHostname())){ 
                return false;
            }

            if(b.getMaxFrameSize() == null ^ getMaxFrameSize() == null) {
                return false;
            }
            if(b.getMaxFrameSize() != null && !b.getMaxFrameSize().equals(getMaxFrameSize())){ 
                return false;
            }

            if(b.getChannelMax() == null ^ getChannelMax() == null) {
                return false;
            }
            if(b.getChannelMax() != null && !b.getChannelMax().equals(getChannelMax())){ 
                return false;
            }

            if(b.getHeartbeatInterval() == null ^ getHeartbeatInterval() == null) {
                return false;
            }
            if(b.getHeartbeatInterval() != null && !b.getHeartbeatInterval().equals(getHeartbeatInterval())){ 
                return false;
            }

            if(b.getOutgoingLocales() == null ^ getOutgoingLocales() == null) {
                return false;
            }
            if(b.getOutgoingLocales() != null && !b.getOutgoingLocales().equals(getOutgoingLocales())){ 
                return false;
            }

            if(b.getIncomingLocales() == null ^ getIncomingLocales() == null) {
                return false;
            }
            if(b.getIncomingLocales() != null && !b.getIncomingLocales().equals(getIncomingLocales())){ 
                return false;
            }

            if(b.getPeerProperties() == null ^ getPeerProperties() == null) {
                return false;
            }
            if(b.getPeerProperties() != null && !b.getPeerProperties().equals(getPeerProperties())){ 
                return false;
            }

            if(b.getOfferedCapabilities() == null ^ getOfferedCapabilities() == null) {
                return false;
            }
            if(b.getOfferedCapabilities() != null && !b.getOfferedCapabilities().equals(getOfferedCapabilities())){ 
                return false;
            }

            if(b.getDesiredCapabilities() == null ^ getDesiredCapabilities() == null) {
                return false;
            }
            if(b.getDesiredCapabilities() != null && !b.getDesiredCapabilities().equals(getDesiredCapabilities())){ 
                return false;
            }
            return true;
        }

        public int hashCode() {
            return AbstractAmqpList.hashCodeFor(this);
        }
    }

    public static class AmqpOpenBuffer extends AmqpList.AmqpListBuffer implements AmqpOpen{

        private AmqpOpenBean bean;

        protected AmqpOpenBuffer(Encoded<IAmqpList<AmqpType<?, ?>>> encoded) {
            super(encoded);
        }

        public final void setOptions(AmqpOptions options) {
            bean().setOptions(options);
        }

        public final AmqpOptions getOptions() {
            return bean().getOptions();
        }

        public void setContainerId(String containerId) {
            bean().setContainerId(containerId);
        }

        public final void setContainerId(AmqpString containerId) {
            bean().setContainerId(containerId);
        }

        public final String getContainerId() {
            return bean().getContainerId();
        }

        public void setHostname(String hostname) {
            bean().setHostname(hostname);
        }

        public final void setHostname(AmqpString hostname) {
            bean().setHostname(hostname);
        }

        public final String getHostname() {
            return bean().getHostname();
        }

        public void setMaxFrameSize(Long maxFrameSize) {
            bean().setMaxFrameSize(maxFrameSize);
        }

        public void setMaxFrameSize(long maxFrameSize) {
            bean().setMaxFrameSize(maxFrameSize);
        }


        public final void setMaxFrameSize(AmqpUint maxFrameSize) {
            bean().setMaxFrameSize(maxFrameSize);
        }

        public final Long getMaxFrameSize() {
            return bean().getMaxFrameSize();
        }

        public void setChannelMax(Integer channelMax) {
            bean().setChannelMax(channelMax);
        }

        public void setChannelMax(int channelMax) {
            bean().setChannelMax(channelMax);
        }


        public final void setChannelMax(AmqpUshort channelMax) {
            bean().setChannelMax(channelMax);
        }

        public final Integer getChannelMax() {
            return bean().getChannelMax();
        }

        public void setHeartbeatInterval(Integer heartbeatInterval) {
            bean().setHeartbeatInterval(heartbeatInterval);
        }

        public void setHeartbeatInterval(int heartbeatInterval) {
            bean().setHeartbeatInterval(heartbeatInterval);
        }


        public final void setHeartbeatInterval(AmqpUshort heartbeatInterval) {
            bean().setHeartbeatInterval(heartbeatInterval);
        }

        public final Integer getHeartbeatInterval() {
            return bean().getHeartbeatInterval();
        }

        public final void setOutgoingLocales(AmqpList outgoingLocales) {
            bean().setOutgoingLocales(outgoingLocales);
        }

        public final IAmqpList<AmqpType<?, ?>> getOutgoingLocales() {
            return bean().getOutgoingLocales();
        }

        public final void setIncomingLocales(AmqpList incomingLocales) {
            bean().setIncomingLocales(incomingLocales);
        }

        public final IAmqpList<AmqpType<?, ?>> getIncomingLocales() {
            return bean().getIncomingLocales();
        }

        public final void setPeerProperties(AmqpMap peerProperties) {
            bean().setPeerProperties(peerProperties);
        }

        public final IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> getPeerProperties() {
            return bean().getPeerProperties();
        }

        public final void setOfferedCapabilities(AmqpList offeredCapabilities) {
            bean().setOfferedCapabilities(offeredCapabilities);
        }

        public final IAmqpList<AmqpType<?, ?>> getOfferedCapabilities() {
            return bean().getOfferedCapabilities();
        }

        public final void setDesiredCapabilities(AmqpList desiredCapabilities) {
            bean().setDesiredCapabilities(desiredCapabilities);
        }

        public final IAmqpList<AmqpType<?, ?>> getDesiredCapabilities() {
            return bean().getDesiredCapabilities();
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

        public AmqpOpen.AmqpOpenBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpOpen bean() {
            if(bean == null) {
                bean = new AmqpOpen.AmqpOpenBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public final void handle(AmqpCommandHandler handler) throws Exception {
            handler.handleOpen(this);
        }

        public boolean equals(Object o){
            return bean().equals(o);
        }

        public boolean equals(AmqpOpen o){
            return bean().equals(o);
        }

        public int hashCode() {
            return bean().hashCode();
        }

        public static AmqpOpen.AmqpOpenBuffer create(Encoded<IAmqpList<AmqpType<?, ?>>> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpOpen.AmqpOpenBuffer(encoded);
        }

        public static AmqpOpen.AmqpOpenBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpOpen(in));
        }

        public static AmqpOpen.AmqpOpenBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpOpen(buffer, offset));
        }
    }
}