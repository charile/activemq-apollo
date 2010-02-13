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
package org.apache.activemq.amqp.protocol.marshaller;

import java.io.DataInput;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;
import org.apache.activemq.amqp.protocol.types.AmqpAttach;
import org.apache.activemq.amqp.protocol.types.AmqpBar;
import org.apache.activemq.amqp.protocol.types.AmqpBinary;
import org.apache.activemq.amqp.protocol.types.AmqpBoolean;
import org.apache.activemq.amqp.protocol.types.AmqpByte;
import org.apache.activemq.amqp.protocol.types.AmqpChar;
import org.apache.activemq.amqp.protocol.types.AmqpClose;
import org.apache.activemq.amqp.protocol.types.AmqpCompleted;
import org.apache.activemq.amqp.protocol.types.AmqpConnectionError;
import org.apache.activemq.amqp.protocol.types.AmqpDetach;
import org.apache.activemq.amqp.protocol.types.AmqpDisposition;
import org.apache.activemq.amqp.protocol.types.AmqpDouble;
import org.apache.activemq.amqp.protocol.types.AmqpDrain;
import org.apache.activemq.amqp.protocol.types.AmqpEnlist;
import org.apache.activemq.amqp.protocol.types.AmqpFilter;
import org.apache.activemq.amqp.protocol.types.AmqpFloat;
import org.apache.activemq.amqp.protocol.types.AmqpFlow;
import org.apache.activemq.amqp.protocol.types.AmqpFooter;
import org.apache.activemq.amqp.protocol.types.AmqpFragment;
import org.apache.activemq.amqp.protocol.types.AmqpHeader;
import org.apache.activemq.amqp.protocol.types.AmqpInt;
import org.apache.activemq.amqp.protocol.types.AmqpLink;
import org.apache.activemq.amqp.protocol.types.AmqpLinkError;
import org.apache.activemq.amqp.protocol.types.AmqpList;
import org.apache.activemq.amqp.protocol.types.AmqpLong;
import org.apache.activemq.amqp.protocol.types.AmqpMap;
import org.apache.activemq.amqp.protocol.types.AmqpNoop;
import org.apache.activemq.amqp.protocol.types.AmqpNull;
import org.apache.activemq.amqp.protocol.types.AmqpOpen;
import org.apache.activemq.amqp.protocol.types.AmqpProperties;
import org.apache.activemq.amqp.protocol.types.AmqpRejected;
import org.apache.activemq.amqp.protocol.types.AmqpReleased;
import org.apache.activemq.amqp.protocol.types.AmqpRelink;
import org.apache.activemq.amqp.protocol.types.AmqpSaslChallenge;
import org.apache.activemq.amqp.protocol.types.AmqpSaslInit;
import org.apache.activemq.amqp.protocol.types.AmqpSaslMechanisms;
import org.apache.activemq.amqp.protocol.types.AmqpSaslOutcome;
import org.apache.activemq.amqp.protocol.types.AmqpSaslResponse;
import org.apache.activemq.amqp.protocol.types.AmqpSessionError;
import org.apache.activemq.amqp.protocol.types.AmqpShort;
import org.apache.activemq.amqp.protocol.types.AmqpSource;
import org.apache.activemq.amqp.protocol.types.AmqpString;
import org.apache.activemq.amqp.protocol.types.AmqpSymbol;
import org.apache.activemq.amqp.protocol.types.AmqpTarget;
import org.apache.activemq.amqp.protocol.types.AmqpTimestamp;
import org.apache.activemq.amqp.protocol.types.AmqpTransfer;
import org.apache.activemq.amqp.protocol.types.AmqpTxn;
import org.apache.activemq.amqp.protocol.types.AmqpType;
import org.apache.activemq.amqp.protocol.types.AmqpUbyte;
import org.apache.activemq.amqp.protocol.types.AmqpUint;
import org.apache.activemq.amqp.protocol.types.AmqpUlong;
import org.apache.activemq.amqp.protocol.types.AmqpUnlink;
import org.apache.activemq.amqp.protocol.types.AmqpUshort;
import org.apache.activemq.amqp.protocol.types.AmqpUuid;
import org.apache.activemq.amqp.protocol.types.AmqpXid;
import org.apache.activemq.amqp.protocol.types.IAmqpList;
import org.apache.activemq.amqp.protocol.types.IAmqpMap;
import org.apache.activemq.util.buffer.Buffer;

public interface AmqpMarshaller {

    /**
     * @return the protocol version of the marshaller
     */
    public AmqpVersion getVersion();

    public AmqpType<?, ?> decodeType(Buffer source) throws AmqpEncodingError;

