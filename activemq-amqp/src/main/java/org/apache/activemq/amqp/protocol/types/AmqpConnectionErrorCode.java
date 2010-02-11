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

import java.lang.Integer;
import java.util.HashMap;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.types.AmqpUshort;

/**
 * Represents a codes used to indicate the reason for closure
 */
public enum AmqpConnectionErrorCode {

    /**
     * <p>
     * An operator intervened to close the Connection for some reason. The client may retry at
     * some later date.
     * </p>
     */
    CONNECTION_FORCED(new Integer("320")),
    /**
     * <p>
     * The peer closed the connection because of an internal error. The peer may require
     * intervention by an operator in order to resume normal operations.
     * </p>
     */
    INTERNAL_ERROR(new Integer("500")),
    /**
     * <p>
     * A valid frame header cannot be formed from the incoming byte stream.
     * </p>
     */
    FRAMING_ERROR(new Integer("501"));

    private static final HashMap<Integer, AmqpConnectionErrorCode> LOOKUP = new HashMap<Integer, AmqpConnectionErrorCode>(2);
    static {
        for (AmqpConnectionErrorCode connectionErrorCode : AmqpConnectionErrorCode.values()) {
            LOOKUP.put(connectionErrorCode.value.getValue(), connectionErrorCode);
        }
    }

    private final AmqpUshort value;

    private AmqpConnectionErrorCode(Integer value) {
        this.value = new AmqpUshort.AmqpUshortBean(value);
    }

    public final AmqpUshort getValue() {
        return value;
    }

    public static final AmqpConnectionErrorCode get(AmqpUshort value) throws AmqpEncodingError{
        AmqpConnectionErrorCode connectionErrorCode= LOOKUP.get(value.getValue());
        if (connectionErrorCode == null) {
            //TODO perhaps this should be an IllegalArgumentException?
            throw new AmqpEncodingError("Unknown connectionErrorCode: " + value + " expected one of " + LOOKUP.keySet());
        }
        return connectionErrorCode;
    }
}