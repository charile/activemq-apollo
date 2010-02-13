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
import org.apache.activemq.amqp.protocol.types.AmqpBinary;
import org.apache.activemq.amqp.protocol.types.AmqpProperties;
import org.apache.activemq.amqp.protocol.types.AmqpString;
import org.apache.activemq.amqp.protocol.types.AmqpSymbol;
import org.apache.activemq.amqp.protocol.types.AmqpType;
import org.apache.activemq.amqp.protocol.types.AmqpUlong;
import org.apache.activemq.amqp.protocol.types.IAmqpList;
import org.apache.activemq.util.buffer.Buffer;

public class AmqpPropertiesMarshaller implements DescribedTypeMarshaller<AmqpProperties>{

    static final AmqpPropertiesMarshaller SINGLETON = new AmqpPropertiesMarshaller();
    private static final Encoded<IAmqpList<AmqpType<?, ?>>> NULL_ENCODED = new Encoder.NullEncoded<IAmqpList<AmqpType<?, ?>>>();

    public static final String SYMBOLIC_ID = "amqp:properties:list";
    //Format code: 0x00000001:0x00009802:
    public static final long CATEGORY = 1;
    public static final long DESCRIPTOR_ID = 38914;
    public static final long NUMERIC_ID = CATEGORY << 32 | DESCRIPTOR_ID; //(4295006210L)
    //Hard coded descriptor:
    public static final EncodedBuffer DESCRIPTOR = FormatCategory.createBuffer(new Buffer(new byte [] {
        (byte) 0x80,                                         // ulong descriptor encoding)
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,  // CATEGORY CODE
        (byte) 0x00, (byte) 0x00, (byte) 0x98, (byte) 0x02   // DESCRIPTOR ID CODE
    }), 0);

    private static final ListDecoder<AmqpType<?, ?>> DECODER = new ListDecoder<AmqpType<?, ?>>() {
        public final IAmqpList<AmqpType<?, ?>> unmarshalType(int dataCount, int dataSize, DataInput in) throws AmqpEncodingError, IOException {
            if (dataCount > 7) {
                throw new AmqpEncodingError("Too many fields for " + SYMBOLIC_ID + ": " + dataCount);
            }
            IAmqpList<AmqpType<?, ?>> rc = new IAmqpList.ArrayBackedList<AmqpType<?, ?>>(new AmqpType<?, ?>[7]);
            //message-id:
            if(dataCount > 0) {
                rc.set(0, AmqpBinary.AmqpBinaryBuffer.create(AmqpBinaryMarshaller.createEncoded(in)));
                dataCount--;
            }

            //user-id:
            if(dataCount > 0) {
                rc.set(1, AmqpBinary.AmqpBinaryBuffer.create(AmqpBinaryMarshaller.createEncoded(in)));
                dataCount--;
            }

            //to:
            if(dataCount > 0) {
                rc.set(2, AmqpString.AmqpStringBuffer.create(AmqpStringMarshaller.createEncoded(in)));
                dataCount--;
            }

            //reply-to:
            if(dataCount > 0) {
                rc.set(3, AmqpString.AmqpStringBuffer.create(AmqpStringMarshaller.createEncoded(in)));
                dataCount--;
            }

            //correlation-id:
            if(dataCount > 0) {
                rc.set(4, AmqpBinary.AmqpBinaryBuffer.create(AmqpBinaryMarshaller.createEncoded(in)));
                dataCount--;
            }

            //content-length:
            if(dataCount > 0) {
                rc.set(5, AmqpUlong.AmqpUlongBuffer.create(AmqpUlongMarshaller.createEncoded(in)));
                dataCount--;
            }

            //content-type:
            if(dataCount > 0) {
                rc.set(6, AmqpSymbol.AmqpSymbolBuffer.create(AmqpSymbolMarshaller.createEncoded(in)));
                dataCount--;
            }
            return rc;
        }

        public IAmqpList<AmqpType<?, ?>> decode(EncodedBuffer[] constituents) {
            if (constituents.length > 7) {
                throw new AmqpEncodingError("Too many fields for " + SYMBOLIC_ID + ":" + constituents.length);
            }
            int dataCount = constituents.length;
            IAmqpList<AmqpType<?, ?>> rc = new IAmqpList.ArrayBackedList<AmqpType<?, ?>>(new AmqpType<?, ?>[7]);
            //message-id:
            if(dataCount > 0) {
                rc.set(0, AmqpBinary.AmqpBinaryBuffer.create(AmqpBinaryMarshaller.createEncoded(constituents[0])));
                dataCount--;
            }

            //user-id:
            if(dataCount > 0) {
                rc.set(1, AmqpBinary.AmqpBinaryBuffer.create(AmqpBinaryMarshaller.createEncoded(constituents[1])));
                dataCount--;
            }

            //to:
            if(dataCount > 0) {
                rc.set(2, AmqpString.AmqpStringBuffer.create(AmqpStringMarshaller.createEncoded(constituents[2])));
                dataCount--;
            }

            //reply-to:
            if(dataCount > 0) {
                rc.set(3, AmqpString.AmqpStringBuffer.create(AmqpStringMarshaller.createEncoded(constituents[3])));
                dataCount--;
            }

            //correlation-id:
            if(dataCount > 0) {
                rc.set(4, AmqpBinary.AmqpBinaryBuffer.create(AmqpBinaryMarshaller.createEncoded(constituents[4])));
                dataCount--;
            }

            //content-length:
            if(dataCount > 0) {
                rc.set(5, AmqpUlong.AmqpUlongBuffer.create(AmqpUlongMarshaller.createEncoded(constituents[5])));
                dataCount--;
            }

            //content-type:
            if(dataCount > 0) {
                rc.set(6, AmqpSymbol.AmqpSymbolBuffer.create(AmqpSymbolMarshaller.createEncoded(constituents[6])));
                dataCount--;
            }
            return rc;
        }
    };

    public static class AmqpPropertiesEncoded extends DescribedEncoded<IAmqpList<AmqpType<?, ?>>> {

        public AmqpPropertiesEncoded(DescribedBuffer buffer) {
            super(buffer);
        }

        public AmqpPropertiesEncoded(AmqpProperties value) {
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

    public static final Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpProperties value) throws AmqpEncodingError {
        return new AmqpPropertiesEncoded(value);
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
        return new AmqpPropertiesEncoded(db);
    }

    public final AmqpProperties.AmqpPropertiesBuffer decodeDescribedType(AmqpType<?, ?> descriptor, DescribedBuffer encoded) throws AmqpEncodingError {
        return AmqpProperties.AmqpPropertiesBuffer.create(new AmqpPropertiesEncoded(encoded));
    }
}
