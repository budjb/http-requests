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

import com.budjb.httprequests.HttpRequest;
import com.budjb.httprequests.filter.HttpClientFilter;
import com.budjb.httprequests.filter.RequestFilter;

import java.util.Base64;

/**
 * An {@link HttpClientFilter} that provides Basic Authentication support.
 */
public class BasicAuthFilter implements RequestFilter {
    /**
     * Name of the header for basic authentication.
     */
    private static final String HEADER = "Authorization";

    /**
     * Contains the encoded credentials
     */
    private String credentials;

    /**
     * Constructor.
     *
     * @param username User name to authentication with.
     * @param password Password to authenticate with.
     */
    public BasicAuthFilter(String username, String password) {
        credentials = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void filter(HttpRequest request) {
        request.setHeader(HEADER, credentials);
    }

}
