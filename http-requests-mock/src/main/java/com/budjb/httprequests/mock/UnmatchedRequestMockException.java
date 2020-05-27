/*
 * Copyright 2016-2020 the original author or authors.
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

import com.budjb.httprequests.HttpMethod;
import com.budjb.httprequests.HttpRequest;
import com.budjb.httprequests.exception.HttpClientException;

public class UnmatchedRequestMockException extends HttpClientException {
    /**
     * The unmatched HTTP request.
     */
    private final HttpRequest request;

    /**
     * Constructor.
     *
     * @param request The HTTP request that did not have a matching mock.
     * @param method  The HTTP method of the request.
     */
    public UnmatchedRequestMockException(HttpRequest request, HttpMethod method) {
        super("No HTTP request mock found matching URI " + method.toString() + " " + request.getUri());
        this.request = request;
    }

    /**
     * Returns the HTTP request that did not have a matching mock.
     *
     * @return The unmatched HTTP request.
     */
    public HttpRequest getRequest() {
        return request;
    }
}
