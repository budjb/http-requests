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

package com.budjb.httprequests.test;

import com.budjb.httprequests.HttpMethod;
import com.budjb.httprequests.HttpRequest;
import com.budjb.httprequests.MultiValuedMap;
import com.budjb.httprequests.converter.EntityConverterManager;
import com.budjb.httprequests.exception.UnsupportedConversionException;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a mocked HTTP request and an associated HTTP response.
 */
public class RequestMock {
    /**
     * Response headers.
     */
    private final MultiValuedMap responseHeaders = new MultiValuedMap();

    /**
     * Entity converter manager.
     */
    private final EntityConverterManager entityConverterManager;

    /**
     * Request headers.
     */
    private MultiValuedMap requestHeaders;

    /**
     * Request query parameters.
     */
    private MultiValuedMap requestQueryParameters;

    /**
     * URI of the request.
     */
    private String requestUri;

    /**
     * Request HTTP method.
     */
    private HttpMethod requestMethod;

    /**
     * Response status code.
     */
    private int responseStatusCode = 200;

    /**
     * Response entity.
     */
    private InputStream responseEntity;

    /**
     * Whether this mock has been called.
     */
    private int called = 0;

    /**
     * Base constructor.
     *
     * @param entityConverterManager Entity converter manager.
     */
    RequestMock(EntityConverterManager entityConverterManager) {
        this.entityConverterManager = entityConverterManager;
    }

    /**
     * Constructor taking a URI.
     *
     * @param entityConverterManager Entity converter manager.
     * @param uri                    URI of the request.
     */
    RequestMock(EntityConverterManager entityConverterManager, URI uri) {
        this.entityConverterManager = entityConverterManager;
        parseUri(uri);
    }

    /**
     * Constructor taking a URI.
     *
     * @param entityConverterManager Entity converter manager.
     * @param uri                    URI of the request.
     * @throws URISyntaxException When a problem occurs while parsing a URI.
     */
    public RequestMock(EntityConverterManager entityConverterManager, String uri) throws URISyntaxException {
        this.entityConverterManager = entityConverterManager;
        parseUri(uri);
    }

    /**
     * Returns the response status code.
     *
     * @return The response status code.
     */
    public int getResponseStatusCode() {
        return responseStatusCode;
    }

    /**
     * Sets the response status code.
     *
     * @param responseStatusCode The response status code.
     */
    public RequestMock setResponseStatusCode(int responseStatusCode) {
        this.responseStatusCode = responseStatusCode;
        return this;
    }

    /**
     * Returns the response entity.
     *
     * @return The response entity.
     */
    public InputStream getResponseEntity() {
        return responseEntity;
    }

    /**
     * Sets the the response entity.
     *
     * @param responseEntity The response entity.
     */
    public RequestMock setResponseEntity(InputStream responseEntity) {
        this.responseEntity = responseEntity;
        return this;
    }

    /**
     * Converts and sets the response entity.
     *
     * @param responseEntity The response entity.
     * @throws UnsupportedConversionException When no converter is available to convert the body.
     */
    public RequestMock setResponseEntity(Object responseEntity) throws UnsupportedConversionException {
        this.responseEntity = entityConverterManager.write(responseEntity).getInputStream();
        return this;
    }

    /**
     * Returns the HTTP method.
     *
     * @return The HTTP method.
     */
    public HttpMethod getRequestMethod() {
        return requestMethod;
    }

