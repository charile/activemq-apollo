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
import java.lang.String;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpVersion;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.amqp.protocol.marshaller.Encoding;
import org.apache.activemq.amqp.protocol.marshaller.UnexpectedTypeException;
import org.apache.activemq.amqp.protocol.marshaller.v1_0_0.Encoder;
import org.apache.activemq.amqp.protocol.marshaller.v1_0_0.Encoder.*;
import org.apache.activemq.amqp.protocol.types.AmqpString;
import org.apache.activemq.util.buffer.Buffer;

public class AmqpStringMarshaller {

    private static final Encoder ENCODER = Encoder.SINGLETON;
    private static final Encoded<String> NULL_ENCODED = new Encoder.NullEncoded<String>();

    public static final byte STR8_UTF8_FORMAT_CODE = (byte) 0xa1;
    public static final byte STR8_UTF16_FORMAT_CODE = (byte) 0xa2;
    public static final byte STR32_UTF8_FORMAT_CODE = (byte) 0xb1;
    public static final byte STR32_UTF16_FORMAT_CODE = (byte) 0xb2;

    public static enum STRING_ENCODING implements Encoding{
        STR8_UTF8 (STR8_UTF8_FORMAT_CODE), // up to 2^8 - 1 octets worth of UTF-8 unicode
        STR8_UTF16 (STR8_UTF16_FORMAT_CODE), // up to 2^8 - 1 octets worth of UTF-16 unicode
        STR32_UTF8 (STR32_UTF8_FORMAT_CODE), // up to 2^32 - 1 octets worth of UTF-8 unicode
        STR32_UTF16 (STR32_UTF16_FORMAT_CODE); // up to 2^32 - 1 octets worth of UTF-16 unicode

        public final byte FORMAT_CODE;
        public final FormatSubCategory CATEGORY;

        STRING_ENCODING(byte formatCode) {
            this.FORMAT_CODE = formatCode;
            this.CATEGORY = FormatSubCategory.getCategory(formatCode);
        }

        public final byte getEncodingFormatCode() {
            return FORMAT_CODE;
        }

        public final AmqpVersion getEncodingVersion() {
            return AmqpMarshaller.VERSION;
        }

        public static STRING_ENCODING getEncoding(byte formatCode) throws UnexpectedTypeException {
            switch(formatCode) {
            case STR8_UTF8_FORMAT_CODE: {
                return STR8_UTF8;
            }
            case STR8_UTF16_FORMAT_CODE: {
                return STR8_UTF16;
            }
            case STR32_UTF8_FORMAT_CODE: {
                return STR32_UTF8;
            }
            case STR32_UTF16_FORMAT_CODE: {
                return STR32_UTF16;
            }
            default: {
                throw new UnexpectedTypeException("Unexpected format code for String: " + formatCode);
            }
            }
        }

        static final AmqpStringEncoded createEncoded(EncodedBuffer buffer) throws AmqpEncodingError {
            switch(buffer.getEncodingFormatCode()) {
            case STR8_UTF8_FORMAT_CODE: {
                return new AmqpStringStr8Utf8Encoded(buffer);
            }
            case STR8_UTF16_FORMAT_CODE: {
                return new AmqpStringStr8Utf16Encoded(buffer);
            }
            case STR32_UTF8_FORMAT_CODE: {
                return new AmqpStringStr32Utf8Encoded(buffer);
            }
            case STR32_UTF16_FORMAT_CODE: {
                return new AmqpStringStr32Utf16Encoded(buffer);
            }
            default: {
                throw new UnexpectedTypeException("Unexpected format code for String: " + buffer.getEncodingFormatCode());
            }
            }
        }
        static final AmqpStringEncoded createEncoded(byte formatCode, String value) throws AmqpEncodingError {
            switch(formatCode) {
            case STR8_UTF8_FORMAT_CODE: {
                return new AmqpStringStr8Utf8Encoded(value);
            }
            case STR8_UTF16_FORMAT_CODE: {
                return new AmqpStringStr8Utf16Encoded(value);
            }
            case STR32_UTF8_FORMAT_CODE: {
                return new AmqpStringStr32Utf8Encoded(value);
            }
            case STR32_UTF16_FORMAT_CODE: {
                return new AmqpStringStr32Utf16Encoded(value);
            }
            default: {
                throw new UnexpectedTypeException("Unexpected format code for String: " + formatCode);
            }
            }
        }
    }
    public static abstract class AmqpStringEncoded extends AbstractEncoded <String> {
        public AmqpStringEncoded(EncodedBuffer encoded) {
            super(encoded);
        }

