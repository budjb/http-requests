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
package com.budjb.httprequests.filter;

import com.budjb.httprequests.HttpContext;
import com.budjb.httprequests.HttpRequest;
import com.budjb.httprequests.HttpResponse;

/**
 * An {@link HttpClientFilter} that supports retrying HTTP requests.
 */
public interface RetryFilter extends HttpClientFilter {
    /**
     * Called once the request has been completed. This method should return {@code true} if the request
     * should be retried. Changes to the {@link HttpRequest} and {@link HttpResponse} objects will be lost, since
     * every request attempt receives a fresh copy of the request and the previous response is lost. If the retry
     * filter needs to modify the subsequent request before it is sent out, classes that implement this filter should
     * also implement the {@link RequestFilter} interface.
     *
     * @param context HTTP context.
     * @return Whether the request should be retried.
     */
    boolean isRetryRequired(HttpContext context);
}