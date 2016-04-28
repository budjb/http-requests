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
package com.budjb.httprequests.filter

import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.HttpResponse

/**
 * An {@link HttpClientFilter} that supports retrying HTTP requests.
 */
interface HttpClientRetryFilter extends HttpClientFilter {
    /**
     * Returns whether this filter requests a retry of the HTTP request.
     *
     * This method should not alter the request or response objects. They are only made available so that the filter
     * has all the information it needs to determine if a retry should be attempted.
     *
     * Note that this filter will receive a call to {@link #onRetry(HttpRequest, HttpResponse)} only if this method
     * returns true, even if a retry is otherwise attempted.
     *
     * @param request Configuration of the request.
     * @param response Response properties.
     * @param retries The number of retries that have occurred before this method is run.
     * @return <code>true</code> if it is determined that a retry of the request should be attempted.
     */
    boolean shouldRetry(HttpRequest request, HttpResponse response, int retries)

    /**
     * Called before the attempt to retry the HTTP request.
     *
     * This method is only called when {@link #shouldRetry(HttpRequest, HttpResponse, int)} returns true, even if a
     * retry is otherwise attempted.
     *
     * @param request Configuration of the request.
     * @param response Response properties.
     */
    void onRetry(HttpRequest request, HttpResponse response)
}