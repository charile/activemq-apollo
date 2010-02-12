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
import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpVersion;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.amqp.protocol.marshaller.Encoding;
import org.apache.activemq.amqp.protocol.marshaller.UnexpectedTypeException;
import org.apache.activemq.amqp.protocol.marshaller.v1_0_0.Encoder;
import org.apache.activemq.amqp.protocol.marshaller.v1_0_0.Encoder.*;
import org.apache.activemq.amqp.protocol.types.AmqpBinary;
import org.apache.activemq.util.buffer.Buffer;

public class AmqpBinaryMarshaller {

    private static final Encoder ENCODER = Encoder.SINGLETON;
    private static final Encoded<Buffer> NULL_ENCODED = new Encoder.NullEncoded<Buffer>();

    public static final byte VBIN8_FORMAT_CODE = (byte) 0xa0;
    public static final byte VBIN32_FORMAT_CODE = (byte) 0xb0;

    public static enum BINARY_ENCODING implements Encoding{
        VBIN8 (VBIN8_FORMAT_CODE), // up to 2^8 - 1 octets of binary data
        VBIN32 (VBIN32_FORMAT_CODE); // up to 2^32 - 1 octets of binary data

        public final byte FORMAT_CODE;
        public final FormatSubCategory CATEGORY;

        BINARY_ENCODING(byte formatCode) {
            this.FORMAT_CODE = formatCode;
            this.CATEGORY = FormatSubCategory.getCategory(formatCode);
        }

        public final byte getEncodingFormatCode() {
            return FORMAT_CODE;
        }

        public final AmqpVersion getEncodingVersion() {
            return AmqpMarshaller.VERSION;
        }

        public static BINARY_ENCODING getEncoding(byte formatCode) throws UnexpectedTypeException {
            switch(formatCode) {
            case VBIN8_FORMAT_CODE: {
                return VBIN8;
            }
            case VBIN32_FORMAT_CODE: {
                return VBIN32;
            }
            default: {
                throw new UnexpectedTypeException("Unexpected format code for Binary: " + formatCode);
            }
            }
        }

        static final AmqpBinaryEncoded createEncoded(EncodedBuffer buffer) throws AmqpEncodingError {
            switch(buffer.getEncodingFormatCode()) {
            case VBIN8_FORMAT_CODE: {
                return new AmqpBinaryVbin8Encoded(buffer);
            }
            case VBIN32_FORMAT_CODE: {
                return new AmqpBinaryVbin32Encoded(buffer);
            }
            default: {
                throw new UnexpectedTypeException("Unexpected format code for Binary: " + buffer.getEncodingFormatCode());
            }
            }
        }
        static final AmqpBinaryEncoded createEncoded(byte formatCode, Buffer value) throws AmqpEncodingError {
            switch(formatCode) {
            case VBIN8_FORMAT_CODE: {
                return new AmqpBinaryVbin8Encoded(value);
            }
            case VBIN32_FORMAT_CODE: {
                return new AmqpBinaryVbin32Encoded(value);
            }
            default: {
                throw new UnexpectedTypeException("Unexpected format code for Binary: " + formatCode);
            }
            }
        }
    }
    public static abstract class AmqpBinaryEncoded extends AbstractEncoded <Buffer> {
        public AmqpBinaryEncoded(EncodedBuffer encoded) {
            super(encoded);
        }

        public AmqpBinaryEncoded(byte formatCode, Buffer value) throws AmqpEncodingError {
            super(formatCode, value);
        }
    }

    /**
     * up to 2^8 - 1 octets of binary data
     */
    private static class AmqpBinaryVbin8Encoded extends AmqpBinaryEncoded {

        private final BINARY_ENCODING encoding = BINARY_ENCODING.VBIN8;
        public AmqpBinaryVbin8Encoded(EncodedBuffer encoded) {
            super(encoded);
        }

