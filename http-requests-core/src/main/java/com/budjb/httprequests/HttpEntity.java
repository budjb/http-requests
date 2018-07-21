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

import com.budjb.httprequests.exception.EmptyEntityException;
import com.budjb.httprequests.exception.EntityException;
import com.budjb.httprequests.exception.NullEntityException;

import java.io.*;

/**
 * Contains an HTTP entity, along with its content type, and optionally its character set.
 */
public class HttpEntity implements Closeable {
    /**
     * Default content type.
     */
    private final static String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    /**
     * Input stream containing the entity.
     */
    private final InputStream inputStream;

    /**
     * Content type of the entity.
     */
    private final String contentType;

    /**
     * Character set of the entity.
     */
    private final String charSet;

    private byte[] entityBuffer;

    /**
     * Constructor.
     *
     * @param inputStream Input stream containing the entity.
     * @param contentType Content type of the entity.
     * @param charSet     Character set of the entity.
     * @throws IOException     When an IO exception occurs.
     * @throws EntityException When an entity is null or empty.
     */
    public HttpEntity(InputStream inputStream, String contentType, String charSet) throws IOException, EntityException {
        this.inputStream = requireNonEmptyStream(inputStream);
        this.contentType = contentType;
        this.charSet = charSet;
    }

    /**
     * Constructor.
     *
     * @param inputStream Input stream containing the entity.
     * @throws IOException     When an IO exception occurs.
     * @throws EntityException When an entity is null or empty.
     */
    public HttpEntity(InputStream inputStream) throws IOException, EntityException {
        this(inputStream, null, null);
    }

    /**
     * Constructor.
     *
     * @param inputStream Input stream containing the entity.
     * @param contentType Content type of the entity.
     * @throws IOException     When an IO exception occurs.
     * @throws EntityException When an entity is null or empty.
     */
    public HttpEntity(InputStream inputStream, String contentType) throws IOException, EntityException {
        this(inputStream, contentType, null);
    }

    /**
     * Inspects the given input stream and ensures it is non-null and that there are contents.
     * TODO: do we actually need to validate there are contents?
     *
     * @param inputStream Input stream containing the entity.
     * @return The entity.
     * @throws IOException     When an IO exception occurs.
     * @throws EntityException When an entity is null or empty.
     */
    private InputStream requireNonEmptyStream(InputStream inputStream) throws IOException, EntityException {
        if (inputStream == null) {
            throw new NullEntityException();
        }

        PushbackInputStream pushBackInputStream = new PushbackInputStream(inputStream);
        int read = pushBackInputStream.read();
        if (read == -1) {
            pushBackInputStream.close();
            throw new EmptyEntityException();
        }

        pushBackInputStream.unread(read);

        return pushBackInputStream;
    }

    /**
     * Returns the character set of the entity.
     *
     * @return The character set of the entity.
     */
    public String getCharSet() {
        return charSet;
    }

    /**
     * Returns the input stream containing the entity.
     *
     * @return The input stream containing the entity.
     */
    public InputStream getInputStream() {
        if (isBuffered()) {
            return new ByteArrayInputStream(entityBuffer);
        }
        else {
            return inputStream;
        }
    }

    /**
     * Returns the content type of the entity.
     *
     * @return The content type of the entity.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Returns the content type with the character set appended, if set.
     *
     * @return The full content type string, or {@code null} if one is not set.
     */
    public String getFullContentType() {
        String contentType = this.contentType;

        if (contentType == null) {
            contentType = DEFAULT_CONTENT_TYPE;
        }

        if (charSet == null) {
            return contentType;
        }
        else {
            return contentType + "; charset=" + charSet;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    /**
     * Reads the entity contained in the given input stream if data is present.
     *
     * @throws IOException When an IO exception occurs.
     */
    public void buffer() throws IOException {
        entityBuffer = StreamUtils.readBytes(inputStream);
        inputStream.close();
    }


    /**
     * Returns whether the response entity is buffered. If there is no entity, will return {@code false}.
     *
     * @return Whether the response entity is buffered.
     */
    public boolean isBuffered() {
        return entityBuffer != null;
    }
}
