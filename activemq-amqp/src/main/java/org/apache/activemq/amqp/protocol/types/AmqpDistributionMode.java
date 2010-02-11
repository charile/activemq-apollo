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

/**
 * Represents a Link distribution policy
 * <p>
 * Policies for distributing Messages when multiple Links are connected to the same Node.
 * </p>
 */
public enum AmqpDistributionMode {

    /**
     * <p>
     * once successfully transferred over the Link, the Message will no longer be available to
     * other Links from the same Node
     * </p>
     */
    DESTRUCTIVE(new Long("1")),
    /**
     * <p>
     * once successfully transferred over the Link, the Message is still available for other
     * Links from the same Node
     * </p>
     */
    NON_DESTRUCTIVE(new Long("2"));

    private static final HashMap<Long, AmqpDistributionMode> LOOKUP = new HashMap<Long, AmqpDistributionMode>(2);
    static {
        for (AmqpDistributionMode distributionMode : AmqpDistributionMode.values()) {
            LOOKUP.put(distributionMode.value.getValue(), distributionMode);
        }
    }

    private final AmqpUint value;

    private AmqpDistributionMode(Long value) {
        this.value = new AmqpUint.AmqpUintBean(value);
    }

    public final AmqpUint getValue() {
        return value;
    }

    public static final AmqpDistributionMode get(AmqpUint value) throws AmqpEncodingError{
        AmqpDistributionMode distributionMode= LOOKUP.get(value.getValue());
        if (distributionMode == null) {
            //TODO perhaps this should be an IllegalArgumentException?
            throw new AmqpEncodingError("Unknown distributionMode: " + value + " expected one of " + LOOKUP.keySet());
        }
        return distributionMode;
    }
}