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
import java.lang.Integer;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.amqp.protocol.marshaller.v1_0_0.Encoder;
import org.apache.activemq.amqp.protocol.marshaller.v1_0_0.Encoder.*;
import org.apache.activemq.amqp.protocol.types.AmqpInt;
import org.apache.activemq.util.buffer.Buffer;

public class AmqpIntMarshaller {

    private static final Encoder ENCODER = Encoder.SINGLETON;
    private static final Encoded<Integer> NULL_ENCODED = new Encoder.NullEncoded<Integer>();

    public static final byte FORMAT_CODE = (byte) 0x71;
    public static final FormatSubCategory FORMAT_CATEGORY  = FormatSubCategory.getCategory(FORMAT_CODE);

    public static class AmqpIntEncoded  extends AbstractEncoded<Integer> {

        public AmqpIntEncoded (EncodedBuffer encoded) {
            super(encoded);
        }

        public AmqpIntEncoded (Integer value) throws AmqpEncodingError {
            super(FORMAT_CODE, value);
        }

        public final void encode(Integer value, Buffer encoded, int offset) throws AmqpEncodingError{
            ENCODER.encodeInt(value, encoded, offset);
        }

        public final Integer decode(EncodedBuffer encoded) throws AmqpEncodingError{
            return ENCODER.decodeInt(encoded.getBuffer(), encoded.getDataOffset());
        }

        public final void marshalData(DataOutput out) throws IOException {
            ENCODER.writeInt(value, out);
        }

        public final Integer unmarshalData(DataInput in) throws IOException {
            return ENCODER.readInt(in);
        }
    }

    public static final Encoded<Integer> encode(AmqpInt data) throws AmqpEncodingError {
        if(data == null) {
            return NULL_ENCODED;
        }
        return new AmqpIntEncoded(data.getValue());
    }

    static final Encoded<Integer> createEncoded(Buffer source, int offset) throws AmqpEncodingError {
        return createEncoded(FormatCategory.createBuffer(source, offset));
    }

    static final Encoded<Integer> createEncoded(DataInput in) throws IOException, AmqpEncodingError {
        return createEncoded(FormatCategory.createBuffer(in.readByte(), in));
    }

    static final Encoded<Integer> createEncoded(EncodedBuffer buffer) throws AmqpEncodingError {
        if(buffer.getEncodingFormatCode() == AmqpNullMarshaller.FORMAT_CODE) {
            return new Encoder.NullEncoded<Integer>();
        }
        if(buffer.getEncodingFormatCode() != FORMAT_CODE) {
            throw new AmqpEncodingError("Unexpected format for AmqpInt expected: " + FORMAT_CODE);
        }
        return new AmqpIntEncoded(buffer);
    }
}
