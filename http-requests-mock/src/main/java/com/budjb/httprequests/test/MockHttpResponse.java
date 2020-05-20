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
package com.budjb.httprequests.test;

import com.budjb.httprequests.HttpEntity;
import com.budjb.httprequests.HttpRequest;
import com.budjb.httprequests.HttpResponse;
import com.budjb.httprequests.MultiValuedMap;
import com.budjb.httprequests.converter.EntityConverterManager;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An {@link HttpResponse} implementation that wraps a {@link RequestMock}.
 */
class MockHttpResponse extends HttpResponse {
    /**
     * Pattern to parse content type.
     */
    private final static Pattern CONTENT_TYPE_PATTERN = Pattern.compile("^([^;]+).*$");

    /**
     * Pattern to parse character set.
     */
    private final static Pattern CHARACTER_SET_PATTERN = Pattern.compile("charset\\s*=\\s*([^;]+)");

    /**
     * Jersey Client response.
     */
    private final RequestMock mock;

    /**
     * Constructor.
     *
     * @param request          Request properties used to make the request.
     * @param converterManager Converter manager.
     * @param mock             HTTP request mock.
     */
    MockHttpResponse(HttpRequest request, EntityConverterManager converterManager, RequestMock mock) throws IOException {
        super(converterManager, request, mock.getResponseStatusCode(), new MultiValuedMap(mock.getResponseHeaders()), parseEntity(mock));

        this.mock = mock;

        if (!hasEntity()) {
            close();
        }
    }

    /**
     * Parses the entity from the response.
     *
     * @param mock Mocked request.
     * @return The parsed HTTP entity, or {@code null} if none is available.
     * @throws IOException When an IO exception occurs.
     */
    private static HttpEntity parseEntity(RequestMock mock) throws IOException {
        if (mock.getResponseEntity() == null) {
            return null;
        }

        String contentType = mock.getResponseHeaders().getFlat("Content-Type");

        if (contentType != null) {
            Matcher contentMatcher = CONTENT_TYPE_PATTERN.matcher(contentType);

            if (contentMatcher.find()) {
                Matcher charsetMatcher = CHARACTER_SET_PATTERN.matcher(contentType);

                if (charsetMatcher.find()) {
                    return new HttpEntity(mock.getResponseEntity(), contentMatcher.group(1), charsetMatcher.group(1));
                }
                else {
                    return new HttpEntity(mock.getResponseEntity(), contentMatcher.group(1));
                }
            }
        }

        return new HttpEntity(mock.getResponseEntity());
    }

    /**
     * Returns the mock that matched the request.
     *
     * @return The mock that matched the request.
     */
    public RequestMock getMock() {
        return mock;
    }
}
