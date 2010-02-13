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
package org.apache.activemq.amqp.protocol.marshaller.v1_0_0;

import java.io.DataInput;
import java.io.IOException;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.amqp.protocol.marshaller.UnexpectedTypeException;
import org.apache.activemq.amqp.protocol.marshaller.v1_0_0.Encoder;
import org.apache.activemq.amqp.protocol.marshaller.v1_0_0.Encoder.*;
import org.apache.activemq.amqp.protocol.types.AmqpList;
import org.apache.activemq.amqp.protocol.types.AmqpMap;
import org.apache.activemq.amqp.protocol.types.AmqpOpen;
import org.apache.activemq.amqp.protocol.types.AmqpOptions;
import org.apache.activemq.amqp.protocol.types.AmqpString;
import org.apache.activemq.amqp.protocol.types.AmqpSymbol;
import org.apache.activemq.amqp.protocol.types.AmqpType;
import org.apache.activemq.amqp.protocol.types.AmqpUint;
import org.apache.activemq.amqp.protocol.types.AmqpUlong;
import org.apache.activemq.amqp.protocol.types.AmqpUshort;
import org.apache.activemq.amqp.protocol.types.IAmqpList;
import org.apache.activemq.util.buffer.Buffer;

public class AmqpOpenMarshaller implements DescribedTypeMarshaller<AmqpOpen>{

    static final AmqpOpenMarshaller SINGLETON = new AmqpOpenMarshaller();
    private static final Encoded<IAmqpList<AmqpType<?, ?>>> NULL_ENCODED = new Encoder.NullEncoded<IAmqpList<AmqpType<?, ?>>>();

    public static final String SYMBOLIC_ID = "amqp:open:list";
    //Format code: 0x00000001:0x00000201:
    public static final long CATEGORY = 1;
    public static final long DESCRIPTOR_ID = 513;
    public static final long NUMERIC_ID = CATEGORY << 32 | DESCRIPTOR_ID; //(4294967809L)
    //Hard coded descriptor:
    public static final EncodedBuffer DESCRIPTOR = FormatCategory.createBuffer(new Buffer(new byte [] {
        (byte) 0x80,                                         // ulong descriptor encoding)
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,  // CATEGORY CODE
        (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x01   // DESCRIPTOR ID CODE
    }), 0);

    private static final ListDecoder<AmqpType<?, ?>> DECODER = new ListDecoder<AmqpType<?, ?>>() {
        public final IAmqpList<AmqpType<?, ?>> unmarshalType(int dataCount, int dataSize, DataInput in) throws AmqpEncodingError, IOException {
            if (dataCount > 11) {
                throw new AmqpEncodingError("Too many fields for " + SYMBOLIC_ID + ": " + dataCount);
            }
            IAmqpList<AmqpType<?, ?>> rc = new IAmqpList.ArrayBackedList<AmqpType<?, ?>>(new AmqpType<?, ?>[11]);
            //options:
            if(dataCount > 0) {
                rc.set(0, AmqpOptions.AmqpOptionsBuffer.create(AmqpMapMarshaller.createEncoded(in)));
                dataCount--;
            }

            //container-id:
            if(dataCount > 0) {
                rc.set(1, AmqpString.AmqpStringBuffer.create(AmqpStringMarshaller.createEncoded(in)));
                dataCount--;
            }
            else {
                throw new AmqpEncodingError("Missing required field for " + SYMBOLIC_ID + ": container-id");
            }

            //hostname:
            if(dataCount > 0) {
                rc.set(2, AmqpString.AmqpStringBuffer.create(AmqpStringMarshaller.createEncoded(in)));
                dataCount--;
            }

            //max-frame-size:
            if(dataCount > 0) {
                rc.set(3, AmqpUint.AmqpUintBuffer.create(AmqpUintMarshaller.createEncoded(in)));
                dataCount--;
            }

            //channel-max:
            if(dataCount > 0) {
                rc.set(4, AmqpUshort.AmqpUshortBuffer.create(AmqpUshortMarshaller.createEncoded(in)));
                dataCount--;
            }
            else {
                throw new AmqpEncodingError("Missing required field for " + SYMBOLIC_ID + ": channel-max");
            }

            //heartbeat-interval:
            if(dataCount > 0) {
                rc.set(5, AmqpUshort.AmqpUshortBuffer.create(AmqpUshortMarshaller.createEncoded(in)));
                dataCount--;
            }

            //outgoing-locales:
            if(dataCount > 0) {
                rc.set(6, AmqpList.AmqpListBuffer.create(AmqpListMarshaller.createEncoded(in)));
                dataCount--;
            }

            //incoming-locales:
            if(dataCount > 0) {
                rc.set(7, AmqpList.AmqpListBuffer.create(AmqpListMarshaller.createEncoded(in)));
                dataCount--;
            }

            //peer-properties:
            if(dataCount > 0) {
                rc.set(8, AmqpMap.AmqpMapBuffer.create(AmqpMapMarshaller.createEncoded(in)));
                dataCount--;
            }

            //offered-capabilities:
            if(dataCount > 0) {
                rc.set(9, AmqpList.AmqpListBuffer.create(AmqpListMarshaller.createEncoded(in)));
                dataCount--;
            }

            //desired-capabilities:
            if(dataCount > 0) {
                rc.set(10, AmqpList.AmqpListBuffer.create(AmqpListMarshaller.createEncoded(in)));
                dataCount--;
            }
            return rc;
        }

        public IAmqpList<AmqpType<?, ?>> decode(EncodedBuffer[] constituents) {
            if (constituents.length > 11) {
                throw new AmqpEncodingError("Too many fields for " + SYMBOLIC_ID + ":" + constituents.length);
            }
            int dataCount = constituents.length;
            IAmqpList<AmqpType<?, ?>> rc = new IAmqpList.ArrayBackedList<AmqpType<?, ?>>(new AmqpType<?, ?>[11]);
            //options:
            if(dataCount > 0) {
                rc.set(0, AmqpOptions.AmqpOptionsBuffer.create(AmqpMapMarshaller.createEncoded(constituents[0])));
                dataCount--;
            }

            //container-id:
            if(dataCount > 0) {
                rc.set(1, AmqpString.AmqpStringBuffer.create(AmqpStringMarshaller.createEncoded(constituents[1])));
                dataCount--;
            }
            else {
                throw new AmqpEncodingError("Missing required field for " + SYMBOLIC_ID + ": container-id");
            }

            //hostname:
            if(dataCount > 0) {
                rc.set(2, AmqpString.AmqpStringBuffer.create(AmqpStringMarshaller.createEncoded(constituents[2])));
                dataCount--;
            }

            //max-frame-size:
            if(dataCount > 0) {
                rc.set(3, AmqpUint.AmqpUintBuffer.create(AmqpUintMarshaller.createEncoded(constituents[3])));
                dataCount--;
            }

            //channel-max:
            if(dataCount > 0) {
                rc.set(4, AmqpUshort.AmqpUshortBuffer.create(AmqpUshortMarshaller.createEncoded(constituents[4])));
                dataCount--;
            }
            else {
                throw new AmqpEncodingError("Missing required field for " + SYMBOLIC_ID + ": channel-max");
            }

            //heartbeat-interval:
            if(dataCount > 0) {
                rc.set(5, AmqpUshort.AmqpUshortBuffer.create(AmqpUshortMarshaller.createEncoded(constituents[5])));
                dataCount--;
            }

            //outgoing-locales:
            if(dataCount > 0) {
                rc.set(6, AmqpList.AmqpListBuffer.create(AmqpListMarshaller.createEncoded(constituents[6])));
                dataCount--;
            }

            //incoming-locales:
            if(dataCount > 0) {
                rc.set(7, AmqpList.AmqpListBuffer.create(AmqpListMarshaller.createEncoded(constituents[7])));
                dataCount--;
            }

            //peer-properties:
            if(dataCount > 0) {
                rc.set(8, AmqpMap.AmqpMapBuffer.create(AmqpMapMarshaller.createEncoded(constituents[8])));
                dataCount--;
            }

            //offered-capabilities:
            if(dataCount > 0) {
                rc.set(9, AmqpList.AmqpListBuffer.create(AmqpListMarshaller.createEncoded(constituents[9])));
                dataCount--;
            }

            //desired-capabilities:
            if(dataCount > 0) {
                rc.set(10, AmqpList.AmqpListBuffer.create(AmqpListMarshaller.createEncoded(constituents[10])));
                dataCount--;
            }
            return rc;
        }
    };

    public static class AmqpOpenEncoded extends DescribedEncoded<IAmqpList<AmqpType<?, ?>>> {

        public AmqpOpenEncoded(DescribedBuffer buffer) {
            super(buffer);
        }

        public AmqpOpenEncoded(AmqpOpen value) {
            super(AmqpListMarshaller.encode(value));
        }

        protected final String getSymbolicId() {
            return SYMBOLIC_ID;
        }

        protected final long getNumericId() {
            return NUMERIC_ID;
        }

        protected final Encoded<IAmqpList<AmqpType<?, ?>>> decodeDescribed(EncodedBuffer encoded) throws AmqpEncodingError {
            return AmqpListMarshaller.createEncoded(encoded, DECODER);
        }

        protected final Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalDescribed(DataInput in) throws IOException {
            return AmqpListMarshaller.createEncoded(in, DECODER);
        }

        protected final EncodedBuffer getDescriptor() {
            return DESCRIPTOR;
        }
    }

    public static final Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpOpen value) throws AmqpEncodingError {
        return new AmqpOpenEncoded(value);
    }

