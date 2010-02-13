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
import org.apache.activemq.amqp.protocol.types.AmqpList;
import org.apache.activemq.amqp.protocol.types.AmqpType;
import org.apache.activemq.amqp.protocol.types.IAmqpList;
import org.apache.activemq.util.buffer.Buffer;

public class AmqpListMarshaller {

    private static final Encoder ENCODER = Encoder.SINGLETON;
    private static final Encoded<IAmqpList<AmqpType<?, ?>>> NULL_ENCODED = new Encoder.NullEncoded<IAmqpList<AmqpType<?, ?>>>();

    public static final byte LIST8_FORMAT_CODE = (byte) 0xc0;
    public static final byte LIST32_FORMAT_CODE = (byte) 0xd0;
    public static final byte ARRAY8_FORMAT_CODE = (byte) 0xe0;
    public static final byte ARRAY32_FORMAT_CODE = (byte) 0xf0;

    public static enum LIST_ENCODING implements Encoding{
        LIST8 (LIST8_FORMAT_CODE), // up to 2^8 - 1 list elements with total size less than 2^8 octets
        LIST32 (LIST32_FORMAT_CODE), // up to 2^32 - 1 list elements with total size less than 2^32 octets
        ARRAY8 (ARRAY8_FORMAT_CODE), // up to 2^8 - 1 array elements with total size less than 2^8 octets
        ARRAY32 (ARRAY32_FORMAT_CODE); // up to 2^32 - 1 array elements with total size less than 2^32 octets

        public final byte FORMAT_CODE;
        public final FormatSubCategory CATEGORY;

        LIST_ENCODING(byte formatCode) {
            this.FORMAT_CODE = formatCode;
            this.CATEGORY = FormatSubCategory.getCategory(formatCode);
        }

        public final byte getEncodingFormatCode() {
            return FORMAT_CODE;
        }

        public final AmqpVersion getEncodingVersion() {
            return AmqpMarshaller.VERSION;
        }

        public static LIST_ENCODING getEncoding(byte formatCode) throws UnexpectedTypeException {
            switch(formatCode) {
            case LIST8_FORMAT_CODE: {
                return LIST8;
            }
            case LIST32_FORMAT_CODE: {
                return LIST32;
            }
            case ARRAY8_FORMAT_CODE: {
                return ARRAY8;
            }
            case ARRAY32_FORMAT_CODE: {
                return ARRAY32;
            }
            default: {
                throw new UnexpectedTypeException("Unexpected format code for List: " + formatCode);
            }
            }
        }

        static final AmqpListEncoded createEncoded(EncodedBuffer buffer) throws AmqpEncodingError {
            switch(buffer.getEncodingFormatCode()) {
            case LIST8_FORMAT_CODE: {
                return new AmqpListList8Encoded(buffer);
            }
            case LIST32_FORMAT_CODE: {
                return new AmqpListList32Encoded(buffer);
            }
            case ARRAY8_FORMAT_CODE: {
                return new AmqpListArray8Encoded(buffer);
            }
            case ARRAY32_FORMAT_CODE: {
                return new AmqpListArray32Encoded(buffer);
            }
            default: {
                throw new UnexpectedTypeException("Unexpected format code for List: " + buffer.getEncodingFormatCode());
            }
            }
        }
        static final AmqpListEncoded createEncoded(byte formatCode, IAmqpList<AmqpType<?, ?>> value) throws AmqpEncodingError {
            switch(formatCode) {
            case LIST8_FORMAT_CODE: {
                return new AmqpListList8Encoded(value);
            }
            case LIST32_FORMAT_CODE: {
                return new AmqpListList32Encoded(value);
            }
            case ARRAY8_FORMAT_CODE: {
                return new AmqpListArray8Encoded(value);
            }
            case ARRAY32_FORMAT_CODE: {
                return new AmqpListArray32Encoded(value);
            }
            default: {
                throw new UnexpectedTypeException("Unexpected format code for List: " + formatCode);
            }
            }
        }
    }
    public static abstract class AmqpListEncoded extends AbstractEncoded <IAmqpList<AmqpType<?, ?>>> {
        ListDecoder decoder = Encoder.DEFAULT_LIST_DECODER;

        public AmqpListEncoded(EncodedBuffer encoded) {
            super(encoded);
        }

