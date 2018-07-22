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

import java.util.Objects;

/**
 * Contains an HTTP entity, and optionally its content type and character set.
 */
public class ConvertingHttpEntity {
    /**
     * Input stream containing the entity.
     */
    private final Object object;

    /**
     * Content type of the entity. May be {@code null}.
     */
    private final String contentType;

    /**
     * Character set of the entity. May be {@code null}.
     */
    private final String charSet;

    /**
     * Constructor.
     *
     * @param object      Object that will be converted.
     * @param contentType Content type of the entity.
     * @param charSet     Character set of the entity.
     */
    public ConvertingHttpEntity(Object object, String contentType, String charSet) {
        this.object = Objects.requireNonNull(object);
        this.contentType = contentType;
        this.charSet = charSet;
    }

    /**
     * Constructor.
     *
     * @param object Object that will be converted.
     */
    public ConvertingHttpEntity(Object object) {
        this(object, null);
    }

    /**
     * Constructor.
     *
     * @param object      Object that will be converted.
     * @param contentType Content type of the entity.
     */
    public ConvertingHttpEntity(Object object, String contentType) {
        this(object, contentType, null);
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
     * Returns the content type of the entity.
     *
     * @return The content type of the entity.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Returns the object that will be converted.
     *
     * @return The object that will be converted.
     */
    public Object getObject() {
        return object;
    }
}
