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

public interface EntityWriter extends EntityConverter {
    /**
     * Convert the given object and return it as an {@link HttpEntity}.
     * <p>
     * If an expected error occurs, null may be returned so that another converter may
     * attempt conversion.
     *
     * @param entity       Entity object to convert into a byte array.
     * @param contentType  Content-Type of the object (may be {@code null}).
     * @param characterSet The character set of the request (may be {@code null}).
     * @return An {@link HttpEntity} on successful conversion, or {@code null}.
     * @throws Exception when an unexpected conversion error occurs.
     */
    HttpEntity write(Object entity, String contentType, String characterSet) throws Exception;
}
