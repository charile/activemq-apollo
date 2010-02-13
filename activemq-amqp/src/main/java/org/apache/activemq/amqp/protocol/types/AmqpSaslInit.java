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
import java.util.Iterator;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpMarshaller;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.amqp.protocol.types.IAmqpList;
import org.apache.activemq.util.buffer.Buffer;

/**
 * Represents a initiate sasl exchange
 * <p>
 * Selects the sasl mechanism and provides the initial response if needed.
 * </p>
 */
public interface AmqpSaslInit extends AmqpList {



    /**
     * options map
     */
    public void setOptions(AmqpMap options);

    /**
     * options map
     */
    public IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> getOptions();

    /**
     * selected security mechanism
     * <p>
     * The name of the SASL mechanism used for the SASL exchange. If the selected mechanism is
     * not supported by the receiving peer, it MUST close the Connection with the
     * authentication-failure close-code. Each peer MUST authenticate using the highest-level
     * security profile it can handle from the list provided by the partner.
     * </p>
     */
    public void setMechanism(String mechanism);

    /**
     * selected security mechanism
     * <p>
     * The name of the SASL mechanism used for the SASL exchange. If the selected mechanism is
     * not supported by the receiving peer, it MUST close the Connection with the
     * authentication-failure close-code. Each peer MUST authenticate using the highest-level
     * security profile it can handle from the list provided by the partner.
     * </p>
     */
    public void setMechanism(AmqpString mechanism);

    /**
     * selected security mechanism
     * <p>
     * The name of the SASL mechanism used for the SASL exchange. If the selected mechanism is
     * not supported by the receiving peer, it MUST close the Connection with the
     * authentication-failure close-code. Each peer MUST authenticate using the highest-level
     * security profile it can handle from the list provided by the partner.
     * </p>
     */
    public String getMechanism();

    /**
     * security response data
     * <p>
     * A block of opaque data passed to the security mechanism. The contents of this data are
     * defined by the SASL security mechanism.
     * </p>
     */
    public void setInitialResponse(Buffer initialResponse);

    /**
     * security response data
     * <p>
     * A block of opaque data passed to the security mechanism. The contents of this data are
     * defined by the SASL security mechanism.
     * </p>
     */
    public void setInitialResponse(AmqpBinary initialResponse);

    /**
     * security response data
     * <p>
     * A block of opaque data passed to the security mechanism. The contents of this data are
     * defined by the SASL security mechanism.
     * </p>
     */
    public Buffer getInitialResponse();

    public static class AmqpSaslInitBean implements AmqpSaslInit{

        private AmqpSaslInitBuffer buffer;
        private AmqpSaslInitBean bean = this;
        private AmqpMap options;
        private AmqpString mechanism;
        private AmqpBinary initialResponse;

        AmqpSaslInitBean() {
        }

        AmqpSaslInitBean(IAmqpList<AmqpType<?, ?>> value) {

            for(int i = 0; i < value.getListCount(); i++) {
                set(i, value.get(i));
            }
        }

        AmqpSaslInitBean(AmqpSaslInit.AmqpSaslInitBean other) {
            this.bean = other;
        }

        public final AmqpSaslInitBean copy() {
            return new AmqpSaslInit.AmqpSaslInitBean(bean);
        }

        public final AmqpSaslInit.AmqpSaslInitBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            if(buffer == null) {
                buffer = new AmqpSaslInitBuffer(marshaller.encode(this));
            }
            return buffer;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            getBuffer(marshaller).marshal(out, marshaller);
        }


        public final void setOptions(AmqpMap options) {
            copyCheck();
            bean.options = options;
        }

        public final IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> getOptions() {
            return bean.options;
        }

        public void setMechanism(String mechanism) {
            setMechanism(TypeFactory.createAmqpString(mechanism));
        }


        public final void setMechanism(AmqpString mechanism) {
            copyCheck();
            bean.mechanism = mechanism;
        }

