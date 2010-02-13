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
import org.apache.activemq.amqp.protocol.types.AmqpDrain;
import org.apache.activemq.amqp.protocol.types.AmqpHandle;
import org.apache.activemq.amqp.protocol.types.AmqpOptions;
import org.apache.activemq.amqp.protocol.types.AmqpSymbol;
import org.apache.activemq.amqp.protocol.types.AmqpType;
import org.apache.activemq.amqp.protocol.types.AmqpUlong;
import org.apache.activemq.amqp.protocol.types.IAmqpList;
import org.apache.activemq.util.buffer.Buffer;

public class AmqpDrainMarshaller implements DescribedTypeMarshaller<AmqpDrain>{

    static final AmqpDrainMarshaller SINGLETON = new AmqpDrainMarshaller();
    private static final Encoded<IAmqpList<AmqpType<?, ?>>> NULL_ENCODED = new Encoder.NullEncoded<IAmqpList<AmqpType<?, ?>>>();

    public static final String SYMBOLIC_ID = "amqp:drain:list";
    //Format code: 0x00000001:0x00000308:
    public static final long CATEGORY = 1;
    public static final long DESCRIPTOR_ID = 776;
    public static final long NUMERIC_ID = CATEGORY << 32 | DESCRIPTOR_ID; //(4294968072L)
    //Hard coded descriptor:
    public static final EncodedBuffer DESCRIPTOR = FormatCategory.createBuffer(new Buffer(new byte [] {
        (byte) 0x80,                                         // ulong descriptor encoding)
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,  // CATEGORY CODE
        (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x08   // DESCRIPTOR ID CODE
    }), 0);

    private static final ListDecoder<AmqpType<?, ?>> DECODER = new ListDecoder<AmqpType<?, ?>>() {
        public final IAmqpList<AmqpType<?, ?>> unmarshalType(int dataCount, int dataSize, DataInput in) throws AmqpEncodingError, IOException {
            if (dataCount > 2) {
                throw new AmqpEncodingError("Too many fields for " + SYMBOLIC_ID + ": " + dataCount);
            }
            IAmqpList<AmqpType<?, ?>> rc = new IAmqpList.ArrayBackedList<AmqpType<?, ?>>(new AmqpType<?, ?>[2]);
            //options:
            if(dataCount > 0) {
                rc.set(0, AmqpOptions.AmqpOptionsBuffer.create(AmqpMapMarshaller.createEncoded(in)));
                dataCount--;
            }

            //handle:
            if(dataCount > 0) {
                rc.set(1, AmqpHandle.AmqpHandleBuffer.create(AmqpUintMarshaller.createEncoded(in)));
                dataCount--;
            }
            else {
                throw new AmqpEncodingError("Missing required field for " + SYMBOLIC_ID + ": handle");
            }
            return rc;
        }

        public IAmqpList<AmqpType<?, ?>> decode(EncodedBuffer[] constituents) {
            if (constituents.length > 2) {
                throw new AmqpEncodingError("Too many fields for " + SYMBOLIC_ID + ":" + constituents.length);
            }
            int dataCount = constituents.length;
            IAmqpList<AmqpType<?, ?>> rc = new IAmqpList.ArrayBackedList<AmqpType<?, ?>>(new AmqpType<?, ?>[2]);
            //options:
            if(dataCount > 0) {
                rc.set(0, AmqpOptions.AmqpOptionsBuffer.create(AmqpMapMarshaller.createEncoded(constituents[0])));
                dataCount--;
            }

            //handle:
            if(dataCount > 0) {
                rc.set(1, AmqpHandle.AmqpHandleBuffer.create(AmqpUintMarshaller.createEncoded(constituents[1])));
                dataCount--;
            }
            else {
                throw new AmqpEncodingError("Missing required field for " + SYMBOLIC_ID + ": handle");
            }
            return rc;
        }
    };

    public static class AmqpDrainEncoded extends DescribedEncoded<IAmqpList<AmqpType<?, ?>>> {

        public AmqpDrainEncoded(DescribedBuffer buffer) {
            super(buffer);
        }

        public AmqpDrainEncoded(AmqpDrain value) {
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

    public static final Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpDrain value) throws AmqpEncodingError {
        return new AmqpDrainEncoded(value);
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
        return new AmqpDrainEncoded(db);
    }

    public final AmqpDrain.AmqpDrainBuffer decodeDescribedType(AmqpType<?, ?> descriptor, DescribedBuffer encoded) throws AmqpEncodingError {
        return AmqpDrain.AmqpDrainBuffer.create(new AmqpDrainEncoded(encoded));
    }
}