    static final Encoded<IAmqpList<AmqpType<?, ?>>> createEncoded(Buffer source, int offset) throws AmqpEncodingError {
        return createEncoded(FormatCategory.createBuffer(source, offset));
    }

    static final Encoded<IAmqpList<AmqpType<?, ?>>> createEncoded(DataInput in) throws IOException, AmqpEncodingError {
        return createEncoded(FormatCategory.createBuffer(in.readByte(), in));
    }

    static final Encoded<IAmqpList<AmqpType<?, ?>>> createEncoded(EncodedBuffer buffer) throws AmqpEncodingError {
        byte fc = buffer.getEncodingFormatCode();
        if (fc == Encoder.NULL_FORMAT_CODE) {
            return NULL_ENCODED;
        }

        DescribedBuffer db = buffer.asDescribed();
        AmqpType<?, ?> descriptor = AmqpMarshaller.SINGLETON.decodeType(db.getDescriptorBuffer());
        if(!(descriptor instanceof AmqpUlong && ((AmqpUlong)descriptor).getValue().longValue() == NUMERIC_ID ||
               descriptor instanceof AmqpSymbol && ((AmqpSymbol)descriptor).getValue().equals(SYMBOLIC_ID))) {
            throw new UnexpectedTypeException("descriptor mismatch: " + descriptor);
        }
        return new AmqpOpenEncoded(db);
    }

    public final AmqpOpen.AmqpOpenBuffer decodeDescribedType(AmqpType<?, ?> descriptor, DescribedBuffer encoded) throws AmqpEncodingError {
        return AmqpOpen.AmqpOpenBuffer.create(new AmqpOpenEncoded(encoded));
    }
}
