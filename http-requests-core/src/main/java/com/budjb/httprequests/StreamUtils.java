/*
 * Copyright 2016-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.budjb.httprequests;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class StreamUtils {
    /**
     * Reads a <code>String</code> from the given input stream with the given character set.
     *
     * @param inputStream  Input stream to read a string from.
     * @param characterSet Character set of the String.
     * @return The string read from the input stream.
     * @throws IOException When an IO exception occurs.
     */
    public static String readString(InputStream inputStream, String characterSet) throws IOException {
        return new String(readBytes(inputStream), characterSet);
    }

    /**
     * Reads a byte array from the given input stream.
     *
     * @param inputStream Input stream to read from.
     * @return Byte array read from the input stream.
     * @throws IOException When an IO exception occurs.
     */
    public static byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];

        while (true) {
            int length = inputStream.read(buffer);

            if (length == -1) {
                break;
            }

            outputStream.write(buffer, 0, length);
        }

        return outputStream.toByteArray();
    }

    /**
     * Shovels data from an {@link InputStream} to an {@link OutputStream}. Streams are not closed
     * as part of this operation. The input stream is read in chunks of 8192 bytes at a time.
     *
     * @param inputStream  Input stream to read from.
     * @param outputStream Output stream to write to.
     * @throws IOException When an IO exception occurs.
     */
    public static void shovel(InputStream inputStream, OutputStream outputStream) throws IOException {
        shovel(inputStream, outputStream, 8192);
    }

    /**
     * Shovels data from an {@link InputStream} to an {@link OutputStream}. Streams are not closed
     * as part of this operation.
     *
     * @param inputStream  Input stream to read from.
     * @param outputStream Output stream to write to.
     * @param size         Number of bytes to read at a time.
     * @throws IOException When an IO exception occurs.
     */
    public static void shovel(InputStream inputStream, OutputStream outputStream, int size) throws IOException {
        byte[] buffer = new byte[size];
        int read;

        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }
    }
}
