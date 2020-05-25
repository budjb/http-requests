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
package com.budjb.httprequests.converter;

import com.budjb.httprequests.HttpEntity;

public interface EntityReader extends EntityConverter {
    /**
     * Convert the given entity.
     * <p>
     * If an error occurs, null may be returned so that another converter can attempt a conversion.
     *
     * @param clazz  Class of the object that should be returned by the converter.
     * @param entity The HTTP entity to convert from.
     * @param <T>    Type of the object that should be returned by the converter
     * @return The converted entity.
     * @throws Exception when an unexpected error occurs during conversion.
     */
    <T> T read(Class<? extends T> clazz, HttpEntity entity) throws Exception;
}
