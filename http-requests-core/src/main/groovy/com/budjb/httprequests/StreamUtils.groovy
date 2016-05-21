/*
 * Copyright 2016 Bud Byrd
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
package com.budjb.httprequests

abstract class StreamUtils {
    /**
     * Reads a <code>String</code> from the given input stream with the given character set.
     *
     * @param inputStream Input stream to read a string from.
     * @param characterSet Character set of the String.
     * @return The string read from the input stream.
     */
    static String readString(InputStream inputStream, String characterSet) {
        return new String(readBytes(inputStream), characterSet)
    }

    /**
     * Reads a byte array from the given input stream.
     *
     * @param inputStream Input stream to read from.
     * @return Byte array read from the input stream.
     */
    static byte[] readBytes(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()

        byte[] buffer = new byte[1024]

        while (true) {
            int length = inputStream.read(buffer)

            if (length == -1) {
                break
            }

            outputStream.write(buffer, 0, length)
        }

        return outputStream.toByteArray()
    }

    /**
     * Shovels data from an {@link InputStream} to an {@link OutputStream}. Streams are not closed
     * as part of this operation.
     *
     * @param inputStream Input stream to read from.
     * @param outputStream Output stream to write to.
     * @param size Number of bytes to read at a time.
     */
    static void shovel(InputStream inputStream, OutputStream outputStream, int size = 8192) {
        byte[] buffer = new byte[size]
        int read

        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read)
        }
    }
}
