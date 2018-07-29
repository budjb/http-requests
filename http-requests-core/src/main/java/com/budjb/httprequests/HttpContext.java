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

import java.util.HashMap;
import java.util.Map;

/**
 * A context for HTTP requests that can be used by filters.
 */
public class HttpContext {
    /**
     * A map containing data relevant to the request.
     */
    private final Map<String, Object> data = new HashMap<>();

    /**
     * The request configuration
     */
    private HttpRequest request;

    /**
     * The request entity.
     */
    private HttpEntity requestEntity;

    /**
     * Method of the request.
     */
    private HttpMethod method;

    /**
     * The HTTP response.
     */
    private HttpResponse response;

    /**
     * The number of retries that have been attempted.
     */
    private int retries = 0;

    /**
     * Returns the request entity.
     *
     * @return The request entity, or {@code null} if there is not one.
     */
    public HttpEntity getRequestEntity() {
        return requestEntity;
    }

    /**
     * Sets the request entity.
     *
     * @param requestEntity The request entity.
     */
    public void setRequestEntity(HttpEntity requestEntity) {
        this.requestEntity = requestEntity;
    }

    /**
     * Returns the request configuration.
     *
     * @return The request configuration.
     */
    public HttpRequest getRequest() {
        return request;
    }

    /**
     * Sets the request configuration.
     *
     * @param request The request configuration.
     */
    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    /**
     * Returns the method of the request.
     *
     * @return The method of the request.
     */
    public HttpMethod getMethod() {
        return method;
    }

    /**
     * Sets the method of the request.
     *
     * @param method The method of the request.
     */
    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    /**
     * Returns the HTTP response.
     *
     * @return The HTTP response.
     */
    public HttpResponse getResponse() {
        return response;
    }

    /**
     * Sets the HTTP response.
     *
     * @param response The HTTP response.
     */
    public void setResponse(HttpResponse response) {
        this.response = response;
    }

    /**
     * Returns the map containing data relevant to the request.
     *
     * @return The map containing data relevant to the request.
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * Retrieve an object with the given name from the context.
     *
     * @param name Name of the object.
     * @return The requested object, or {@code null} if not found.
     */
    public Object get(String name) {
        return data.getOrDefault(name, null);
    }

    /**
     * Retrieve an object with the given name and class type from the context.
     *
     * @param name Name of the object.
     * @param type Type of the object.
     * @param <T>  Generic type of the method call.
     * @return The requested object if found and matches the given type, or <code>null</code> otherwise.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String name, Class<T> type) {
        Object object = get(name);

        if (object != null && type.isAssignableFrom(object.getClass())) {
            return (T) object;
        }

        return null;
    }

    /**
     * Sets an object with the given name in the context.
     *
     * @param name   Name of the object.
     * @param object Object to store.
     */
    public void set(String name, Object object) {
        data.put(name, object);
    }

    /**
     * Increases the number of attempted retries.
     */
    public void incrementRetries() {
        retries++;
    }

    /**
     * Returns the number of retries that have been attempted.
     *
     * @return The number of retries that have been attempted.
     */
    public int getRetries() {
        return retries;
    }

    /**
     * Sets the number of retries that have been attempted.
     *
     * @param retries The number of retries that have been attempted.
     */
    public void setRetries(int retries) {
        this.retries = retries;
    }
}
