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
 * Represents a error code used to identify the nature of an exception
 */
public enum AmqpSessionErrorCode {

    /**
     * <p>
     * The client attempted to work with a server entity to which it has no access due to
     * security settings.
     * </p>
     */
    UNAUTHORIZED_ACCESS(new Integer("4003")),
    /**
     * <p>
     * The client attempted to work with a server entity that does not exist.
     * </p>
     */
    NOT_FOUND(new Integer("4004")),
    /**
     * <p>
     * The client attempted to work with a server entity to which it has no access because
     * another client is working with it.
     * </p>
     */
    RESOURCE_LOCKED(new Integer("4005")),
    /**
     * <p>
     * The client requested a command that was not allowed because some precondition failed.
     * </p>
     */
    PRECONDITION_FAILED(new Integer("4006")),
    /**
     * <p>
     * A server entity the client is working with has been deleted.
     * </p>
     */
    RESOURCE_DELETED(new Integer("4008")),
    /**
     * <p>
     * The peer sent a command that is not permitted in the current state of the Session.
     * </p>
     */
    ILLEGAL_STATE(new Integer("4009")),
    /**
     * <p>
     * The peer sent more Message transfers than currently allowed on the Link.
     * </p>
     */
    TRANSFER_LIMIT_EXCEEDED(new Integer("4010")),
    /**
     * <p>
     * The command segments could not be decoded.
     * </p>
     */
    COMMAND_INVALID(new Integer("5003")),
    /**
     * <p>
     * The client exceeded its resource allocation.
     * </p>
     */
    RESOURCE_LIMIT_EXCEEDED(new Integer("5006")),
    /**
     * <p>
     * The peer tried to use a command a manner that is inconsistent with the semantics defined
     * in the specification.
     * </p>
     */
    NOT_ALLOWED(new Integer("5030")),
    /**
     * <p>
     * The command argument is malformed, i.e. it does not match the specified type. The
     * illegal-argument exception can be raised on execution of any command.
     * </p>
     */
    ILLEGAL_ARGUMENT(new Integer("5031")),
    /**
     * <p>
     * The peer tried to use functionality that is not implemented in its partner.
     * </p>
     */
    NOT_IMPLEMENTED(new Integer("5040")),
    /**
     * <p>
     * The peer could not complete the command because of an internal error. The peer may
     * require intervention by an operator in order to resume normal operations.
     * </p>
     */
    INTERNAL_ERROR(new Integer("5041")),
    /**
     * <p>
     * An invalid argument was passed to a command, and the operation could not proceed. An
     * invalid argument is not illegal (see illegal-argument), i.e. it matches the type
     * definition; however the particular value is invalid in this context.
     * </p>
     */
    INVALID_ARGUMENT(new Integer("5042")),
    /**
     * <p>
     * The rollback was caused for an unspecified reason.
     * </p>
     * <p>
     * Note that the AMQP container acting as the Transaction Resource may at any time decide
     * that the current transaction branch must be rolled back. In this case the peer acting
     * as the Transactional Resource may decide to spontaneously detach the Session with this
     * error code.
     * </p>
     * <p>
     * If the AMQP container acting as the Transaction Controller attempts to re-attach to the
     * Session after receiving this exception from the Resource spontaneously rolling back then
     * the Controller MUST manually rollback the current transaction before attempting any
     * further transactional work.
     * </p>
     */
    XA_RBROLLBACK(new Integer("6001")),
    /**
     * <p>
     * A transaction branch took too long.
     * </p>
     */
    XA_RBTIMEOUT(new Integer("6002")),
    /**
     * <p>
     * The transaction branch may have been heuristically completed.
     * </p>
     */
    XA_HEURHAZ(new Integer("6003")),
    /**
     * <p>
     * The transaction branch has been heuristically committed.
     * </p>
     */
    XA_HEURCOM(new Integer("6004")),
    /**
     * <p>
     * The transaction branch has been heuristically rolled back.
     * </p>
     */
    XA_HEURRB(new Integer("6005")),
    /**
     * <p>
     * The transaction branch has been heuristically committed and rolled back.
     * </p>
     */
    XA_HEURMIX(new Integer("6006")),
    /**
     * <p>
     * The transaction branch was read-only and has been committed.
     * </p>
     */
    XA_RDONLY(new Integer("6007"));

    private static final HashMap<Integer, AmqpSessionErrorCode> LOOKUP = new HashMap<Integer, AmqpSessionErrorCode>(2);
    static {
        for (AmqpSessionErrorCode sessionErrorCode : AmqpSessionErrorCode.values()) {
            LOOKUP.put(sessionErrorCode.value.getValue(), sessionErrorCode);
        }
    }

    private final AmqpUshort value;

    private AmqpSessionErrorCode(Integer value) {
        this.value = new AmqpUshort.AmqpUshortBean(value);
    }

    public final AmqpUshort getValue() {
        return value;
    }

    public static final AmqpSessionErrorCode get(AmqpUshort value) throws AmqpEncodingError{
        AmqpSessionErrorCode sessionErrorCode= LOOKUP.get(value.getValue());
        if (sessionErrorCode == null) {
            //TODO perhaps this should be an IllegalArgumentException?
            throw new AmqpEncodingError("Unknown sessionErrorCode: " + value + " expected one of " + LOOKUP.keySet());
        }
        return sessionErrorCode;
    }
}