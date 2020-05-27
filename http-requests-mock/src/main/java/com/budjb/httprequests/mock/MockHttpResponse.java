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
package com.budjb.httprequests.mock;

import com.budjb.httprequests.*;
import com.budjb.httprequests.converter.EntityConverterManager;
import com.budjb.httprequests.exception.UnsupportedConversionException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An {@link HttpResponse} implementation that wraps a {@link RequestMock}.
 */
public class MockHttpResponse implements HttpResponse {
    /**
     * Pattern to parse content type.
     */
    private final static Pattern CONTENT_TYPE_PATTERN = Pattern.compile("^([^;]+).*$");

    /**
     * Pattern to parse character set.
     */
    private final static Pattern CHARACTER_SET_PATTERN = Pattern.compile("charset\\s*=\\s*([^;]+)");

    /**
     * Entity converter manager.
     */
    private final EntityConverterManager entityConverterManager;

    /**
     * Mock that matched the request.
     */
    private RequestMock mock;

    /**
     * HTTP request.
     */
    private HttpRequest request;

    /**
     * Input stream containing the response entity.
     */
    private HttpEntity entity;

    /**
     * Response HTTP status.
     */
    private int status;

    /**
     * Response headers.
     */
    private MultiValuedMap headers = new MultiValuedMap();

    /**
     * Request context.
     */
    private HttpContext context;

    /**
     * Constructor that's useful to manually build up a response.
     *
     * @param entityConverterManager Converter manager.
     */
    public MockHttpResponse(EntityConverterManager entityConverterManager) {
        this.entityConverterManager = entityConverterManager;
    }

    /**
     * Constructor that populates the response from a request and its matching mock.
     *
     * @param entityConverterManager Entity converter manager.
     * @param request                Request that resulted in this response.
     * @param mock                   Request mock to build the response from.
     */
    MockHttpResponse(EntityConverterManager entityConverterManager, HttpContext context, HttpRequest request, RequestMock mock) throws IOException {
        this.entityConverterManager = entityConverterManager;
        this.mock = mock;
        this.status = mock.getResponseStatusCode();
        this.headers = mock.getResponseHeaders();
        this.request = request;
        this.context = context;

        if (mock.getResponseEntity() != null) {
            InputStream inputStream = mock.getResponseEntity();
            int read = inputStream.read();

            if (read != -1) {
                PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream);
                pushbackInputStream.unread(read);

                String contentType = headers.getFlat("Content-Type");

                if (contentType != null) {
                    Matcher contentMatcher = CONTENT_TYPE_PATTERN.matcher(contentType);

                    if (contentMatcher.find()) {
                        Matcher charsetMatcher = CHARACTER_SET_PATTERN.matcher(contentType);

                        if (charsetMatcher.find()) {
                            entity = new HttpEntity(pushbackInputStream, contentMatcher.group(1), charsetMatcher.group(1));
                        }
                        else {
                            entity = new HttpEntity(pushbackInputStream, contentMatcher.group(1));
                        }
                    }
                }

                entity = new HttpEntity(pushbackInputStream);
            }
        }
    }

    /**
     * Returns the request HTTP context.
     *
     * @return Request HTTP context.
     */
    public HttpContext getContext() {
        return context;
    }

    /**
     * Returns the mock that matched the request.
     *
     * @return The mock that matched the request.
     */
    public RequestMock getMock() {
        return mock;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStatus() {
        return status;
    }

    /**
     * Returns the response HTTP status.
     *
     * @param status Response HTTP status.
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequest getRequest() {
        return request;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHeader(String name) {
        if (getHeaders().containsKey(name) && getHeaders().get(name).size() > 0) {
            return getHeaders().get(name).get(0);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getHeaders(String name) {
        return headers.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MultiValuedMap getHeaders() {
        return headers;
    }

    /**
     * Sets the response headers.
     *
     * @param headers Response headers.
     */
    public void setHeaders(MultiValuedMap headers) {
        this.headers = headers;
    }

    /**
     * Sets the response header with the given name with the given values.
     *
     * @param name   Name of the header.
     * @param values Values to assign to the header.
     */
    public void setHeader(String name, List<String> values) {
        this.headers.set(name, values);
    }

    /**
     * Sets the response header with the given name with the given value.
     *
     * @param name  Name of the header.
     * @param value Value to assign to the header.
     */
    public void setHeader(String name, String value) {
        this.headers.set(name, value);
    }

    /**
     * Adds a list of values to the response header with the given name.
     *
     * @param name   Name of the header.
     * @param values Values to add to the header.
     */
    public void addHeader(String name, List<String> values) {
        this.headers.add(name, values);
    }

    /**
     * Adds a value to the response header with the given name.
     *
     * @param name  Name of the header.
     * @param value Value to add to the header.
     */
    public void addHeader(String name, String value) {
        this.headers.add(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpEntity getEntity() {
        return entity;
    }

    /**
     * Sets the response entity.
     *
     * @param entity The response entity.
     */
    public void setEntity(HttpEntity entity) {
        this.entity = entity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getEntity(Class<T> type) throws UnsupportedConversionException, IOException {
        return entityConverterManager.read(type, entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasEntity() {
        return entity != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {

    }
}
