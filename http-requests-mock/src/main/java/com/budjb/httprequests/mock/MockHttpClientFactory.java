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

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * An {@link HttpClientFactory} implementation that creates Jersey 1.x HTTP clients.
 */
public class MockHttpClientFactory extends AbstractHttpClientFactory {
    /**
     * List of mocked requests.
     */
    private final List<RequestMock> mocks = new LinkedList<>();

    /**
     * Constructor that can optionally register the default set of entity converters.
     *
     * @param converterManager Entity converter manager.
     */
    public MockHttpClientFactory(EntityConverterManager converterManager) {
        super(Objects.requireNonNull(converterManager, "EntityConverterManager must not be null"));
    }

    /**
     * Create a new Jersey HTTP client.
     *
     * @return A new Jersey 1.x {@link HttpClient} implementation.
     */
    @Override
    public HttpClient createHttpClient() {
        return new MockHttpClient(this, getConverterManager());
    }

    /**
     * Returns all mocks registered with the factory.
     *
     * @return All mocks registered with the factory.
     */
    public List<RequestMock> getMocks() {
        return mocks;
    }

    /**
     * Returns whether all mocks were called.
     *
     * @return Whether all mocks were called.
     */
    public boolean allMocksCalled() {
        return mocks.stream().allMatch(RequestMock::called);
    }

    /**
     * Attempts to find a mocked request the matches the given HTTP request and HTTP method.
     * If one is not found, {@code null} is returned.
     *
     * @param request HTTP request to match.
     * @param method  HTTP method to match.
     * @return The matching mock, or null.
     */
    RequestMock findMatchingMock(HttpRequest request, HttpMethod method) {
        return mocks.stream().filter(mock -> mock.matches(request, method)).findFirst().orElse(null);
    }

    /**
     * Creates a new, empty mock.
     *
     * @return A new, empty mock.
     */
    public RequestMock createMock() {
        RequestMock mock = new RequestMock(getConverterManager());
        mocks.add(mock);
        return mock;
    }
}
