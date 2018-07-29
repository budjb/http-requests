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

/**
 * An enumeration of supported HTTP methods.
 */
public enum HttpMethod {
    /**
     * HTTP GET.
     */
    GET(false, true),

    /**
     * HTTP POST.
     */
    POST(true, true),

    /**
     * HTTP PUT.
     */
    PUT(true, true),

    /**
     * HTTP DELETE.
     */
    DELETE(true, true),

    /**
     * HTTP HEAD.
     */
    HEAD(true, false),

    /**
     * HTTP TRACE.
     */
    TRACE(false, true),

    /**
     * HTTP OPTIONS.
     */
    OPTIONS(true, true),

    /**
     * HTTP PATCH.
     */
    PATCH(true, true);

    /**
     * Whether the method supports a request entity.
     */
    private final boolean supportsRequestEntity;

    /**
     * Whether the method supports a response entity.
     */
    private final boolean supportsResponseEntity;

    /**
     * Constructor.
     *
     * @param supportsRequestEntity  Whether the method supports a request entity.
     * @param supportsResponseEntity Whether the method supports a response entity.
     */
    HttpMethod(boolean supportsRequestEntity, boolean supportsResponseEntity) {
        this.supportsRequestEntity = supportsRequestEntity;
        this.supportsResponseEntity = supportsResponseEntity;
    }

    /**
     * Returns whether the method supports a request entity.
     *
     * @return Whether the method supports a request entity.
     */
    public boolean isSupportsRequestEntity() {
        return supportsRequestEntity;
    }

    /**
     * Returns whether the method supports a response entity.
     *
     * @return Whether the method supports a response entity.
     */
    public boolean isSupportsResponseEntity() {
        return supportsResponseEntity;
    }
}