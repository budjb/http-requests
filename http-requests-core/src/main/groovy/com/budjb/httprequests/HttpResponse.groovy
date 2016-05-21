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

import com.budjb.httprequests.converter.EntityConverterManager
import com.budjb.httprequests.exception.UnsupportedConversionException

/**
 * An object that represents the response of an HTTP request.
 */
abstract class HttpResponse implements Closeable {
    /**
     * Default character set of the response.
     */
    private static final DEFAULT_CHARSET = 'UTF-8'

    /**
     * The HTTP status of the response.
     */
    int status

    /**
     * Content type of the response.
     */
    String contentType

    /**
     * Headers of the response.
     */
    Map<String, List<String>> headers = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER)

    /**
     * A list of allowed HTTP methods. Typically returned from OPTIONS requests.
     */
    List<HttpMethod> allow = []

    /**
     * The character set of the response.
     */
    protected String charset

    /**
     * Response entity.
     */
    InputStream entity

    /**
     * Request properties used to configure the request that generated this response.
     */
    HttpRequest request

    /**
     * Converter manager.
     */
    EntityConverterManager converterManager

    /**
     * A byte array that contains the entire entity as a buffer. This is only filled when
     * {@link HttpRequest#bufferResponseEntity} is <code>true</code>.
     */
    private byte[] entityBuffer

    /**
     * Constructor.
     *
     * @param request Request properties used to make the request.
     * @param converterManager Converter manager.
     */
    HttpResponse(HttpRequest request, EntityConverterManager converterManager) {
        this.request = request
        this.converterManager = converterManager
    }

    /**
     * Sets the content type of the response.
     *
     * @param contentType Content type of the response.
     */
    void setContentType(String contentType) {
        if (!contentType) {
            this.contentType = null
            this.charset = null
            return
        }

        List<String> parts = contentType.tokenize(';')

        this.contentType = parts.remove(0)

        TreeMap<String, String> parameters = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER)
        for (String part : parts) {
            List<String> paramParts = part.tokenize('=').collect { it.trim() }

            String key = paramParts[0]
            String value = paramParts.size() > 1 ? paramParts[1] : null

            parameters.put(key, value)
        }

        String charset = DEFAULT_CHARSET

        if (parameters.containsKey('charset')) {
            String cs = parameters.get('charset')
            if (cs) {
                charset = cs
            }
        }

        this.charset = charset
    }

    /**
     * Sets the response headers from the given map.
     *
     * @param headers Response headers of the request.
     */
    void setHeaders(Map<String, ?> headers) {
        this.headers.clear()

        headers.each { name, values ->
            if (name) {
                setHeader(name, values)
            }
        }
    }

    /**
     * Sets the response header with the given name, parsing out individual values.
     *
     * @param name Name of the header.
     * @param value Value(s) of the header.
     */
    void setHeader(String name, Object value) {
        if (!headers.containsKey(name)) {
            headers.put(name, [])
        }

        if (value instanceof Collection) {
            value.each {
                headers.get(name).add(it.toString())
            }
        }
        else {
            value.toString().split(/,\s*/).each {
                headers.get(name).add(it.toString())
            }
        }
    }

    /**
     * Returns the first value of the header with the given name, or null if it doesn't exist.
     *
     * @param name Name of the header.
     * @return The first value of the requested header, or null if it doesn't exist.
     */
    String getHeader(String name) {
        if (headers.containsKey(name)) {
            return headers.get(name).first()
        }
        return null
    }

    /**
     * Returns a list of values of the header with the given name, or null if the header doesn't exist.
     *
     * @param name Name of the header.
     * @return A list of values of the requested header, or null if it doesn't exist.
     */
    List<String> getHeaders(String name) {
        if (headers.containsKey(name)) {
            return headers.get(name)
        }
        return null
    }

    /**
     * Return all response headers.
     *
     * This method returns a map where the key is the name of the header, and the value is a list of values
     * for the response header. This is true even when there is only one value for the header.
     *
     * @return A copy of the map containing the response headers.
     */
    Map<String, List<String>> getHeaders() {
        return headers.clone() as Map<String, List<String>>
    }

    /**
     * Return all response headers.
     *
     * This method returns a map where the key is the name of the header, and the value is either the single value
     * for that header or a list of values if multiple were received.
     *
     * @return A copy of the map containing the response headers.
     */
    Map<String, Object> getFlattenedHeaders() {
        return headers.collectEntries { name, values ->
            if (values.size() == 1) {
                return [(name): values[0]]
            }
            else {
                return [(name): values]
            }
        } as Map<String, Object>
    }

    /**
     * Sets the entity.
     *
     * @param inputStream
     */
    void setEntity(InputStream inputStream) {
        entity = null
        entityBuffer = null

        if (inputStream == null) {
            return
        }

        PushbackInputStream pushBackInputStream = new PushbackInputStream(inputStream)
        int read = pushBackInputStream.read()
        if (read == -1) {
            pushBackInputStream.close()
            return
        }

        pushBackInputStream.unread(read)

        if (request.isBufferResponseEntity()) {
            entityBuffer = StreamUtils.readBytes(pushBackInputStream)
            pushBackInputStream.close()
        }
        else {
            entity = new EntityInputStream(pushBackInputStream)
        }
    }

    /**
     * Returns the entity.
     *
     * If the entity was buffered, calling this method will always return a new InputStream.
     * Otherwise, the original InputStream will be returned. Note that if the entity is not
     * buffered and it was already read once, subsequent reading will likely fail.
     *
     * @return The response entity.
     */
    InputStream getEntity() {
        if (entityBuffer != null) {
            return new EntityInputStream(new ByteArrayInputStream(entityBuffer))
        }
        return entity
    }

    /**
     * Returns the entity, converted to the given class type.
     *
     * @param type Class type to convert the entity to.
     * @return The converted entity.
     * @throws UnsupportedConversionException when no converter is found to convert the entity.
     */
    public <T> T getEntity(Class<T> type) throws UnsupportedConversionException, IOException {
        InputStream entity = getEntity()
        T object = converterManager.read(type, entity, getContentType(), getCharset())
        entity.close()

        return object
    }

    /**
     * Closes the entity and releases any system resources associated
     * with it. If the response is already closed then invoking this
     * method has no effect.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    void close() throws IOException {
        if (entity != null) {
            entity.close()
        }
    }

    /**
     * Returns whether the response contains an entity.
     *
     * @return Whether the response contains an entity.
     */
    boolean hasEntity() {
        return entity != null || entityBuffer != null
    }

    /**
     * Returns the allowed HTTP methods returned in the response.
     *
     * @return Allowed HTTP methods returned in the response.
     */
    List<HttpMethod> getAllow() {
        if (!allow && headers.containsKey('Allow')) {
            allow = headers.get('Allow').collect { HttpMethod.valueOf(it) }
        }

        return allow
    }

    /**
     * Returns the Content-Type of the response.
     *
     * @return Content-Type of the response.
     */
    String getContentType() {
        if (!contentType && headers.containsKey('Content-Type')) {
            setContentType(headers.get('Content-Type').first())
        }
        return contentType
    }

    /**
     * Returns the character set of the response.
     *
     * @return Character set of the response.
     */
    String getCharset() {
        if (!charset && !contentType && headers.containsKey('Content-Type')) {
            setContentType(headers.get('Content-Type').first())
        }
        return charset
    }

    /**
     * Add a value to the given header name.
     *
     * @param key Name of the value.
     * @param value Value of the header.
     */
    void addHeader(String key, String value) {
        if (!headers.containsKey(key)) {
            headers.put(key, [])
        }
        headers.get(key).add(value)
    }

    /**
     * Returns whether the response entity is buffered. If there is no entity, will return <code>false</code>.
     *
     * @return Whether the response entity is buffered.
     */
    boolean isEntityBuffered() {
        return entityBuffer != null
    }
}
