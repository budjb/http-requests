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
class HttpResponse implements Closeable {
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
    String charset = 'UTF-8'

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
     * Sets the character set of the response.
     *
     * @param charset Character set of the response.
     */
    void setCharset(String charset) {
        if (charset) {
            this.charset = charset
        }
    }

    /**
     * Sets the content type of the response.
     *
     * @param contentType Content type of the response.
     */
    void setContentType(String contentType) {
        if (!contentType) {
            return
        }

        int index = contentType.indexOf(';')

        if (index == -1) {
            this.contentType = contentType
            return
        }

        this.contentType = contentType.substring(0, index)
        if (index + 1 < contentType.size()) {
            String charset = contentType.substring(index + 1).tokenize(';').find { it.toLowerCase().startsWith('charset') }
            if (charset) {
                List<String> tokens = charset.tokenize('=')
                if (tokens.size() == 2) {
                    setCharset(tokens[1])
                }
            }
        }
    }

    /**
     * Sets the response headers from the given map.
     *
     * @param headers Response headers of the request.
     */
    void setHeaders(Map<String, ?> headers) {
        headers.each { name, values ->
            setHeader(name, values)
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

        if (!request || request.isBufferResponseEntity()) {
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
     * with it. If the stream is already closed then invoking this
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
            contentType = headers.get('Content-Type').first()
        }
        return contentType
    }

    /**
     * Returns the character set of the response.
     *
     * @return Character set of the response.
     */
    String getCharset() {
        if (!charset && headers.containsKey('Content-Type')) {
            String contentType = headers.get('Content-Type').first()

            List<String> parameters = contentType.split(';')

            if (parameters.size() > 1) {
                parameters.remove(0)

                parameters.each {
                    List<String> parts = it.split(/\s*=\s*/)

                    if (parts.size() == 2 && parts[0].equalsIgnoreCase('charset')) {
                        charset = parts[1]
                    }
                }
            }
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
}