    public AmqpType<?, ?> unmarshalType(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpSessionError data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpSessionError(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpSessionError(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<Integer> encode(AmqpChar data) throws AmqpEncodingError;

    public Encoded<Integer> decodeAmqpChar(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<Integer> unmarshalAmqpChar(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpUnlink data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpUnlink(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpUnlink(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpFlow data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpFlow(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpFlow(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<Long> encode(AmqpLong data) throws AmqpEncodingError;

    public Encoded<Long> decodeAmqpLong(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<Long> unmarshalAmqpLong(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<Float> encode(AmqpFloat data) throws AmqpEncodingError;

    public Encoded<Float> decodeAmqpFloat(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<Float> unmarshalAmqpFloat(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpRelink data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpRelink(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpRelink(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpClose data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpClose(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpClose(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpOpen data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpOpen(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpOpen(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<Byte> encode(AmqpByte data) throws AmqpEncodingError;

    public Encoded<Byte> decodeAmqpByte(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<Byte> unmarshalAmqpByte(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> encode(AmqpRejected data) throws AmqpEncodingError;

    public Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> decodeAmqpRejected(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> unmarshalAmqpRejected(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpSaslOutcome data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpSaslOutcome(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpSaslOutcome(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<Boolean> encode(AmqpBoolean data) throws AmqpEncodingError;

    public Encoded<Boolean> decodeAmqpBoolean(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<Boolean> unmarshalAmqpBoolean(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<BigInteger> encode(AmqpUlong data) throws AmqpEncodingError;

    public Encoded<BigInteger> decodeAmqpUlong(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<BigInteger> unmarshalAmqpUlong(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpTransfer data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpTransfer(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpTransfer(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpDetach data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpDetach(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpDetach(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<String> encode(AmqpSymbol data) throws AmqpEncodingError;

    public Encoded<String> decodeAmqpSymbol(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<String> unmarshalAmqpSymbol(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpSaslInit data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpSaslInit(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpSaslInit(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<Integer> encode(AmqpInt data) throws AmqpEncodingError;

    public Encoded<Integer> decodeAmqpInt(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<Integer> unmarshalAmqpInt(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpEnlist data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpEnlist(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpEnlist(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpProperties data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpProperties(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpProperties(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<Double> encode(AmqpDouble data) throws AmqpEncodingError;

    public Encoded<Double> decodeAmqpDouble(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<Double> unmarshalAmqpDouble(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpDrain data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpDrain(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpDrain(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpHeader data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpHeader(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpHeader(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpSaslMechanisms data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpSaslMechanisms(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpSaslMechanisms(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<UUID> encode(AmqpUuid data) throws AmqpEncodingError;

    public Encoded<UUID> decodeAmqpUuid(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<UUID> unmarshalAmqpUuid(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpBar data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpBar(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpBar(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpFilter data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpFilter(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpFilter(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<Buffer> encode(AmqpBinary data) throws AmqpEncodingError;

    public Encoded<Buffer> decodeAmqpBinary(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<Buffer> unmarshalAmqpBinary(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpNoop data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpNoop(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpNoop(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpLink data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpLink(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpLink(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<Date> encode(AmqpTimestamp data) throws AmqpEncodingError;

    public Encoded<Date> decodeAmqpTimestamp(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<Date> unmarshalAmqpTimestamp(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<Short> encode(AmqpShort data) throws AmqpEncodingError;

    public Encoded<Short> decodeAmqpShort(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<Short> unmarshalAmqpShort(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> encode(AmqpReleased data) throws AmqpEncodingError;

    public Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> decodeAmqpReleased(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> unmarshalAmqpReleased(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpTxn data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpTxn(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpTxn(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> encode(AmqpMap data) throws AmqpEncodingError;

    public Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> decodeAmqpMap(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> unmarshalAmqpMap(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpLinkError data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpLinkError(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpLinkError(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpConnectionError data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpConnectionError(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpConnectionError(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpSaslResponse data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpSaslResponse(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpSaslResponse(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpFragment data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpFragment(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpFragment(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpDisposition data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpDisposition(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpDisposition(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<Object> encode(AmqpNull data) throws AmqpEncodingError;

    public Encoded<Object> decodeAmqpNull(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<Object> unmarshalAmqpNull(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> encode(AmqpCompleted data) throws AmqpEncodingError;

    public Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> decodeAmqpCompleted(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> unmarshalAmqpCompleted(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpXid data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpXid(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpXid(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpList data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpList(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpList(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<Integer> encode(AmqpUshort data) throws AmqpEncodingError;

    public Encoded<Integer> decodeAmqpUshort(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<Integer> unmarshalAmqpUshort(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpFooter data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpFooter(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpFooter(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> encode(AmqpSource data) throws AmqpEncodingError;

    public Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> decodeAmqpSource(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> unmarshalAmqpSource(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpSaslChallenge data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpSaslChallenge(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpSaslChallenge(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> encode(AmqpAttach data) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> decodeAmqpAttach(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpList<AmqpType<?, ?>>> unmarshalAmqpAttach(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<String> encode(AmqpString data) throws AmqpEncodingError;

    public Encoded<String> decodeAmqpString(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<String> unmarshalAmqpString(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> encode(AmqpTarget data) throws AmqpEncodingError;

    public Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> decodeAmqpTarget(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>>> unmarshalAmqpTarget(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<Short> encode(AmqpUbyte data) throws AmqpEncodingError;

    public Encoded<Short> decodeAmqpUbyte(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<Short> unmarshalAmqpUbyte(DataInput in) throws IOException, AmqpEncodingError;

    public Encoded<Long> encode(AmqpUint data) throws AmqpEncodingError;

    public Encoded<Long> decodeAmqpUint(Buffer source, int offset) throws AmqpEncodingError;

    public Encoded<Long> unmarshalAmqpUint(DataInput in) throws IOException, AmqpEncodingError;
}