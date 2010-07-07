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

import org.apache.activemq.apollo.store.StoreFactory
import org.apache.activemq.apollo.dto.{HawtDBStoreDTO, StoreDTO}
import org.apache.activemq.apollo.broker.{Reporting, ReporterLevel, Reporter}
import ReporterLevel._

/**
 * <p>
 * Hook to use a HawtDBStore when a HawtDBStoreDTO is
 * used in a broker configuration.
 * </p>
 * <p>
 * This class is discovered using the following resource file:
 * <code>META-INF/services/org.apache.activemq.apollo/stores</code>
 * </p>
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
class HawtDBMemoryPoolSPI extends StoreFactory.SPI {

  def create(config: StoreDTO) = {
    if( config.isInstanceOf[HawtDBStoreDTO]) {
      new HawtDBStore
    } else {
      null
    }
  }

   def validate(config: StoreDTO, reporter:Reporter):ReporterLevel = {
     if( config.isInstanceOf[HawtDBStoreDTO]) {
       HawtDBStore.validate(config.asInstanceOf[HawtDBStoreDTO], reporter)
     } else {
       null
     }
   }
}