        public AmqpBinaryVbin8Encoded(Buffer value) throws AmqpEncodingError {
            super(BINARY_ENCODING.VBIN8.FORMAT_CODE, value);
        }

        protected final int computeDataSize() throws AmqpEncodingError {
            return ENCODER.getEncodedSizeOfBinary(value, encoding);
        }

        public final void encode(Buffer value, Buffer encoded, int offset) throws AmqpEncodingError {
            ENCODER.encodeBinaryVbin8(value, encoded, offset);
        }

        public final Buffer decode(EncodedBuffer encoded) throws AmqpEncodingError {
            return ENCODER.decodeBinaryVbin8(encoded.getBuffer(), encoded.getDataOffset(), encoded.getDataSize());
        }

        public final void marshalData(DataOutput out) throws IOException {
            ENCODER.writeBinaryVbin8(value, out);
        }

        public final Buffer unmarshalData(DataInput in) throws IOException {
            return ENCODER.readBinaryVbin8(getDataSize(), in);
        }
    }

    /**
     * up to 2^32 - 1 octets of binary data
     */
    private static class AmqpBinaryVbin32Encoded extends AmqpBinaryEncoded {

        private final BINARY_ENCODING encoding = BINARY_ENCODING.VBIN32;
        public AmqpBinaryVbin32Encoded(EncodedBuffer encoded) {
            super(encoded);
        }

        public AmqpBinaryVbin32Encoded(Buffer value) throws AmqpEncodingError {
            super(BINARY_ENCODING.VBIN32.FORMAT_CODE, value);
        }

        protected final int computeDataSize() throws AmqpEncodingError {
            return ENCODER.getEncodedSizeOfBinary(value, encoding);
        }

        public final void encode(Buffer value, Buffer encoded, int offset) throws AmqpEncodingError {
            ENCODER.encodeBinaryVbin32(value, encoded, offset);
        }

        public final Buffer decode(EncodedBuffer encoded) throws AmqpEncodingError {
            return ENCODER.decodeBinaryVbin32(encoded.getBuffer(), encoded.getDataOffset(), encoded.getDataSize());
        }

        public final void marshalData(DataOutput out) throws IOException {
            ENCODER.writeBinaryVbin32(value, out);
        }

        public final Buffer unmarshalData(DataInput in) throws IOException {
            return ENCODER.readBinaryVbin32(getDataSize(), in);
        }
    }


    private static final BINARY_ENCODING chooseEncoding(AmqpBinary val) throws AmqpEncodingError {
        return Encoder.chooseBinaryEncoding(val.getValue());
    }

    private static final BINARY_ENCODING chooseEncoding(Buffer val) throws AmqpEncodingError {
        return Encoder.chooseBinaryEncoding(val);
    }

    static final Encoded<Buffer> encode(AmqpBinary data) throws AmqpEncodingError {
        if(data == null) {
            return NULL_ENCODED;
        }
        return BINARY_ENCODING.createEncoded(chooseEncoding(data).FORMAT_CODE, data.getValue());
    }

    static final Encoded<Buffer> createEncoded(Buffer source, int offset) throws AmqpEncodingError {
        return createEncoded(FormatCategory.createBuffer(source, offset));
    }

    static final Encoded<Buffer> createEncoded(Buffer val) throws AmqpEncodingError {
        return BINARY_ENCODING.createEncoded(chooseEncoding(val).FORMAT_CODE, val);
    }

    static final Encoded<Buffer> createEncoded(DataInput in) throws IOException, AmqpEncodingError {
        return createEncoded(FormatCategory.createBuffer(in.readByte(), in));
    }

    static final Encoded<Buffer> createEncoded(EncodedBuffer buffer) throws AmqpEncodingError {
        if(buffer.getEncodingFormatCode() == AmqpNullMarshaller.FORMAT_CODE) {
            return NULL_ENCODED;
        }
        return BINARY_ENCODING.createEncoded(buffer);
    }
}