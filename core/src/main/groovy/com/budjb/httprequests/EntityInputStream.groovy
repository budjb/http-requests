package com.budjb.httprequests

class EntityInputStream extends InputStream {
    /**
     * Input stream this stream wraps.
     */
    private InputStream source

    /**
     * Whether the stream has been closed.
     */
    boolean closed = false

    /**
     * Constructor that takes in another {@link InputStream}.
     *
     * @param source Input stream to wrap.
     */
    EntityInputStream(InputStream source) {
        this.source = source
    }

    /**
     * Returns whether the {@link InputStream} has been closed.
     *
     * @return Whether the {@link InputStream} has been closed.
     */
    boolean isClosed() {
        return closed
    }

    /**
     * Reads the next byte of data from the input stream. The value byte is
     * returned as an <code>int</code> in the range <code>0</code> to
     * <code>255</code>. If no byte is available because the end of the stream
     * has been reached, the value <code>-1</code> is returned. This method
     * blocks until input data is available, the end of the stream is detected,
     * or an exception is thrown.
     *
     * <p> A subclass must provide an implementation of this method.
     *
     * @return the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception IOException  if an I/O error occurs.
     */
    @Override
    int read() throws IOException {
        if (closed) {
            throw new IOException("stream is closed")
        }
        return source.read()
    }

    /**
     * Closes the underlying {@link InputStream}, if it has not already been closed.
     *
     * @throws IOException
     */
    void close() throws IOException {
        if (!closed) {
            closed = true
            source.close()
        }

    }
}
