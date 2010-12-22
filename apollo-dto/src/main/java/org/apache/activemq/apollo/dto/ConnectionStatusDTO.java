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



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * </p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
@XmlRootElement(name="connection_status")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConnectionStatusDTO extends ServiceStatusDTO {

    /**
     * The number of bytes that have been read from the connection.
     */
	@XmlAttribute(name="read_counter")
	public long read_counter;

    /**
     * The number of bytes that have been written to the connection.
     */
	@XmlAttribute(name="write_counter")
	public long write_counter;

    /**
     * The transport the connection is using.
     */
	@XmlAttribute
	public String transport;

    /**
     * The protocol the connection is using.
     */
	@XmlAttribute
	public String protocol;

    /**
     * The remote address of the connection
     */
	@XmlAttribute(name="remote_address")
	public String remote_address;

}
