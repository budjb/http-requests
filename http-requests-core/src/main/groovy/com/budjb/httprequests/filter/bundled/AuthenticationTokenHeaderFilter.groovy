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
package com.budjb.httprequests.filter.bundled

import com.budjb.httprequests.HttpContext
import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.HttpResponse
import com.budjb.httprequests.filter.HttpClientRequestFilter
import com.budjb.httprequests.filter.HttpClientRetryFilter

/**
 * An opinionated authentication filter that implements an authentication style where the client needs
 * to authenticate against some service which returns an authorization token and the client must send
 * that token with its request in a header.
 *
 * The authentication token and optionally its timeout are stored in the filter instance. The filter
 * will automatically re-authenticate when the existing token has either timed out (if a timeout is known)
 * or an <code>HTTP 401</code> is received from the first attempt at the request.
 */
abstract class AuthenticationTokenHeaderFilter implements HttpClientRetryFilter, HttpClientRequestFilter {
    /**
     * Authentication token.
     */
    String authenticationToken

    /**
     * When the authentication token will expire.
     */
    Date timeout

    /**
     * Perform the logic of authenticating.
     *
     * This method, upon successful authentication, should update the {@link #authenticationToken}
     * property, and optionally the {@link #timeout} property if applicable.
     */
    abstract void authenticate()

    /**
     * Retrieve the header name that should contain the authentication token.
     *
     * @return The header name that should contain the authentication token.
     */
    abstract String getAuthenticationTokenHeader()

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
    @Override
    boolean shouldRetry(HttpRequest request, HttpResponse response, int retries) {
        return retries == 0 && response.getStatus() == 401
    }

    /**
     * Called before the attempt to retry the HTTP request.
     *
     * This method is only called when {@link #shouldRetry(HttpRequest, HttpResponse, int)} returns true, even if a
     * retry is otherwise attempted.
     *
     * @param request Configuration of the request.
     * @param response Response properties.
     */
    @Override
    void onRetry(HttpRequest request, HttpResponse response) {
        authenticate()
        request.setHeader(getAuthenticationTokenHeader(), getAuthenticationToken())
    }

    /**
     * Provides an opportunity to modify the {@link HttpRequest} before it is transmitted.
     *
     * @param context HTTP request context.
     */
    @Override
    void filterHttpRequest(HttpContext context) {
        if (!getAuthenticationToken() || (getTimeout() && getTimeout() < new Date())) {
            authenticate()
            context.getRequest().setHeader(getAuthenticationTokenHeader(), getAuthenticationToken())
        }
    }
}
