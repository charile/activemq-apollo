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
import java.util.ArrayList;

/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
@XmlRootElement(name = "destination")
@XmlAccessorType(XmlAccessType.FIELD)
public class DestinationDTO extends StringIdDTO {

    /**
     * The name of the destination.  You can use wild cards.
     */
	@XmlAttribute
	public String name;

    /**
     * If set to true, then routing then there is no difference between
     * sending to a queue or topic of the same name.  The first time
     * a queue subscriptions is created, it will act like if a durable
     * subscription was created on the topic. 
     */
    @XmlAttribute
    public Boolean unified;

    @XmlElement(name="slow-consumer-policy")
    public String slow_consumer_policy;

    @XmlElement(name="acl")
    public DestinationAclDTO acl;

}