        public AmqpStringEncoded(byte formatCode, String value) throws AmqpEncodingError {
            super(formatCode, value);
        }
    }

    /**
     * up to 2^8 - 1 octets worth of UTF-8 unicode
     */
    private static class AmqpStringStr8Utf8Encoded extends AmqpStringEncoded {

        private final STRING_ENCODING encoding = STRING_ENCODING.STR8_UTF8;
        public AmqpStringStr8Utf8Encoded(EncodedBuffer encoded) {
            super(encoded);
        }

        public AmqpStringStr8Utf8Encoded(String value) throws AmqpEncodingError {
            super(STRING_ENCODING.STR8_UTF8.FORMAT_CODE, value);
        }

        protected final int computeDataSize() throws AmqpEncodingError {
            return ENCODER.getEncodedSizeOfString(value, encoding);
        }

        public final void encode(String value, Buffer encoded, int offset) throws AmqpEncodingError {
            ENCODER.encodeStringStr8Utf8(value, encoded, offset);
        }

        public final void marshalData(DataOutput out) throws IOException {
            ENCODER.writeStringStr8Utf8(value, out);
        }

        public final String decode(EncodedBuffer encoded) throws AmqpEncodingError {
            return ENCODER.decodeStringStr8Utf8(encoded.getBuffer(), encoded.getDataOffset(), encoded.getDataSize());
        }

        public final String unmarshalData(DataInput in) throws IOException {
            return ENCODER.readStringStr8Utf8(getDataSize(), in);
        }
    }

    /**
     * up to 2^8 - 1 octets worth of UTF-16 unicode
     */
    private static class AmqpStringStr8Utf16Encoded extends AmqpStringEncoded {

        private final STRING_ENCODING encoding = STRING_ENCODING.STR8_UTF16;
        public AmqpStringStr8Utf16Encoded(EncodedBuffer encoded) {
            super(encoded);
        }

        public AmqpStringStr8Utf16Encoded(String value) throws AmqpEncodingError {
            super(STRING_ENCODING.STR8_UTF16.FORMAT_CODE, value);
        }

        protected final int computeDataSize() throws AmqpEncodingError {
            return ENCODER.getEncodedSizeOfString(value, encoding);
        }

        public final void encode(String value, Buffer encoded, int offset) throws AmqpEncodingError {
            ENCODER.encodeStringStr8Utf16(value, encoded, offset);
        }

        public final void marshalData(DataOutput out) throws IOException {
            ENCODER.writeStringStr8Utf16(value, out);
        }

        public final String decode(EncodedBuffer encoded) throws AmqpEncodingError {
            return ENCODER.decodeStringStr8Utf16(encoded.getBuffer(), encoded.getDataOffset(), encoded.getDataSize());
        }

        public final String unmarshalData(DataInput in) throws IOException {
            return ENCODER.readStringStr8Utf16(getDataSize(), in);
        }
    }

    /**
     * up to 2^32 - 1 octets worth of UTF-8 unicode
     */
    private static class AmqpStringStr32Utf8Encoded extends AmqpStringEncoded {

        private final STRING_ENCODING encoding = STRING_ENCODING.STR32_UTF8;
        public AmqpStringStr32Utf8Encoded(EncodedBuffer encoded) {
            super(encoded);
        }

