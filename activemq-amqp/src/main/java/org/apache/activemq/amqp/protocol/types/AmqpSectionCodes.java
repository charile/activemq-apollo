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

import java.lang.Long;
import java.util.HashMap;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.types.AmqpUint;

public enum AmqpSectionCodes {

    /**
     * <p>
     * type.
     * </p>
     */
    HEADER(new Long("0")),
    /**
     * <p>
     * type.
     * </p>
     */
    PROPERTIES(new Long("1")),
    /**
     * <p>
     * type.
     * </p>
     */
    FOOTER(new Long("2")),
    /**
     * <p>
     * .
     * </p>
     */
    MAP_DATA(new Long("3")),
    /**
     * <p>
     * .
     * </p>
     */
    LIST_DATA(new Long("4")),
    /**
     * <p>
     * type).
     * </p>
     */
    DATA(new Long("5"));

    private static final HashMap<Long, AmqpSectionCodes> LOOKUP = new HashMap<Long, AmqpSectionCodes>(2);
    static {
        for (AmqpSectionCodes sectionCodes : AmqpSectionCodes.values()) {
            LOOKUP.put(sectionCodes.value.getValue(), sectionCodes);
        }
    }

    private final AmqpUint value;

    private AmqpSectionCodes(Long value) {
        this.value = new AmqpUint.AmqpUintBean(value);
    }

    public final AmqpUint getValue() {
        return value;
    }

    public static final AmqpSectionCodes get(AmqpUint value) throws AmqpEncodingError{
        AmqpSectionCodes sectionCodes= LOOKUP.get(value.getValue());
        if (sectionCodes == null) {
            //TODO perhaps this should be an IllegalArgumentException?
            throw new AmqpEncodingError("Unknown sectionCodes: " + value + " expected one of " + LOOKUP.keySet());
        }
        return sectionCodes;
    }
}