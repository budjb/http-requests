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
package com.budjb.httprequests.filter.bundled;

import com.budjb.httprequests.HttpContext;
import com.budjb.httprequests.HttpRequest;
import com.budjb.httprequests.filter.RequestFilter;
import com.budjb.httprequests.filter.RetryFilter;

import java.util.Date;

/**
 * An opinionated authentication filter that implements an authentication style where the client needs
 * to authenticate against some service which returns an authorization token and the client must send
 * that token with its request in a header.
 * <p>
 * The authentication token and optionally its timeout are stored in the filter instance. The filter
 * will automatically re-authenticate when the existing token has either timed out (if a timeout is known)
 * or first attempt at the request has failed authentication.
 */
public abstract class AuthenticationTokenHeaderFilter implements RequestFilter, RetryFilter {
    /**
     * Authentication token.
     */
    private String authenticationToken;
    /**
     * When the authentication token will expire.
     */
    private Date timeout;

    /**
     * Returns the authentication token.
     *
     * @return The authentication token.
     */
    protected String getAuthenticationToken() {
        return authenticationToken;
    }

    /**
     * Sets the authentication token.
     *
     * @param authenticationToken The authentication token.
     */
    protected void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    /**
     * Returns when the authentication token will expire.
     *
     * @return When the authentication token will expire.
     */
    protected Date getTimeout() {
        return timeout;
    }

    /**
     * Sets the authentication token timeout.
     *
     * @param timeout The authentication token timeout.
     */
    protected void setTimeout(Date timeout) {
        this.timeout = timeout;
    }

    /**
     * Perform the logic of authenticating.
     * <p>
     * This method, upon successful authentication, should update the {@link #authenticationToken}
     * property, and optionally the {@link #timeout} property if applicable.
     */
    protected abstract void authenticate();

    /**
     * Retrieve the header name that should contain the authentication token.
     *
     * @return The header name that should contain the authentication token.
     */
    protected abstract String getAuthenticationTokenHeader();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRetryRequired(HttpContext context) {
        if (context.getRetries() == 0 && hasAuthenticationFailed(context)) {
            reset();
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void filter(HttpRequest request) {
        if (getAuthenticationToken() == null || (getTimeout() != null && getTimeout().compareTo(new Date()) < 0)) {
            authenticate();
        }
        request.setHeader(getAuthenticationTokenHeader(), getAuthenticationToken());
    }

    /**
     * Determines whether the request has failed authentication.
     *
     * @param context HTTP request context.
     * @return Whether the request has failed authentication.
     */
    protected boolean hasAuthenticationFailed(HttpContext context) {
        return context.getResponse().getStatus() == 401;
    }

    /**
     * Resets the authentication filter by removing the authentication token and timeout properties.
     */
    protected void reset() {
        setAuthenticationToken(null);
        setTimeout(null);
    }
}
