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
package com.budjb.httprequests;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An object used to configure an HTTP request.
 */
public class HttpRequest implements Cloneable {
    /**
     * Request headers.
     */
    private final MultiValuedMap headers = new MultiValuedMap();

    /**
     * Query parameters.
     */
    private final MultiValuedMap queryParameters = new MultiValuedMap();

    /**
     * URI of the request.
     */
    private String uri;

    /**
     * Requested content type of the response.
     */
    private String accept;

    /**
     * The read timeout of the HTTP connection, in milliseconds. Defaults to 0 (infinity).
     */
    private int readTimeout = 0;

    /**
     * The connection timeout of the HTTP connection, in milliseconds. Defaults to 0 (infinity).
     */
    private int connectionTimeout = 0;

    /**
     * Whether SSL certificates will be validated.
     */
    private boolean sslValidated = true;

    /**
     * Whether the client should automatically follow redirects.
     */
    private boolean followRedirects = true;

    /**
     * Whether to buffer the response entity in the {@link HttpResponse} object so that it can be
     * ready multiple times.
     */
    private boolean bufferResponseEntity = true;

    /**
     * Base constructor.
     */
    public HttpRequest() {

    }

    /**
     * Constructor that builds the request from a URI.
     *
     * @param uri URI of the request.
     */
    public HttpRequest(URI uri) {
        parseUri(uri);
    }

    /**
     * Constructor that builds the request from a URI string.
     *
     * @param uri URI of the request.
     * @throws URISyntaxException When a problem occurs while parsing a URI.
     */
    public HttpRequest(String uri) throws URISyntaxException {
        parseUri(uri);
    }

    /**
     * Returns the URI of the request.
     *
     * @return The URI of the request.
     */
    public String getUri() {
        return uri;
    }

    /**
     * Sets the URI of the request.
     * <p>
     * Note that query parameters will reset to what is contained in the URI string.
     *
     * @param uri URI of the request.
     * @return The instance of this class the method was called with.
     * @throws URISyntaxException When a problem occurs while parsing a URI.
     */
    public HttpRequest setUri(String uri) throws URISyntaxException {
        parseUri(uri);
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
    public HttpRequest setUri(URI uri) {
        parseUri(uri);
        return this;
    }

    /**
     * Returns the requested Content-Type of the response.
     *
     * @return The requested Content-Type of the response.
     */
    public String getAccept() {
        return accept;
    }

    /**
     * Sets the requested Content-Type of the response.
     *
     * @param accept Requested Content-Type of the response.
     * @return The instance of this class the method was called with.
     */
    public HttpRequest setAccept(String accept) {
        this.accept = accept;
        return this;
    }

    /**
     * Returns the read timeout, in milliseconds.
     *
     * @return The read timeout, in milliseconds.
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * Sets the read timeout, in milliseconds. 0 means infinity.
     *
     * @param timeout Read timeout of the request, in milliseconds.
     * @return The instance of this class the method was called with.
     */
    public HttpRequest setReadTimeout(int timeout) {
        this.readTimeout = timeout;
        return this;
    }

    /**
     * Returns the connection timeout, in milliseconds.
     *
     * @return The connection timeout, in milliseconds.
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Sets the connection timeout, in milliseconds. 0 means infinity.
     *
     * @param timeout Connection timeout of the request, in milliseconds.
     * @return The instance of this class the method was called with.
     */
    public HttpRequest setConnectionTimeout(int timeout) {
        this.connectionTimeout = timeout;
        return this;
    }

    /**
     * Returns whether SSL will be validated.
     *
     * @return Whether SSL will be validated.
     */
    public boolean isSslValidated() {
        return sslValidated;
    }

    /**
     * Sets whether SSL will be validated.
     *
     * @param sslValidated Whether SSL will be validated.
     * @return The instance of this class the method was called with.
     */
    public HttpRequest setSslValidated(boolean sslValidated) {
        this.sslValidated = sslValidated;
        return this;
    }

    /**
     * Returns whether the client should follow redirects.
     *
     * @return Whether the client should follow redirects.
     */
    public boolean isFollowRedirects() {
        return followRedirects;
    }

    /**
     * Sets whether the client should follow redirects.
     *
     * @param followRedirects Whether the client should follow redirects.
     * @return The instance of this class the method was called with.
     */
    public HttpRequest setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
        return this;
    }

    /**
     * Returns whether to automatically buffer the resopnse entity.
     *
     * @return Whether to automatically buffer the resopnse entity.
     */
    public boolean isBufferResponseEntity() {
        return bufferResponseEntity;
    }

    /**
     * Sets whether to automatically buffer the response entity.
     *
     * @param bufferEntity Whether to automatically buffer the response entity.
     * @return The instance of this class the method was called with.
     */
    public HttpRequest setBufferResponseEntity(boolean bufferEntity) {
        this.bufferResponseEntity = bufferEntity;
        return this;
    }

    /**
     * Returns the request headers.
     *
     * @return The request headers.
     */
    public MultiValuedMap getHeaders() {
        return headers;
    }

