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

import com.budjb.httprequests.exception.UnsupportedConversionException;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * An object that represents the response of an HTTP request.
 */
public interface HttpResponse extends Closeable {
    /**
     * Returns the status code of the response.
     *
     * @return The status code of the response.
     */
    int getStatus();

    /**
     * Returns the HTTP request.
     *
     * @return The HTTP request.
     */
    HttpRequest getRequest();

    /**
     * Returns the first value of the header with the given name, or null if it doesn't exist.
     *
     * @param name Name of the header.
     * @return The first value of the requested header, or null if it doesn't exist.
     */
    String getHeader(String name);

    /**
     * Returns a list of values of the header with the given name, or null if the header doesn't exist.
     *
     * @param name Name of the header.
     * @return A list of values of the requested header, or null if it doesn't exist.
     */
    List<String> getHeaders(String name);

    /**
     * Return all response headers.
     * <p>
     * This method returns a map where the key is the name of the header, and the value is a list of values
     * for the response header. This is true even when there is only one value for the header.
     *
     * @return A copy of the map containing the response headers.
     */
    MultiValuedMap getHeaders();

    /**
     * Returns the entity.
     * <p>
     * If the entity was buffered, calling this method will always return a new InputStream.
     * Otherwise, the original InputStream will be returned. Note that if the entity is not
     * buffered and it was already read once, subsequent reading will likely fail.
     *
     * @return The response entity.
     */
    HttpEntity getEntity();

    /**
     * Sets the HTTP entity of the response.
     *
     * @param entity HTTP entity.
     */
    void setEntity(HttpEntity entity);

    /**
     * Returns the entity, converted to the given class type.
     *
     * @param type Class type to convert the entity to.
     * @param <T>  Generic type of the method call.
     * @return The converted entity.
     * @throws UnsupportedConversionException when no converter is found to convert the entity.
     * @throws IOException                    When an IO exception occurs.
     */
    <T> T getEntity(Class<T> type) throws UnsupportedConversionException, IOException;

    /**
     * Returns whether the response contains an entity.
     *
     * @return Whether the response contains an entity.
     */
    boolean hasEntity();
}
