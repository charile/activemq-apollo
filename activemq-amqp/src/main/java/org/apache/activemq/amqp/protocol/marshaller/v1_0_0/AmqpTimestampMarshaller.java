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
import java.util.Date;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.amqp.protocol.marshaller.v1_0_0.Encoder;
import org.apache.activemq.amqp.protocol.marshaller.v1_0_0.Encoder.*;
import org.apache.activemq.amqp.protocol.types.AmqpTimestamp;
import org.apache.activemq.util.buffer.Buffer;

public class AmqpTimestampMarshaller {

    private static final Encoder ENCODER = Encoder.SINGLETON;
    private static final Encoded<Date> NULL_ENCODED = new Encoder.NullEncoded<Date>();

    public static final byte FORMAT_CODE = (byte) 0x83;
    public static final FormatSubCategory FORMAT_CATEGORY  = FormatSubCategory.getCategory(FORMAT_CODE);

    public static class AmqpTimestampEncoded  extends AbstractEncoded<Date> {

        public AmqpTimestampEncoded (EncodedBuffer encoded) {
            super(encoded);
        }

        public AmqpTimestampEncoded (Date value) throws AmqpEncodingError {
            super(FORMAT_CODE, value);
        }

        public final void encode(Date value, Buffer encoded, int offset) throws AmqpEncodingError{
            ENCODER.encodeTimestamp(value, encoded, offset);
        }

        public final Date decode(EncodedBuffer encoded) throws AmqpEncodingError{
            return ENCODER.decodeTimestamp(encoded.getBuffer(), encoded.getDataOffset());
        }

        public final void marshalData(DataOutput out) throws IOException {
            ENCODER.writeTimestamp(value, out);
        }

        public final Date unmarshalData(DataInput in) throws IOException {
            return ENCODER.readTimestamp(in);
        }
    }

    public static final Encoded<Date> encode(AmqpTimestamp data) throws AmqpEncodingError {
        if(data == null) {
            return NULL_ENCODED;
        }
        return new AmqpTimestampEncoded(data.getValue());
    }

    static final Encoded<Date> createEncoded(Buffer source, int offset) throws AmqpEncodingError {
        return createEncoded(FormatCategory.createBuffer(source, offset));
    }

    static final Encoded<Date> createEncoded(DataInput in) throws IOException, AmqpEncodingError {
        return createEncoded(FormatCategory.createBuffer(in.readByte(), in));
    }

    static final Encoded<Date> createEncoded(EncodedBuffer buffer) throws AmqpEncodingError {
        if(buffer.getEncodingFormatCode() == AmqpNullMarshaller.FORMAT_CODE) {
            return new Encoder.NullEncoded<Date>();
        }
        if(buffer.getEncodingFormatCode() != FORMAT_CODE) {
            throw new AmqpEncodingError("Unexpected format for AmqpTimestamp expected: " + FORMAT_CODE);
        }
        return new AmqpTimestampEncoded(buffer);
    }
}
