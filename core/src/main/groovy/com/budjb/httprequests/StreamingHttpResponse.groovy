package com.budjb.httprequests

/**
 * An {@link HttpResponse} implementation that provides direct access to the response's {@link InputStream}.
 *
 * Authors should ensure that once the response object is no longer needed, the {@link #close} method
 * should be called so that the underlying resources are freed up.
 */
class StreamingHttpResponse extends HttpResponse implements Closeable {
    /**
     * The response's input stream.
     */
    InputStream inputStream

    /**
     * Returns the entity as a byte array. Note that calling this method will automatically close the input stream.
     *
     * @return The entity of the response as a byte array.
     */
    @Override
    byte[] getEntity() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()

        int read
        byte[] tmp = new byte[8192]

        while ((read = inputStream.read(tmp, 0, 1892)) != -1) {
            outputStream.write(tmp, 0, read)
        }

        outputStream.flush()
        setEntity(outputStream.toByteArray())
        inputStream.close()

        return super.getEntity()
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    void close() throws IOException {
        inputStream.close()
    }
}
