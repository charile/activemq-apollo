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
 * Represents a codes used to indicate the reason for unlink
 */
public enum AmqpLinkErrorCode {

    /**
     * <p>
     * An operator intervened to unlink for some reason.
     * </p>
     */
    UNLINK_FORCED(new Integer("320")),
    /**
     * <p>
     * The source with which the link was associated has been destroyed.
     * </p>
     */
    SOURCE_DESTROYED(new Integer("321")),
    /**
     * <p>
     * The target with which the link was associated has been destroyed.
     * </p>
     */
    TARGET_DESTROYED(new Integer("322")),
    NOT_ALLOWED(new Integer("401")),
    /**
     * <p>
     * The source with which the link was requested to associated does not exist.
     * </p>
     */
    SOURCE_NOT_FOUND(new Integer("404")),
    /**
     * <p>
     * The target with which the link was requested to associated does not exist.
     * </p>
     */
    TARGET_NOT_FOUND(new Integer("405")),
    /**
     * <p>
     * The error condition which caused the unlink was is cannot be described by any other
     * existing link-error-code.
     * </p>
     */
    OTHER_ERROR(new Integer("599"));

    private static final HashMap<Integer, AmqpLinkErrorCode> LOOKUP = new HashMap<Integer, AmqpLinkErrorCode>(2);
    static {
        for (AmqpLinkErrorCode linkErrorCode : AmqpLinkErrorCode.values()) {
            LOOKUP.put(linkErrorCode.value.getValue(), linkErrorCode);
        }
    }

    private final AmqpUshort value;

    private AmqpLinkErrorCode(Integer value) {
        this.value = new AmqpUshort.AmqpUshortBean(value);
    }

    public final AmqpUshort getValue() {
        return value;
    }

    public static final AmqpLinkErrorCode get(AmqpUshort value) throws AmqpEncodingError{
        AmqpLinkErrorCode linkErrorCode= LOOKUP.get(value.getValue());
        if (linkErrorCode == null) {
            //TODO perhaps this should be an IllegalArgumentException?
            throw new AmqpEncodingError("Unknown linkErrorCode: " + value + " expected one of " + LOOKUP.keySet());
        }
        return linkErrorCode;
    }
}