        public final String getMechanism() {
            return bean.mechanism.getValue();
        }

        public void setInitialResponse(Buffer initialResponse) {
            setInitialResponse(TypeFactory.createAmqpBinary(initialResponse));
        }


        public final void setInitialResponse(AmqpBinary initialResponse) {
            copyCheck();
            bean.initialResponse = initialResponse;
        }

        public final Buffer getInitialResponse() {
            return bean.initialResponse.getValue();
        }

        public void set(int index, AmqpType<?, ?> value) {
            switch(index) {
            case 0: {
                setOptions((AmqpMap) value);
                break;
            }
            case 1: {
                setMechanism((AmqpString) value);
                break;
            }
            case 2: {
                setInitialResponse((AmqpBinary) value);
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
                return bean.mechanism;
            }
            case 2: {
                return bean.initialResponse;
            }
            default : {
                throw new IndexOutOfBoundsException(String.valueOf(index));
            }
            }
        }

        public int getListCount() {
            return 3;
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

        private final void copy(AmqpSaslInit.AmqpSaslInitBean other) {
            bean = this;
        }

        public boolean equals(Object o){
            if(this == o) {
                return true;
            }

            if(o == null || !(o instanceof AmqpSaslInit)) {
                return false;
            }

            return equals((AmqpSaslInit) o);
        }

        public boolean equals(AmqpSaslInit b) {

            if(b.getOptions() == null ^ getOptions() == null) {
                return false;
            }
            if(b.getOptions() != null && !b.getOptions().equals(getOptions())){ 
                return false;
            }

            if(b.getMechanism() == null ^ getMechanism() == null) {
                return false;
            }
            if(b.getMechanism() != null && !b.getMechanism().equals(getMechanism())){ 
                return false;
            }

            if(b.getInitialResponse() == null ^ getInitialResponse() == null) {
                return false;
            }
            if(b.getInitialResponse() != null && !b.getInitialResponse().equals(getInitialResponse())){ 
                return false;
            }
            return true;
        }

        public int hashCode() {
            return AbstractAmqpList.hashCodeFor(this);
        }
    }

    public static class AmqpSaslInitBuffer extends AmqpList.AmqpListBuffer implements AmqpSaslInit{

        private AmqpSaslInitBean bean;

        protected AmqpSaslInitBuffer(Encoded<IAmqpList<AmqpType<?, ?>>> encoded) {
            super(encoded);
        }

        public final void setOptions(AmqpMap options) {
            bean().setOptions(options);
        }

        public final IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> getOptions() {
            return bean().getOptions();
        }

        public void setMechanism(String mechanism) {
            bean().setMechanism(mechanism);
        }

        public final void setMechanism(AmqpString mechanism) {
            bean().setMechanism(mechanism);
        }

        public final String getMechanism() {
            return bean().getMechanism();
        }

        public void setInitialResponse(Buffer initialResponse) {
            bean().setInitialResponse(initialResponse);
        }

        public final void setInitialResponse(AmqpBinary initialResponse) {
            bean().setInitialResponse(initialResponse);
        }

        public final Buffer getInitialResponse() {
            return bean().getInitialResponse();
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

        public AmqpSaslInit.AmqpSaslInitBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpSaslInit bean() {
            if(bean == null) {
                bean = new AmqpSaslInit.AmqpSaslInitBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public boolean equals(Object o){
            return bean().equals(o);
        }

        public boolean equals(AmqpSaslInit o){
            return bean().equals(o);
        }

        public int hashCode() {
            return bean().hashCode();
        }

        public static AmqpSaslInit.AmqpSaslInitBuffer create(Encoded<IAmqpList<AmqpType<?, ?>>> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpSaslInit.AmqpSaslInitBuffer(encoded);
        }

        public static AmqpSaslInit.AmqpSaslInitBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpSaslInit(in));
        }

        public static AmqpSaslInit.AmqpSaslInitBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpSaslInit(buffer, offset));
        }
    }
}