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
package org.apache.activemq.apollo.util

import java.io.InputStream
import java.util.Properties

/**
 * <p>
 * Used to discover classes using the META-INF discovery trick.
 * </p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
case class ClassFinder[T](path:String, loaders:Seq[ClassLoader]=Thread.currentThread.getContextClassLoader::Nil) {

  def find(): List[Class[T]] = {
    var classes = List[Class[T]]()
    loaders.foreach { loader=>

      val resources = loader.getResources(path)
      var classNames: List[String] = Nil
      while(resources.hasMoreElements) {
        val url = resources.nextElement;
        val p = loadProperties(url.openStream)
        val enum = p.keys
        while (enum.hasMoreElements) {
          classNames = classNames ::: enum.nextElement.asInstanceOf[String] :: Nil
        }
      }
      classNames = classNames.distinct

      classes :::= classNames.map { name=>
        loader.loadClass(name).asInstanceOf[Class[T]]
      }

    }

    return classes.distinct
  }

  private def loadProperties(is:InputStream):Properties = {
    if( is==null ) {
      return null;
    }
    try {
      val p = new Properties()
      p.load(is);
      return p
    } catch {
      case e:Exception =>
      return null
    } finally {
      try {
        is.close()
      } catch {
        case _ =>
      }
    }
  }
}