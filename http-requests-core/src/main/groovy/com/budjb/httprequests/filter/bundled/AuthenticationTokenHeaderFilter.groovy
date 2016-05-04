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
     * Called once the request has been completed. This method should return <code>true</code> if the request
     * should be retried. Changes to the {@link HttpRequest} and {@link HttpResponse} objects will be lost, since
     * every request attempt receives a fresh copy of the request and the previous response is lost. If the retry
     * filter need to modify the subsequent request before it is sent out, classes that implement this filter should
     * also implement the {@link HttpClientRequestFilter} interface.
     *
     * @param context HTTP request context.
     */
    @Override
    boolean onRetry(HttpContext context) {
        if (context.getRetries() == 0 && context.getResponse().getStatus() == 401) {
            authenticate()
            return true
        }
        return false
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
        }
        context.getRequest().setHeader(getAuthenticationTokenHeader(), getAuthenticationToken())
    }
}