        public AmqpListEncoded(byte formatCode, IAmqpList<AmqpType<?, ?>> value) throws AmqpEncodingError {
            super(formatCode, value);
        }

        final void setDecoder(ListDecoder decoder) {
            this.decoder = decoder;
        }
    }

    /**
     * up to 2^8 - 1 list elements with total size less than 2^8 octets
     */
    private static class AmqpListList8Encoded extends AmqpListEncoded {

        private final LIST_ENCODING encoding = LIST_ENCODING.LIST8;
        public AmqpListList8Encoded(EncodedBuffer encoded) {
            super(encoded);
        }

        public AmqpListList8Encoded(IAmqpList<AmqpType<?, ?>> value) throws AmqpEncodingError {
            super(LIST_ENCODING.LIST8.FORMAT_CODE, value);
        }

        protected final int computeDataSize() throws AmqpEncodingError {
            return ENCODER.getEncodedSizeOfList(value, encoding);
        }

        protected final int computeDataCount() throws AmqpEncodingError {
            return ENCODER.getEncodedCountOfList(value, encoding);
        }

        public final void encode(IAmqpList<AmqpType<?, ?>> value, Buffer encoded, int offset) throws AmqpEncodingError {
            ENCODER.encodeListList8(value, encoded, offset);
        }

        public final void marshalData(DataOutput out) throws IOException {
            ENCODER.writeListList8(value, out);
        }

        public final IAmqpList<AmqpType<?, ?>> decode(EncodedBuffer encoded) throws AmqpEncodingError {
            return decoder.decode(encoded.asCompound().constituents());
        }

        public final IAmqpList<AmqpType<?, ?>> unmarshalData(DataInput in) throws IOException {
            return decoder.unmarshalType(getDataCount(), getDataSize(), in);
        }
    }

    /**
     * up to 2^32 - 1 list elements with total size less than 2^32 octets
     */
    private static class AmqpListList32Encoded extends AmqpListEncoded {

        private final LIST_ENCODING encoding = LIST_ENCODING.LIST32;
        public AmqpListList32Encoded(EncodedBuffer encoded) {
            super(encoded);
        }

        public AmqpListList32Encoded(IAmqpList<AmqpType<?, ?>> value) throws AmqpEncodingError {
            super(LIST_ENCODING.LIST32.FORMAT_CODE, value);
        }

        protected final int computeDataSize() throws AmqpEncodingError {
            return ENCODER.getEncodedSizeOfList(value, encoding);
        }

        protected final int computeDataCount() throws AmqpEncodingError {
            return ENCODER.getEncodedCountOfList(value, encoding);
        }

        public final void encode(IAmqpList<AmqpType<?, ?>> value, Buffer encoded, int offset) throws AmqpEncodingError {
            ENCODER.encodeListList32(value, encoded, offset);
        }

        public final void marshalData(DataOutput out) throws IOException {
            ENCODER.writeListList32(value, out);
        }

        public final IAmqpList<AmqpType<?, ?>> decode(EncodedBuffer encoded) throws AmqpEncodingError {
            return decoder.decode(encoded.asCompound().constituents());
        }

        public final IAmqpList<AmqpType<?, ?>> unmarshalData(DataInput in) throws IOException {
            return decoder.unmarshalType(getDataCount(), getDataSize(), in);
        }
    }

    /**
     * up to 2^8 - 1 array elements with total size less than 2^8 octets
     */
    private static class AmqpListArray8Encoded extends AmqpListEncoded {

        private final LIST_ENCODING encoding = LIST_ENCODING.ARRAY8;
        public AmqpListArray8Encoded(EncodedBuffer encoded) {
            super(encoded);
        }

        public AmqpListArray8Encoded(IAmqpList<AmqpType<?, ?>> value) throws AmqpEncodingError {
            super(LIST_ENCODING.ARRAY8.FORMAT_CODE, value);
        }

        protected final int computeDataSize() throws AmqpEncodingError {
            return ENCODER.getEncodedSizeOfList(value, encoding);
        }

        protected final int computeDataCount() throws AmqpEncodingError {
            return ENCODER.getEncodedCountOfList(value, encoding);
        }

        public final void encode(IAmqpList<AmqpType<?, ?>> value, Buffer encoded, int offset) throws AmqpEncodingError {
            ENCODER.encodeListArray8(value, encoded, offset);
        }

        public final void marshalData(DataOutput out) throws IOException {
            ENCODER.writeListArray8(value, out);
        }

