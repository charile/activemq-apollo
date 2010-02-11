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
import java.util.HashMap;
import java.util.Iterator;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpMarshaller;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.amqp.protocol.types.IAmqpList;
import org.apache.activemq.util.buffer.Buffer;

/**
 * Represents a security mechanism response
 * <p>
 * Send the SASL response data as defined by the SASL specification.
 * </p>
 */
public interface AmqpSaslResponse extends AmqpList {



    /**
     * options map
     */
    public void setOptions(HashMap<AmqpType<?,?>, AmqpType<?,?>> options);

    /**
     * options map
     */
    public void setOptions(AmqpMap options);

    /**
     * options map
     */
    public HashMap<AmqpType<?,?>, AmqpType<?,?>> getOptions();

    /**
     * security response data
     * <p>
     * A block of opaque data passed to the security mechanism. The contents of this data are
     * defined by the SASL security mechanism.
     * </p>
     */
    public void setResponse(Buffer response);

    /**
     * security response data
     * <p>
     * A block of opaque data passed to the security mechanism. The contents of this data are
     * defined by the SASL security mechanism.
     * </p>
     */
    public void setResponse(AmqpBinary response);

    /**
     * security response data
     * <p>
     * A block of opaque data passed to the security mechanism. The contents of this data are
     * defined by the SASL security mechanism.
     * </p>
     */
    public Buffer getResponse();

    public static class AmqpSaslResponseBean implements AmqpSaslResponse{

        private AmqpSaslResponseBuffer buffer;
        private AmqpSaslResponseBean bean = this;
        private AmqpMap options;
        private AmqpBinary response;

        public AmqpSaslResponseBean() {
        }

        public AmqpSaslResponseBean(IAmqpList value) {
            //TODO we should defer decoding of the described type:
            for(int i = 0; i < value.getListCount(); i++) {
                set(i, value.get(i));
            }
        }

        public AmqpSaslResponseBean(AmqpSaslResponse.AmqpSaslResponseBean other) {
            this.bean = other;
        }

        public final AmqpSaslResponseBean copy() {
            return new AmqpSaslResponse.AmqpSaslResponseBean(bean);
        }

        public final AmqpSaslResponse.AmqpSaslResponseBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            if(buffer == null) {
                buffer = new AmqpSaslResponseBuffer(marshaller.encode(this));
            }
            return buffer;
        }

        public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
            getBuffer(marshaller).marshal(out, marshaller);
        }


        public void setOptions(HashMap<AmqpType<?,?>, AmqpType<?,?>> options) {
            setOptions(new AmqpMap.AmqpMapBean(options));
        }


        public final void setOptions(AmqpMap options) {
            copyCheck();
            bean.options = options;
        }

        public final HashMap<AmqpType<?,?>, AmqpType<?,?>> getOptions() {
            return bean.options.getValue();
        }

        public void setResponse(Buffer response) {
            setResponse(new AmqpBinary.AmqpBinaryBean(response));
        }


        public final void setResponse(AmqpBinary response) {
            copyCheck();
            bean.response = response;
        }

        public final Buffer getResponse() {
            return bean.response.getValue();
        }

        public void set(int index, AmqpType<?, ?> value) {
            switch(index) {
            case 0: {
                setOptions((AmqpMap) value);
                break;
            }
            case 1: {
                setResponse((AmqpBinary) value);
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
                return bean.response;
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

        private final void copy(AmqpSaslResponse.AmqpSaslResponseBean other) {
            this.options= other.options;
            this.response= other.response;
            bean = this;
        }

        public boolean equivalent(AmqpType<?,?> t){
            if(this == t) {
                return true;
            }

            if(t == null || !(t instanceof AmqpSaslResponse)) {
                return false;
            }

            return equivalent((AmqpSaslResponse) t);
        }

        public boolean equivalent(AmqpSaslResponse b) {

            if(b.getOptions() == null ^ getOptions() == null) {
                return false;
            }
            if(b.getOptions() != null && !b.getOptions().equals(getOptions())){ 
                return false;
            }

            if(b.getResponse() == null ^ getResponse() == null) {
                return false;
            }
            if(b.getResponse() != null && !b.getResponse().equals(getResponse())){ 
                return false;
            }
            return true;
        }
    }

    public static class AmqpSaslResponseBuffer extends AmqpList.AmqpListBuffer implements AmqpSaslResponse{

        private AmqpSaslResponseBean bean;

        protected AmqpSaslResponseBuffer(Encoded<IAmqpList> encoded) {
            super(encoded);
        }

    public void setOptions(HashMap<AmqpType<?,?>, AmqpType<?,?>> options) {
            bean().setOptions(options);
        }

        public final void setOptions(AmqpMap options) {
            bean().setOptions(options);
        }

        public final HashMap<AmqpType<?,?>, AmqpType<?,?>> getOptions() {
            return bean().getOptions();
        }

    public void setResponse(Buffer response) {
            bean().setResponse(response);
        }

        public final void setResponse(AmqpBinary response) {
            bean().setResponse(response);
        }

        public final Buffer getResponse() {
            return bean().getResponse();
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

        public AmqpSaslResponse.AmqpSaslResponseBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpSaslResponse bean() {
            if(bean == null) {
                bean = new AmqpSaslResponse.AmqpSaslResponseBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public boolean equivalent(AmqpType<?, ?> t) {
            return bean().equivalent(t);
        }

        public static AmqpSaslResponse.AmqpSaslResponseBuffer create(Encoded<IAmqpList> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpSaslResponse.AmqpSaslResponseBuffer(encoded);
        }

        public static AmqpSaslResponse.AmqpSaslResponseBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpSaslResponse(in));
        }

        public static AmqpSaslResponse.AmqpSaslResponseBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpSaslResponse(buffer, offset));
        }
    }
}