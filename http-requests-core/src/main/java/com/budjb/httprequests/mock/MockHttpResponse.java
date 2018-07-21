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
package com.budjb.httprequests.mock;

import com.budjb.httprequests.HttpEntity;
import com.budjb.httprequests.HttpRequest;
import com.budjb.httprequests.HttpResponse;
import com.budjb.httprequests.MultiValuedMap;
import com.budjb.httprequests.converter.EntityConverterManager;

import java.io.IOException;

/**
 * A standalone {@link HttpResponse} implementation where its properties are injected rather
 * than read from an HTTP client's response. This is useful for testing scenarios where calls
 * to an HTTP client should not necessarily be transmit across the wire.
 */
public class MockHttpResponse extends HttpResponse {
    /**
     * Constructor.
     *
     * @param converterManager Converter manager.
     * @param request          Request properties used to make the request.
     * @param status           HTTP status of the response.
     * @param headers          Response headers.
     * @param entity           Entity of the response.
     * @throws IOException When an IO exception occurs.
     */
    public MockHttpResponse(EntityConverterManager converterManager, HttpRequest request, int status, MultiValuedMap headers, HttpEntity entity) throws IOException {
        super(converterManager, request, status, headers, entity);
    }
}
