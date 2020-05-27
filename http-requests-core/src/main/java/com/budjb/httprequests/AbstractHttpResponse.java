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

import com.budjb.httprequests.converter.EntityConverterManager;
import com.budjb.httprequests.exception.UnsupportedConversionException;

import java.io.IOException;
import java.util.List;

/**
 * An object that represents the response of an HTTP request.
 */
public abstract class AbstractHttpResponse implements HttpResponse {
    /**
     * Request properties used to configure the request that generated this response.
     */
    private final HttpRequest request;

    /**
     * HTTP response status.
     */
    private final int status;

    /**
     * Response headers.
     */
    private final MultiValuedMap headers = new MultiValuedMap();

    /**
     * Converter manager.
     */
    private final EntityConverterManager converterManager;

    /**
     * Response entity.
     */
    private HttpEntity entity;

    /**
     * Constructor.
     *
     * @param converterManager Converter manager.
     * @param request          Request properties used to make the request.
     * @param status           HTTP response code.
     * @param headers          Headers contained in the response.
     * @param entity           Response entity (may be null).
     * @throws IOException When an IO exception occurs.
     */
    protected AbstractHttpResponse(EntityConverterManager converterManager, HttpRequest request, int status, MultiValuedMap headers, HttpEntity entity) throws IOException {
        this.request = request;
        this.converterManager = converterManager;
        this.status = status;
        this.headers.putAll(headers);
        this.entity = entity;

        if (entity != null && request.isBufferResponseEntity()) {
            entity.buffer();
        }
    }

    /**
     * Returns the status code of the response.
     *
     * @return The status code of the response.
     */

    public int getStatus() {
        return status;
    }

    /**
     * Returns the HTTP request.
     *
     * @return The HTTP request.
     */
    public HttpRequest getRequest() {
        return request;
    }

    /**
     * Returns the first value of the header with the given name, or null if it doesn't exist.
     *
     * @param name Name of the header.
     * @return The first value of the requested header, or null if it doesn't exist.
     */
    public String getHeader(String name) {
        if (getHeaders().containsKey(name) && getHeaders().get(name).size() > 0) {
            return getHeaders().get(name).get(0);
        }
        return null;
    }

    /**
     * Returns a list of values of the header with the given name, or null if the header doesn't exist.
     *
     * @param name Name of the header.
     * @return A list of values of the requested header, or null if it doesn't exist.
     */
    public List<String> getHeaders(String name) {
        return getHeaders().getOrDefault(name, null);
    }

    /**
     * Return all response headers.
     * <p>
     * This method returns a map where the key is the name of the header, and the value is a list of values
     * for the response header. This is true even when there is only one value for the header.
     *
     * @return A copy of the map containing the response headers.
     */
    public MultiValuedMap getHeaders() {
        return headers;
    }

    /**
     * Returns the entity.
     * <p>
     * If the entity was buffered, calling this method will always return a new InputStream.
     * Otherwise, the original InputStream will be returned. Note that if the entity is not
     * buffered and it was already read once, subsequent reading will likely fail.
     *
     * @return The response entity.
     */
    public HttpEntity getEntity() {
        return entity;
    }

    /**
     * Sets the HTTP entity of the response.
     *
     * @param entity HTTP entity.
     */
    public void setEntity(HttpEntity entity) {
        this.entity = entity;
    }

    /**
     * Returns the entity, converted to the given class type.
     *
     * @param type Class type to convert the entity to.
     * @param <T>  Generic type of the method call.
     * @return The converted entity.
     * @throws UnsupportedConversionException when no converter is found to convert the entity.
     * @throws IOException                    When an IO exception occurs.
     */
    public <T> T getEntity(Class<T> type) throws UnsupportedConversionException, IOException {
        if (entity == null) {
            return null;
        }

        T object = converterManager.read(type, entity);
        entity.close();
        return object;
    }

    /**
     * Closes the entity and releases any system resources associated
     * with it. If the response is already closed then invoking this
     * method has no effect.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        if (entity != null) {
            entity.close();
        }
    }

    /**
     * Returns whether the response contains an entity.
     *
     * @return Whether the response contains an entity.
     */
    public boolean hasEntity() {
        return entity != null;
    }
}