    /**
     * Sets the HTTP method.
     *
     * @param requestMethod The HTTP method.
     */
    public RequestMock setRequestMethod(HttpMethod requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    /**
     * Returns the URI of the request.
     *
     * @return The URI of the request.
     */
    public String getRequestUri() {
        return requestUri;
    }

    /**
     * Sets the URI of the request.
     * <p>
     * Note that query parameters will reset to what is contained in the URI string.
     *
     * @param requestUri URI of the request.
     * @return The instance of this class the method was called with.
     * @throws URISyntaxException When a problem occurs while parsing a URI.
     */
    public RequestMock setRequestUri(String requestUri) throws URISyntaxException {
        parseUri(requestUri);
        return this;
    }

    /**
     * Sets the URI of the request.
     * <p>
     * Note that query parameters will reset to what is contained in the URI.
     *
     * @param uri URI of the request.
     * @return The instance of this class the method was called with.
     */
    public RequestMock setRequestUri(URI uri) {
        parseUri(uri);
        return this;
    }

    /**
     * Returns the request headers.
     *
     * @return The request headers.
     */
    public MultiValuedMap getRequestHeaders() {
        return requestHeaders;
    }

    /**
     * Sets the request headers to the given map of headers.
     *
     * @param requestHeaders Map of headers.
     * @return The instance of this class the method was called with.
     */
    public RequestMock setRequestHeaders(MultiValuedMap requestHeaders) {
        initRequestHeaders();
        this.requestHeaders.set(requestHeaders);
        return this;
    }

    /**
     * Add a single header to the request.
     *
     * @param name  Name of the header.
     * @param value Value of the header.
     * @return The instance of this class the method was called with.
     */
    public RequestMock addRequestHeader(String name, String value) {
        initRequestHeaders();
        requestHeaders.add(name, value);
        return this;
    }

    /**
     * Add several headers with the same name to the request.
     *
     * @param name   Name of the header.
     * @param values A {@code List} of values of the header.
     * @return The instance of this class the method was called with.
     */
    public RequestMock addRequestHeader(String name, List<String> values) {
        initRequestHeaders();
        requestHeaders.add(name, values);
        return this;
    }

    /**
     * Add many headers to the request.
     *
     * @param headers A map of headers, where the key is the name of the header and the value is either a
     *                {@code String} or a {@code List} of {@code String Strings}.
     * @return The instance of this class the method was called with.
     */
    public RequestMock addRequestHeaders(MultiValuedMap headers) {
        initRequestHeaders();
        this.requestHeaders.add(headers);
        return this;
    }

    /**
     * Overwrites the given header and sets it to the given value.
     *
     * @param name  Name of the header.
     * @param value Value of the header.
     * @return The instance of this class the method was called with.
     */
    public RequestMock setRequestHeader(String name, String value) {
        initRequestHeaders();
        requestHeaders.set(name, value);
        return this;
    }

    /**
     * Overwrites the given header and sets it to the given list of values.
     *
     * @param name   Name of the header.
     * @param values A <code>List</code> of values of the header.
     * @return The instance of this class the method was called with.
     */
    public RequestMock setRequestHeader(String name, List<String> values) {
        initRequestHeaders();
        requestHeaders.set(name, values);
        return this;
    }

    /**
     * Returns the query parameters of the request.
     *
     * @return A copy of the map containing the query parameters.
     */
    public MultiValuedMap getRequestQueryParameters() {
        return requestQueryParameters;
    }

    /**
     * Sets the request query parameters to the given map of query parameters.
     *
     * @param requestQueryParameters Map of headers.
     * @return The instance of this class the method was called with.
     */
    public RequestMock setRequestQueryParameters(MultiValuedMap requestQueryParameters) {
        initRequestQueryParameters();
        this.requestQueryParameters.set(requestQueryParameters);
        return this;
    }

    /**
     * Add a single query parameter to the request.
     *
     * @param name  Name of the query parameter.
     * @param value Value of the query parameter.
     * @return The instance of this class the method was called with.
     */
    public RequestMock addRequestQueryParameter(String name, String value) {
        initRequestQueryParameters();
        requestQueryParameters.add(name, value);
        return this;
    }

    /**
     * Add several query parameters with the same name to the request.
     *
     * @param name   Name of the query parameter.
     * @param values A <code>List</code> of values of the query parameter.
     * @return The instance of this class the method was called with.
     */
    public RequestMock addRequestQueryParameter(String name, List<String> values) {
        initRequestQueryParameters();
        requestQueryParameters.add(name, values);
        return this;
    }

    /**
     * Add many query parameters to the request.
     *
     * @param parameters A map of query parameters, where the key is the name of the query parameter and the value is
     *                   either a <code>String</code> or a <code>List</code> of <code>String</code>s.
     * @return The instance of this class the method was called with.
     */
    public RequestMock addRequestQueryParameters(MultiValuedMap parameters) {
        initRequestQueryParameters();
        this.requestQueryParameters.add(parameters);
        return this;
    }

    /**
     * Sets the query parameter with the given name.
     * <p>
     * Note that this will overwrite any existing query parameter value(s).
     *
     * @param name  Name of the query parameter.
     * @param value Value of the query parameter.
     * @return The instance of this class the method was called with.
     */
    public RequestMock setRequestQueryParameter(String name, String value) {
        initRequestQueryParameters();
        requestQueryParameters.set(name, value);
        return this;
    }

    /**
     * Sets the query parameter with the given name.
     * <p>
     * Note that this will overwrite any existing query parameter value(s).
     *
     * @param name   Name of the query parameter.
     * @param values A <code>List</code> of values of the query parameter.
     * @return The instance of this class the method was called with.
     */
    public RequestMock setRequestQueryParameter(String name, List<String> values) {
        initRequestQueryParameters();
        requestQueryParameters.set(name, values);
        return this;
    }

    /**
     * Returns the response headers.
     *
     * @return The response headers.
     */
    public MultiValuedMap getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * Sets the response headers to the given map of headers.
     *
     * @param responseHeaders Map of headers.
     * @return The instance of this class the method was called with.
     */
    public RequestMock setResponseHeaders(MultiValuedMap responseHeaders) {
        this.responseHeaders.set(responseHeaders);
        return this;
    }

    /**
     * Add a single header to the response.
     *
     * @param name  Name of the header.
     * @param value Value of the header.
     * @return The instance of this class the method was called with.
     */
    public RequestMock addResponseHeader(String name, String value) {
        responseHeaders.add(name, value);
        return this;
    }

    /**
     * Add several headers with the same name to the response.
     *
     * @param name   Name of the header.
     * @param values A {@code List} of values of the header.
     * @return The instance of this class the method was called with.
     */
    public RequestMock addResponseHeader(String name, List<String> values) {
        responseHeaders.add(name, values);
        return this;
    }

    /**
     * Add many headers to the response.
     *
     * @param headers A map of headers, where the key is the name of the header and the value is either a
     *                {@code String} or a {@code List} of {@code String Strings}.
     * @return The instance of this class the method was called with.
     */
    public RequestMock addResponseHeaders(MultiValuedMap headers) {
        this.responseHeaders.add(headers);
        return this;
    }

    /**
     * Overwrites the given header and sets it to the given value.
     *
     * @param name  Name of the header.
     * @param value Value of the header.
     * @return The instance of this class the method was called with.
     */
    public RequestMock setResponseHeader(String name, String value) {
        responseHeaders.set(name, value);
        return this;
    }

    /**
     * Overwrites the given header and sets it to the given list of values.
     *
     * @param name   Name of the header.
     * @param values A <code>List</code> of values of the header.
     * @return The instance of this class the method was called with.
     */
    public RequestMock setResponseHeader(String name, List<String> values) {
        responseHeaders.set(name, values);
        return this;
    }

    /**
     * Increments the call counter.
     */
    void incrementCalled() {
        called++;
    }

    /**
     * Returns whether the mock was called.
     *
     * @return Whether the mock was called.
     */
    public boolean called() {
        return called > 0;
    }

    /**
     * Returns the number of times this mock was called.
     *
     * @return The number of times this mock was called.
     */
    public int getCalledCount() {
        return called;
    }

    /**
     * Parses the given URI and applies its properties to the HTTP request properties.
     *
     * @param uri URI as a <code>String</code>.
     */
    private void parseUri(String uri) throws URISyntaxException {
        parseUri(new URI(uri));
    }

    /**
     * Parses the given URI and applies its properties to the HTTP request properties.
     *
     * @param uri URI object to parse.
     */
    private void parseUri(URI uri) {
        String scheme = uri.getScheme();
        String host = uri.getHost();
        int port = uri.getPort();
        String path = uri.getRawPath();
        String query = uri.getQuery();

        StringBuilder builder = new StringBuilder(scheme).append("://").append(host);

        if (port != -1 && !(scheme.equalsIgnoreCase("https") && port == 443) && !(scheme.equalsIgnoreCase("http") && port == 80)) {
            builder.append(':').append(port);
        }

        builder.append(path);

        this.requestUri = builder.toString();

        if (query != null && query.length() > 0) {
            Arrays.stream(query.split("&")).forEach(it -> {
                List<String> parts = Arrays.stream(it.split("=")).collect(Collectors.toList());

                if (parts.size() == 1) {
                    addRequestQueryParameter(parts.get(0), "");
                }
                else {
                    String name = parts.remove(0);
                    addRequestQueryParameter(name, String.join("=", parts));
                }
            });
        }
    }

    private void initRequestHeaders() {
        if (requestHeaders == null) {
            requestHeaders = new MultiValuedMap();
        }
    }

    private void initRequestQueryParameters() {
        if (requestQueryParameters == null) {
            requestQueryParameters = new MultiValuedMap();
        }
    }

    /**
     * Returns whether the mock matches the given request.
     * <p>
     * A match will occur with the URI's match and any of the set request properties
     * of the mock match. This means that properties were not set on the request
     * (such as headers or an HTTP method, for example), they will not be used to
     * determine a match.
     *
     * @param request Request to match.
     * @param method  HTTP method to match.
     * @return Whether the mock matches.
     */
    public boolean matches(HttpRequest request, HttpMethod method) {
        if (!requestUri.equalsIgnoreCase(request.getUri())) {
            return false;
        }

        if (this.requestMethod != null && !this.requestMethod.equals(method)) {
            return false;
        }

        if (requestHeaders != null && !requestHeaders.equals(request.getHeaders())) {
            return false;
        }

        if (requestQueryParameters != null && !requestQueryParameters.equals(request.getQueryParameters())) {
            return false;
        }

        return true;
    }
}
