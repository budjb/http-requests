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

package com.budjb.httprequests.groovy;

import com.budjb.httprequests.HttpRequest;
import com.budjb.httprequests.MultiValuedMap;
import com.budjb.httprequests.filter.HttpClientFilter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * A closure delegate that helps build an {@link HttpRequest} object.
 */
public class HttpRequestDelegate {
    /**
     * HTTP request instance to configure.
     */
    private final HttpRequest httpRequest;

    /**
     * Constructor.
     *
     * @param request HTTP request instance to configure.
     */
    public HttpRequestDelegate(HttpRequest request) {
        this.httpRequest = request;
    }

    /**
     * Sets the URI of the request.
     *
     * @param uri The URI of the request.
     * @throws URISyntaxException When an error parsing the URI occurs.
     */
    public void uri(String uri) throws URISyntaxException {
        httpRequest.setUri(uri);
    }

    /**
     * Sets the URI of the request.
     *
     * @param uri The URI of the request.
     */
    public void uri(URI uri) {
        httpRequest.setUri(uri);
    }

    /**
     * Sets the read timeout of the request.
     *
     * @param timeout The read timeout of the request.
     */
    public void readTimeout(int timeout) {
        httpRequest.setReadTimeout(timeout);
    }

    /**
     * Sets the connection timeout of the request.
     *
     * @param timeout The connection timeout of the request.
     */
    public void connectionTimeout(int timeout) {
        httpRequest.setConnectionTimeout(timeout);
    }

    /**
     * Sets whether SSL is validated.
     *
     * @param validated Whether SSL is validated.
     */
    public void sslValidated(boolean validated) {
        httpRequest.setSslValidated(validated);
    }

    /**
     * Sets whether redirects are followed.
     *
     * @param follow Whether redirects are followed.
     */
    public void followRedirects(boolean follow) {
        httpRequest.setFollowRedirects(follow);
    }

    /**
     * Sets whether the response entity should be buffered.
     *
     * @param buffer Whether the response entity should be buffered.
     */
    public void bufferResponseEntity(boolean buffer) {
        httpRequest.setBufferResponseEntity(buffer);
    }

    /**
     * Adds an {@link HttpClientFilter} to the request.
     *
     * @param filter Filter to add to the request.
     */
    public void filter(HttpClientFilter filter) {
        httpRequest.addFilter(filter);
    }

    /**
     * Adds a header to the request.
     *
     * @param name  Name of the header.
     * @param value Value of the header.
     */
    public void header(String name, String value) {
        httpRequest.addHeader(name, value);
    }

    /**
     * Adds a head to the request.
     *
     * @param name   Name of the header.
     * @param values Values of the header.
     */
    public void header(String name, List<String> values) {
        httpRequest.addHeader(name, values);
    }

    /**
     * Adds a set of headers to the request.
     *
     * @param headers A map of headers, with the key being the name of the header and the value being the
     *                list of values for the header.
     */
    public void headers(Map<String, List<String>> headers) {
        httpRequest.addHeaders(new MultiValuedMap(headers));
    }

    /**
     * Adds a query parameter to the request.
     *
     * @param name  Name of the query parameter.
     * @param value Value of the query parameter.
     */
    public void queryParameter(String name, String value) {
        httpRequest.addQueryParameter(name, value);
    }

    /**
     * Adds a query parameter to the request.
     *
     * @param name   Name of the query parameter.
     * @param values Values of the query parameter.
     */
    public void queryParameter(String name, List<String> values) {
        httpRequest.addQueryParameter(name, values);
    }

    /**
     * Adds a set of query parameters to the request.
     *
     * @param parameters A map of query parameters, with the key being the name of the query parameter
     *                   and the value being the list of values for the query parameter.
     */
    public void queryParameter(Map<String, List<String>> parameters) {
        httpRequest.addQueryParameters(new MultiValuedMap(parameters));
    }

}
