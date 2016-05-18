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

/**
 * A standalone {@link HttpResponse} implementation where its properties are injected rather
 * than read from an HTTP client's response.
 */
class MockHttpResponse extends HttpResponse {
    /**
     * Constructor.
     *
     * @param request Request properties used to make the request.
     * @param converterManager Converter manager.
     * @param status HTTP status of the response.
     * @param headers Response headers.
     * @param contentType Content-Type of the response.
     * @param entity Entity of the response.
     */
    MockHttpResponse(HttpRequest request, EntityConverterManager converterManager, int status, Map<String, Object> headers, String contentType, InputStream entity) {
        super(request, converterManager)

        setStatus(status)
        setHeaders(headers)
        setContentType(contentType)
        setEntity(entity)
    }
}
