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
}