        public AmqpStringStr32Utf8Encoded(String value) throws AmqpEncodingError {
            super(STRING_ENCODING.STR32_UTF8.FORMAT_CODE, value);
        }

        protected final int computeDataSize() throws AmqpEncodingError {
            return ENCODER.getEncodedSizeOfString(value, encoding);
        }

        public final void encode(String value, Buffer encoded, int offset) throws AmqpEncodingError {
            ENCODER.encodeStringStr32Utf8(value, encoded, offset);
        }

        public final void marshalData(DataOutput out) throws IOException {
            ENCODER.writeStringStr32Utf8(value, out);
        }

        public final String decode(EncodedBuffer encoded) throws AmqpEncodingError {
            return ENCODER.decodeStringStr32Utf8(encoded.getBuffer(), encoded.getDataOffset(), encoded.getDataSize());
        }

        public final String unmarshalData(DataInput in) throws IOException {
            return ENCODER.readStringStr32Utf8(getDataSize(), in);
        }
    }

    /**
     * up to 2^32 - 1 octets worth of UTF-16 unicode
     */
    private static class AmqpStringStr32Utf16Encoded extends AmqpStringEncoded {

        private final STRING_ENCODING encoding = STRING_ENCODING.STR32_UTF16;
        public AmqpStringStr32Utf16Encoded(EncodedBuffer encoded) {
            super(encoded);
        }

        public AmqpStringStr32Utf16Encoded(String value) throws AmqpEncodingError {
            super(STRING_ENCODING.STR32_UTF16.FORMAT_CODE, value);
        }

        protected final int computeDataSize() throws AmqpEncodingError {
            return ENCODER.getEncodedSizeOfString(value, encoding);
        }

        public final void encode(String value, Buffer encoded, int offset) throws AmqpEncodingError {
            ENCODER.encodeStringStr32Utf16(value, encoded, offset);
        }

        public final void marshalData(DataOutput out) throws IOException {
            ENCODER.writeStringStr32Utf16(value, out);
        }

        public final String decode(EncodedBuffer encoded) throws AmqpEncodingError {
            return ENCODER.decodeStringStr32Utf16(encoded.getBuffer(), encoded.getDataOffset(), encoded.getDataSize());
        }

        public final String unmarshalData(DataInput in) throws IOException {
            return ENCODER.readStringStr32Utf16(getDataSize(), in);
        }
    }


    private static final STRING_ENCODING chooseEncoding(AmqpString val) throws AmqpEncodingError {
        return Encoder.chooseStringEncoding(val.getValue());
    }

    private static final STRING_ENCODING chooseEncoding(String val) throws AmqpEncodingError {
        return Encoder.chooseStringEncoding(val);
    }

    static final Encoded<String> encode(AmqpString data) throws AmqpEncodingError {
        if(data == null) {
            return NULL_ENCODED;
        }
        return STRING_ENCODING.createEncoded(chooseEncoding(data).FORMAT_CODE, data.getValue());
    }

    static final Encoded<String> createEncoded(Buffer source, int offset) throws AmqpEncodingError {
        return createEncoded(FormatCategory.createBuffer(source, offset));
    }

    static final Encoded<String> createEncoded(String val) throws AmqpEncodingError {
        return STRING_ENCODING.createEncoded(chooseEncoding(val).FORMAT_CODE, val);
    }

    static final Encoded<String> createEncoded(DataInput in) throws IOException, AmqpEncodingError {
        return createEncoded(FormatCategory.createBuffer(in.readByte(), in));
    }

    static final Encoded<String> createEncoded(EncodedBuffer buffer) throws AmqpEncodingError {
        if(buffer.getEncodingFormatCode() == AmqpNullMarshaller.FORMAT_CODE) {
            return NULL_ENCODED;
        }
        return STRING_ENCODING.createEncoded(buffer);
    }
}
