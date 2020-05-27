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

import com.budjb.httprequests.*;
import com.budjb.httprequests.converter.EntityConverterManager;
import com.budjb.httprequests.filter.HttpClientFilterProcessor;

import java.io.IOException;

/**
 * An implementation of {@link HttpClient} that uses the Jersey Client 1.x library.
 */
public class MockHttpClient extends AbstractHttpClient {
    /**
     * Mock HTTP client factory.
     */
    private final MockHttpClientFactory httpClientFactory;

    /**
     * Constructor.
     *
     * @param converterManager Entity converter manager.
     */
    MockHttpClient(MockHttpClientFactory httpClientFactory, EntityConverterManager converterManager) {
        super(converterManager);
        this.httpClientFactory = httpClientFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected HttpResponse execute(HttpContext context, HttpEntity entity, HttpClientFilterProcessor filterProcessor) throws IOException {
        HttpRequest request = context.getRequest();
        HttpMethod method = context.getMethod();

        RequestMock mock = httpClientFactory.findMatchingMock(context.getRequest(), context.getMethod());

        if (mock == null) {
            throw new UnmatchedRequestMockException(request, method);
        }

        mock.incrementCalled();

        return new MockHttpResponse(getConverterManager(), context, request, mock);
    }
}
