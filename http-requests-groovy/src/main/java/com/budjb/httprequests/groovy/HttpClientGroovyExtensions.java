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

import com.budjb.httprequests.*;
import com.budjb.httprequests.converter.EntityWriter;
import com.budjb.httprequests.exception.UnsupportedConversionException;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

import java.io.IOException;
import java.io.InputStream;

/**
 * Provides Groovy extensions to {@link HttpClient}. Specifically, these extensions allow
 * {@link Closure closures} to be used to create {@link HttpRequest} instances, instead of
 * creating and passing them directly.
 */
public class HttpClientGroovyExtensions {
    /**
     * Execute an HTTP request with the given method and request parameters and without a request entity.
     *
     * @param method  HTTP method to use with the HTTP request.
     * @param closure A closure that configures the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    public static HttpResponse execute(HttpClient self, HttpMethod method, @DelegatesTo(HttpRequestDelegate.class) Closure closure) throws IOException {
        return self.execute(method, build(closure));
    }

    /**
     * Executes an HTTP request with the given method, request parameters, and input stream.
     *
     * @param method      HTTP method to use with the HTTP request.
     * @param closure     A closure that configures the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    public static HttpResponse execute(HttpClient self, HttpMethod method, @DelegatesTo(HttpRequestDelegate.class) Closure closure, InputStream inputStream) throws IOException {
        return self.execute(method, build(closure), inputStream);
    }

    /**
     * Executes an HTTP request with the given method, request parameters, and entity.
     *
     * @param method  HTTP method to use with the HTTP request.
     * @param closure A closure that configures the HTTP request.
     * @param entity  An HTTP entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    public static HttpResponse execute(HttpClient self, HttpMethod method, @DelegatesTo(HttpRequestDelegate.class) Closure closure, HttpEntity entity) throws IOException {
        return self.execute(method, build(closure), entity);
    }

    /**
     * Executes an HTTP request with the given method, request parameters, and entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param method  HTTP method to use with the HTTP request.
     * @param closure A closure that configures the HTTP request.
     * @param entity  Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    public static HttpResponse execute(HttpClient self, HttpMethod method, @DelegatesTo(HttpRequestDelegate.class) Closure closure, Object entity) throws IOException, UnsupportedConversionException {
        return self.execute(method, build(closure), entity);
    }

    /**
     * Executes an HTTP request with the given method, request parameters, and entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param method  HTTP method to use with the HTTP request.
     * @param closure A closure that configures the HTTP request.
     * @param entity  Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    public static HttpResponse execute(HttpClient self, HttpMethod method, @DelegatesTo(HttpRequestDelegate.class) Closure closure, ConvertingHttpEntity entity) throws IOException, UnsupportedConversionException {
        return self.execute(method, build(closure), entity);
    }

    /**
     * Perform an HTTP GET request.
     *
     * @param closure A closure that configures the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    public static HttpResponse get(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure) throws IOException {
        return execute(self, HttpMethod.GET, closure);
    }

    /**
     * Perform an HTTP DELETE request.
     *
     * @param closure A closure that configures the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    public static HttpResponse delete(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure) throws IOException {
        return execute(self, HttpMethod.DELETE, closure);
    }

    /**
     * Perform an HTTP DELETE request with the given request entity.
     *
     * @param closure A closure that configures the HTTP request.
     * @param entity  An HTTP entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    public static HttpResponse delete(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, HttpEntity entity) throws IOException {
        return execute(self, HttpMethod.DELETE, closure, entity);
    }

    /**
     * Perform an HTTP DELETE request with the given input stream.
     *
     * @param closure     A closure that configures the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    public static HttpResponse delete(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, InputStream inputStream) throws IOException {
        return execute(self, HttpMethod.DELETE, closure, inputStream);
    }

    /**
     * Perform an HTTP DELETE request with the given entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param closure A closure that configures the HTTP request.
     * @param entity  Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    public static HttpResponse delete(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, Object entity) throws IOException, UnsupportedConversionException {
        return execute(self, HttpMethod.DELETE, closure, entity);
    }

    /**
     * Perform an HTTP DELETE request with the given entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param closure A closure that configures the HTTP request.
     * @param entity  Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    public static HttpResponse delete(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, ConvertingHttpEntity entity) throws IOException, UnsupportedConversionException {
        return execute(self, HttpMethod.DELETE, closure, entity);
    }

    /**
     * Perform an HTTP POST request.
     *
     * @param closure A closure that configures the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    public static HttpResponse post(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure) throws IOException {
        return execute(self, HttpMethod.POST, closure);
    }

    /**
     * Perform an HTTP POST request with the given request entity.
     *
     * @param closure A closure that configures the HTTP request.
     * @param entity  An HTTP entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    public static HttpResponse post(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, HttpEntity entity) throws IOException {
        return execute(self, HttpMethod.POST, closure, entity);
    }

    /**
     * Perform an HTTP POST request with the given input stream.
     *
     * @param closure     A closure that configures the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    public static HttpResponse post(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, InputStream inputStream) throws IOException {
        return execute(self, HttpMethod.POST, closure, inputStream);
    }

    /**
     * Perform an HTTP POST request with the given entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param closure A closure that configures the HTTP request.
     * @param entity  Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    public static HttpResponse post(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, Object entity) throws IOException, UnsupportedConversionException {
        return execute(self, HttpMethod.POST, closure, entity);
    }

    /**
     * Perform an HTTP POST request with the given entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param closure A closure that configures the HTTP request.
     * @param entity  Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    public static HttpResponse post(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, ConvertingHttpEntity entity) throws IOException, UnsupportedConversionException {
        return execute(self, HttpMethod.POST, closure, entity);
    }

    /**
     * Perform an HTTP PUT request.
     *
     * @param closure A closure that configures the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    public static HttpResponse put(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure) throws IOException {
        return execute(self, HttpMethod.PUT, closure);
    }

    /**
     * Perform an HTTP PUT request with the given request entity.
     *
     * @param closure A closure that configures the HTTP request.
     * @param entity  An HTTP entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    public static HttpResponse put(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, HttpEntity entity) throws IOException {
        return execute(self, HttpMethod.PUT, closure, entity);
    }

    /**
     * Perform an HTTP PUT request with the given input stream.
     *
     * @param closure     A closure that configures the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    public static HttpResponse put(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, InputStream inputStream) throws IOException {
        return execute(self, HttpMethod.PUT, closure, inputStream);
    }

    /**
     * Perform an HTTP PUT request with the given entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param closure A closure that configures the HTTP request.
     * @param entity  Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    public static HttpResponse put(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, Object entity) throws IOException, UnsupportedConversionException {
        return execute(self, HttpMethod.PUT, closure, entity);
    }

    /**
     * Perform an HTTP PUT request with the given entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param closure A closure that configures the HTTP request.
     * @param entity  Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    public static HttpResponse put(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, ConvertingHttpEntity entity) throws IOException, UnsupportedConversionException {
        return execute(self, HttpMethod.PUT, closure, entity);
    }

    /**
     * Perform an HTTP OPTIONS request.
     *
     * @param closure A closure that configures the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    public static HttpResponse options(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure) throws IOException {
        return execute(self, HttpMethod.OPTIONS, closure);
    }

    /**
     * Perform an HTTP OPTIONS request with the given request entity.
     *
     * @param closure A closure that configures the HTTP request.
     * @param entity  An HTTP entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    public static HttpResponse options(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, HttpEntity entity) throws IOException {
        return execute(self, HttpMethod.OPTIONS, closure, entity);
    }

    /**
     * Perform an HTTP OPTIONS request with the given input stream.
     *
     * @param closure     A closure that configures the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    public static HttpResponse options(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, InputStream inputStream) throws IOException {
        return execute(self, HttpMethod.OPTIONS, closure, inputStream);
    }

    /**
     * Perform an HTTP OPTIONS request with the given entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param closure A closure that configures the HTTP request.
     * @param entity  Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    public static HttpResponse options(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, Object entity) throws IOException, UnsupportedConversionException {
        return execute(self, HttpMethod.OPTIONS, closure, entity);
    }

    /**
     * Perform an HTTP OPTIONS request with the given entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param closure A closure that configures the HTTP request.
     * @param entity  Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    public static HttpResponse options(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, ConvertingHttpEntity entity) throws IOException, UnsupportedConversionException {
        return execute(self, HttpMethod.OPTIONS, closure, entity);
    }

    /**
     * Perform an HTTP HEAD request.
     *
     * @param closure A closure that configures the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    public static HttpResponse head(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure) throws IOException {
        return execute(self, HttpMethod.HEAD, closure);
    }

    /**
     * Perform an HTTP HEAD request with the given request entity.
     *
     * @param closure A closure that configures the HTTP request.
     * @param entity  An HTTP entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    public static HttpResponse head(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, HttpEntity entity) throws IOException {
        return execute(self, HttpMethod.HEAD, closure, entity);
    }

    /**
     * Perform an HTTP HEAD request with the given input stream.
     *
     * @param closure     A closure that configures the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    public static HttpResponse head(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, InputStream inputStream) throws IOException {
        return execute(self, HttpMethod.HEAD, closure, inputStream);
    }

    /**
     * Perform an HTTP HEAD request with the given entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param closure A closure that configures the HTTP request.
     * @param entity  Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    public static HttpResponse head(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, Object entity) throws IOException, UnsupportedConversionException {
        return execute(self, HttpMethod.HEAD, closure, entity);
    }

    /**
     * Perform an HTTP HEAD request with the given entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param closure A closure that configures the HTTP request.
     * @param entity  Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    public static HttpResponse head(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, ConvertingHttpEntity entity) throws IOException, UnsupportedConversionException {
        return execute(self, HttpMethod.HEAD, closure, entity);
    }

    /**
     * Perform an HTTP PATCH request.
     *
     * @param closure A closure that configures the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    public static HttpResponse patch(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure) throws IOException {
        return execute(self, HttpMethod.PATCH, closure);
    }

    /**
     * Perform an HTTP PATCH request with the given request entity.
     *
     * @param closure A closure that configures the HTTP request.
     * @param entity  An HTTP entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    public static HttpResponse patch(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, HttpEntity entity) throws IOException {
        return execute(self, HttpMethod.PATCH, closure, entity);
    }

    /**
     * Perform an HTTP PATCH request with the given input stream.
     *
     * @param closure     A closure that configures the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    public static HttpResponse patch(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, InputStream inputStream) throws IOException {
        return execute(self, HttpMethod.PATCH, closure, inputStream);
    }

    /**
     * Perform an HTTP PATCH request with the given entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param closure A closure that configures the HTTP request.
     * @param entity  Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    public static HttpResponse patch(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, Object entity) throws IOException, UnsupportedConversionException {
        return execute(self, HttpMethod.PATCH, closure, entity);
    }

    /**
     * Perform an HTTP PATCH request with the given entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param closure A closure that configures the HTTP request.
     * @param entity  Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    public static HttpResponse patch(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure, ConvertingHttpEntity entity) throws IOException, UnsupportedConversionException {
        return execute(self, HttpMethod.PATCH, closure, entity);
    }

    /**
     * Perform an HTTP TRACE request.
     *
     * @param closure A closure that configures the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    public static HttpResponse trace(HttpClient self, @DelegatesTo(HttpRequestDelegate.class) Closure closure) throws IOException {
        return execute(self, HttpMethod.TRACE, closure);
    }

    /**
     * Runs the given closure to build an {@link HttpRequest}.
     *
     * @param closure A closure that configures the HTTP request.
     * @return The resulting HTTP request object.
     */
    private static HttpRequest build(@DelegatesTo(HttpRequestDelegate.class) Closure closure) {
        HttpRequest request = new HttpRequest();
        closure = (Closure) closure.clone();
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.setDelegate(request);
        closure.call();
        return request;
    }
}
