/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
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
package org.apache.activemq.apollo.dto;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.*;

/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
@XmlRootElement(name = "queue")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueueDTO {

    /*
     * The destination this queue is associated with.  You can use wild cards.
     */
    @XmlAttribute
    public String destination;

    /*
     * The kind of queue.  It may be "ptp" for standard
     * point to point queues or "ds" for durable subscriptions.
     * If not set, then this configuration applies to all queue types.
     */
    @XmlAttribute
    public String kind;

    /**
     * If the kind is "ds" then you can specify which client
     * id this configuration should match.
     */
    @XmlAttribute(name="client-id")
    public String client_id;

    /**
     * If the kind is "ds" then you can specify which subscription
     * id this configuration should match.
     */
    @XmlAttribute(name="subscription-id")
    public String subscription_id;


    /**
     *  The amount of memory buffer space for receiving messages.
     */
    @XmlAttribute(name="producer-buffer")
    @JsonProperty("producer_buffer")
    public Integer producer_buffer;

    /**
     *  The amount of memory buffer space for the queue..
     */
    @XmlAttribute(name="queue-buffer")
    @JsonProperty("queue_buffer")
    public Integer queue_buffer;

    /**
     *  The amount of memory buffer space to use per subscription.
     */
    @XmlAttribute(name="consumer-buffer")
    @JsonProperty("consumer_buffer")
    public Integer consumer_buffer;

    /**
     * Subscribers that consume slower than this rate per seconds will be considered
     * slow.  Once a consumer is considered slow, we may switch to disk spooling.
     */
    @XmlAttribute(name="slow-subscription-rate")
    @JsonProperty("slow_subscription_rate")
    public Integer slow_subscription_rate;

    /**
     * The number of milliseconds between slow consumer checks.
     */
    @XmlAttribute(name="slow-check-interval")
    @JsonProperty("slow_check_interval")
    public Long slow_check_interval;

    /**
     * Should this queue persistently store it's entries?
     */
    @XmlAttribute(name="persistent")
    @JsonProperty("persistent")
    public Boolean persistent;

    /**
     * Should messages be flushed or swapped out of memory if
     * no consumers need the message?
     */
    @XmlAttribute(name="flush-to-store")
    @JsonProperty("flush_to_store")
    public Boolean flush_to_store;

    /**
     * The number max number of flushed queue entries to load
     * for the store at a time.  Not that Flushed entires are just
     * reference pointers to the actual messages.  When not loaded,
     * the batch is referenced as sequence range to conserve memory.
     */
    @XmlAttribute(name="flush-range-size")
    @JsonProperty("flush_range_size")
    public Integer flush_range_size;

    /**
     * The number of intervals that a consumer must not meeting the subscription rate before it is
     * flagged as a slow consumer.
     */
    @XmlAttribute(name="max-slow-intervals")
    @JsonProperty("max_slow_intervals")
    public Integer max_slow_intervals;

}
