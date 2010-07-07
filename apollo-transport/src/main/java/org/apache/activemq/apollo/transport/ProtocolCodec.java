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
package org.apache.activemq.apollo.transport;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.fusesource.hawtbuf.Buffer;


/**
 * Interface to encode and decode commands in and out of a a non blocking channel.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public interface ProtocolCodec {

    /**
     * @return The name of the protocol associated with the the channel codec.
     */
    String protocol();

    ///////////////////////////////////////////////////////////////////
    //
    // Methods related with reading from the channel
    //
    ///////////////////////////////////////////////////////////////////

    /**
     * @param channel
     */
    public void setReadableByteChannel(ReadableByteChannel channel);

    /**
     * Non-blocking channel based decoding.
     * 
     * @return
     * @throws IOException
     */
    Object read() throws IOException;

    /**
     * Pushes back a buffer as being unread.  The protocol
     * discriminator may do this before before any reads occur.
     *
     * @param buffer
     */
    void unread(Buffer buffer);

    /**
     * @return The number of bytes received.
     */
    public long getReadCounter();


    ///////////////////////////////////////////////////////////////////
    //
    // Methods related with writing to the channel
    //
    ///////////////////////////////////////////////////////////////////


    enum BufferState {
        EMPTY,
        WAS_EMPTY,
        NOT_EMPTY,
        FULL,
    }

    public void setWritableByteChannel(WritableByteChannel channel);

    /**
     * Non-blocking channel based encoding.
     *
     * @return true if the write completed.
     * @throws IOException
     */
    BufferState write(Object value) throws IOException;

    /**
     * Attempts to complete the previous write which did not complete.
     * @return
     * @throws IOException
     */
    BufferState flush() throws IOException;

    /**
     * @return The number of bytes written.
     */
    public long getWriteCounter();


}