    /**
     * Sets the request headers to the given map of headers.
     *
     * @param headers Map of headers.
     * @return The instance of this class the method was called with.
     */
    public HttpRequest setHeaders(MultiValuedMap headers) {
        this.headers.set(headers);
        return this;
    }

    /**
     * Add a single header to the request.
     *
     * @param name  Name of the header.
     * @param value Value of the header.
     * @return The instance of this class the method was called with.
     */
    public HttpRequest addHeader(String name, String value) {
        headers.add(name, value);
        return this;
    }

    /**
     * Add several headers with the same name to the request.
     *
     * @param name   Name of the header.
     * @param values A {@code List} of values of the header.
     * @return The instance of this class the method was called with.
     */
    public HttpRequest addHeader(String name, List<String> values) {
        headers.add(name, values);
        return this;
    }

    /**
     * Add many headers to the request.
     *
     * @param headers A map of headers, where the key is the name of the header and the value is either a
     *                {@code String} or a {@code List} of {@code String Strings}.
     * @return The instance of this class the method was called with.
     */
    @SuppressWarnings("unchecked")
    public HttpRequest addHeaders(MultiValuedMap headers) {
        this.headers.add(headers);
        return this;
    }

    /**
     * Overwrites the given header and sets it to the given value.
     *
     * @param name  Name of the header.
     * @param value Value of the header.
     * @return The instance of this class the method was called with.
     */
    public HttpRequest setHeader(String name, String value) {
        headers.set(name, value);
        return this;
    }

    /**
     * Overwrites the given header and sets it to the given list of values.
     *
     * @param name   Name of the header.
     * @param values A <code>List</code> of values of the header.
     * @return The instance of this class the method was called with.
     */
    public HttpRequest setHeader(String name, List<String> values) {
        headers.set(name, values);
        return this;
    }

    /**
     * Returns the query parameters of the request.
     *
     * @return A copy of the map containing the query parameters.
     */
    public MultiValuedMap getQueryParameters() {
        return queryParameters;
    }

    /**
     * Sets the request query parameters to the given map of query parameters.
     *
     * @param queryParameters Map of headers.
     * @return The instance of this class the method was called with.
     */
    public HttpRequest setQueryParameters(MultiValuedMap queryParameters) {
        this.queryParameters.set(queryParameters);
        return this;
    }

    /**
     * Add a single query parameter to the request.
     *
     * @param name  Name of the query parameter.
     * @param value Value of the query parameter.
     * @return The instance of this class the method was called with.
     */
    public HttpRequest addQueryParameter(String name, String value) {
        queryParameters.add(name, value);
        return this;
    }

    /**
     * Add several query parameters with the same name to the request.
     *
     * @param name   Name of the query parameter.
     * @param values A <code>List</code> of values of the query parameter.
     * @return The instance of this class the method was called with.
     */
    public HttpRequest addQueryParameter(String name, List<String> values) {
        queryParameters.add(name, values);
        return this;
    }

    /**
     * Add many query parameters to the request.
     *
     * @param parameters A map of query parameters, where the key is the name of the query parameter and the value is
     *                   either a <code>String</code> or a <code>List</code> of <code>String</code>s.
     * @return The instance of this class the method was called with.
     */
    @SuppressWarnings("unchecked")
    public HttpRequest addQueryParameters(MultiValuedMap parameters) {
        this.queryParameters.add(parameters);
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
    public HttpRequest setQueryParameter(String name, String value) {
        queryParameters.set(name, value);
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
    public HttpRequest setQueryParameter(String name, List<String> values) {
        queryParameters.set(name, values);
        return this;
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
        String path = uri.getPath();
        String query = uri.getQuery();

        StringBuilder builder = new StringBuilder(scheme).append("://").append(host);

        if (port != -1 && !(scheme.equalsIgnoreCase("https") && port == 443) && !(scheme.equalsIgnoreCase("http") && port == 80)) {
            builder = builder.append(':').append(port);
        }

        builder.append(path);

        this.uri = builder.toString();

        if (query != null && query.length() > 0) {
            Arrays.stream(query.split("&")).forEach(it -> {
                List<String> parts = Arrays.stream(it.split("=")).collect(Collectors.toList());

                if (parts.size() == 1) {
                    addQueryParameter(parts.get(0), "");
                }
                else {
                    String name = parts.remove(0);
                    addQueryParameter(name, String.join("=", parts));
                }
            });
        }
    }

    /**
     * Deep-clone the {@link HttpRequest}.
     *
     * @return A new {@link HttpRequest}.
     */
    public Object clone() throws CloneNotSupportedException {
        HttpRequest request = (HttpRequest) super.clone();

        try {
            request.setUri(getUri());
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        request.setAccept(getAccept());
        request.setBufferResponseEntity(isBufferResponseEntity());
        request.setConnectionTimeout(getConnectionTimeout());
        request.setReadTimeout(getReadTimeout());
        request.setFollowRedirects(isFollowRedirects());
        request.setSslValidated(isSslValidated());

        request.setHeaders((MultiValuedMap) getHeaders().clone());
        request.setQueryParameters((MultiValuedMap) getQueryParameters().clone());

        return request;
    }
}
