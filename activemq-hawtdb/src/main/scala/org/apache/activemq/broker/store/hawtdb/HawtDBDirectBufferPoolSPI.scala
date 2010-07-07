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
package org.apache.activemq.broker.store.hawtdb

import java.io.File
import java.lang.String
import org.apache.activemq.apollo.DirectBufferPoolFactory

/**
 * <p>
 * Hook to use a HawtDBDirectBufferPool for the memory pool implementation.
 * </p>
 * <p>
 * This class is discovered using the following resource file:
 * <code>META-INF/services/org.apache.activemq.apollo/direct-buffer-pools</code>
 * </p>
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
class HawtDBDirectBufferPoolSPI extends DirectBufferPoolFactory.SPI {

  val prefix: String = "hawtdb:"

  def create(config: String) = {
    if( config.startsWith(prefix) ) {
      val file = new File(config.substring(prefix.length))
      new HawtDBDirectBufferPool(file)
    } else {
      null
    }
  }

   def validate(config: String):Boolean = {
     config.startsWith(prefix) && !config.substring(prefix.length).isEmpty
   }
}