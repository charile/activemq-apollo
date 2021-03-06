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

import java.lang.Short;
import java.util.HashMap;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.types.AmqpUbyte;

/**
 * <p>
 * An enumeration of the defined states of a Message on a Node.
 * </p>
 */
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//
//!!!!!!!!THIS CLASS IS AUTOGENERATED DO NOT MODIFY DIRECTLY!!!!!!!!!!!!//
//!!!!!!Instead, modify the generator in activemq-amqp-generator!!!!!!!!//
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//
public enum AmqpMessageState {

    /**
     * <p>
     * The AVAILABLE state.
     * </p>
     */
    AVAILABLE(new Short("0")),
    /**
     * <p>
     * The ACQUIRED state.
     * </p>
     */
    ACQUIRED(new Short("1")),
    /**
     * <p>
     * The ARCHIVED state.
     * </p>
     */
    ARCHIVED(new Short("2"));

    private static final HashMap<Short, AmqpMessageState> LOOKUP = new HashMap<Short, AmqpMessageState>(2);
    static {
        for (AmqpMessageState messageState : AmqpMessageState.values()) {
            LOOKUP.put(messageState.value.getValue(), messageState);
        }
    }

    private final AmqpUbyte value;

    private AmqpMessageState(Short value) {
        this.value = new AmqpUbyte.AmqpUbyteBean(value);
    }

    public final AmqpUbyte getValue() {
        return value;
    }

    public static final AmqpMessageState get(AmqpUbyte value) throws AmqpEncodingError{
        AmqpMessageState messageState= LOOKUP.get(value.getValue());
        if (messageState == null) {
            //TODO perhaps this should be an IllegalArgumentException?
            throw new AmqpEncodingError("Unknown messageState: " + value + " expected one of " + LOOKUP.keySet());
        }
        return messageState;
    }
}