        public final IAmqpList<AmqpType<?, ?>> decode(EncodedBuffer encoded) throws AmqpEncodingError {
            return decoder.decode(encoded.asCompound().constituents());
        }

        public final IAmqpList<AmqpType<?, ?>> unmarshalData(DataInput in) throws IOException {
            return decoder.unmarshalType(getDataCount(), getDataSize(), in);
        }
    }

    /**
     * up to 2^32 - 1 array elements with total size less than 2^32 octets
     */
    private static class AmqpListArray32Encoded extends AmqpListEncoded {

        private final LIST_ENCODING encoding = LIST_ENCODING.ARRAY32;
        public AmqpListArray32Encoded(EncodedBuffer encoded) {
            super(encoded);
        }

        public AmqpListArray32Encoded(IAmqpList<AmqpType<?, ?>> value) throws AmqpEncodingError {
            super(LIST_ENCODING.ARRAY32.FORMAT_CODE, value);
        }

        protected final int computeDataSize() throws AmqpEncodingError {
            return ENCODER.getEncodedSizeOfList(value, encoding);
        }

        protected final int computeDataCount() throws AmqpEncodingError {
            return ENCODER.getEncodedCountOfList(value, encoding);
        }

        public final void encode(IAmqpList<AmqpType<?, ?>> value, Buffer encoded, int offset) throws AmqpEncodingError {
            ENCODER.encodeListArray32(value, encoded, offset);
        }

        public final void marshalData(DataOutput out) throws IOException {
            ENCODER.writeListArray32(value, out);
        }

        public final IAmqpList<AmqpType<?, ?>> decode(EncodedBuffer encoded) throws AmqpEncodingError {
            return decoder.decode(encoded.asCompound().constituents());
        }

        public final IAmqpList<AmqpType<?, ?>> unmarshalData(DataInput in) throws IOException {
            return decoder.unmarshalType(getDataCount(), getDataSize(), in);
        }
    }


    private static final LIST_ENCODING chooseEncoding(AmqpList val) throws AmqpEncodingError {
        return Encoder.chooseListEncoding(val);
    }

    private static final LIST_ENCODING chooseEncoding(IAmqpList<AmqpType<?, ?>> val) throws AmqpEncodingError {
        return Encoder.chooseListEncoding(val);
    }

    static final Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpList data) throws AmqpEncodingError {
        if(data == null) {
            return NULL_ENCODED;
        }
        return LIST_ENCODING.createEncoded(chooseEncoding(data).FORMAT_CODE, data);
    }

    static final Encoded<IAmqpList<AmqpType<?, ?>>> createEncoded(Buffer source, int offset) throws AmqpEncodingError {
        return createEncoded(FormatCategory.createBuffer(source, offset));
    }

    static final Encoded<IAmqpList<AmqpType<?, ?>>> createEncoded(IAmqpList<AmqpType<?, ?>> val) throws AmqpEncodingError {
        return LIST_ENCODING.createEncoded(chooseEncoding(val).FORMAT_CODE, val);
    }

    static final Encoded<IAmqpList<AmqpType<?, ?>>> createEncoded(DataInput in) throws IOException, AmqpEncodingError {
        return createEncoded(FormatCategory.createBuffer(in.readByte(), in));
    }

    static final Encoded<IAmqpList<AmqpType<?, ?>>> createEncoded(EncodedBuffer buffer) throws AmqpEncodingError {
        if(buffer.getEncodingFormatCode() == AmqpNullMarshaller.FORMAT_CODE) {
            return NULL_ENCODED;
        }
        return LIST_ENCODING.createEncoded(buffer);
    }

    static final Encoded<IAmqpList<AmqpType<?, ?>>> createEncoded(DataInput in, ListDecoder decoder) throws IOException, AmqpEncodingError {
        return createEncoded(FormatCategory.createBuffer(in.readByte(), in), decoder);
    }

    static final Encoded<IAmqpList<AmqpType<?, ?>>> createEncoded(EncodedBuffer buffer, ListDecoder decoder) throws AmqpEncodingError {
        if(buffer.getEncodingFormatCode() == AmqpNullMarshaller.FORMAT_CODE) {
            return NULL_ENCODED;
        }
        AmqpListEncoded rc = LIST_ENCODING.createEncoded(buffer);
        rc.setDecoder(decoder);
        return rc;
    }
}
