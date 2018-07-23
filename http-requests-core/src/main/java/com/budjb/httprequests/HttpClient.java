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

import com.budjb.httprequests.converter.EntityConverterManager;
import com.budjb.httprequests.converter.EntityWriter;
import com.budjb.httprequests.exception.UnsupportedConversionException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

/**
 * An interface that describes the common structure and methods of an HTTP client.
 * <p>
 * Various filter classes are supported.
 */
public interface HttpClient {
    /**
     * Execute an HTTP request with the given method and request parameters and without a request entity.
     *
     * @param method  HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    HttpResponse execute(HttpMethod method, HttpRequest request) throws IOException;

    /**
     * Execute an HTTP request with the given method and URI and without a request entity.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param uri    URI of the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException        When an underlying IO exception occurs.
     * @throws URISyntaxException When the syntax of the request is incorrect.
     */
    HttpResponse execute(HttpMethod method, String uri) throws IOException, URISyntaxException;

    /**
     * Executes an HTTP request with the given method, request parameters, and input stream.
     *
     * @param method      HTTP method to use with the HTTP request.
     * @param request     Request properties to use with the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    HttpResponse execute(HttpMethod method, HttpRequest request, InputStream inputStream) throws IOException;

    /**
     * Executes an HTTP request with the given method, URI, and input stream.
     *
     * @param method      HTTP method to use with the HTTP request.
     * @param uri         URI of the request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException        When an underlying IO exception occurs.
     * @throws URISyntaxException When the syntax of the request is incorrect.
     */
    HttpResponse execute(HttpMethod method, String uri, InputStream inputStream) throws IOException, URISyntaxException;

    /**
     * Executes an HTTP request with the given method, request parameters, and entity.
     *
     * @param method  HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param entity  An HTTP entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    HttpResponse execute(HttpMethod method, HttpRequest request, HttpEntity entity) throws IOException;

    /**
     * Executes an HTTP request with the given method, URI, and entity.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param uri    URI of the request.
     * @param entity An HTTP entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException        When an underlying IO exception occurs.
     * @throws URISyntaxException When the syntax of the request is incorrect.
     */
    HttpResponse execute(HttpMethod method, String uri, HttpEntity entity) throws IOException, URISyntaxException;

    /**
     * Executes an HTTP request with the given method, request parameters, and entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param method  HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param entity  Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    HttpResponse execute(HttpMethod method, HttpRequest request, Object entity) throws IOException, UnsupportedConversionException;

    /**
     * Executes an HTTP request with the given method, URI, and entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param uri    URI of the request.
     * @param entity Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws URISyntaxException             When the syntax of the request is incorrect.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    HttpResponse execute(HttpMethod method, String uri, Object entity) throws IOException, URISyntaxException, UnsupportedConversionException;

    /**
     * Executes an HTTP request with the given method, request parameters, and entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param method  HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param entity  Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    HttpResponse execute(HttpMethod method, HttpRequest request, ConvertingHttpEntity entity) throws IOException, UnsupportedConversionException;

    /**
     * Executes an HTTP request with the given method, URI, and entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param uri    URI of the request.
     * @param entity Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws URISyntaxException             When the syntax of the request is incorrect.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    HttpResponse execute(HttpMethod method, String uri, ConvertingHttpEntity entity) throws IOException, URISyntaxException, UnsupportedConversionException;

    /**
     * Perform an HTTP GET request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    HttpResponse get(HttpRequest request) throws IOException;

    /**
     * Perform an HTTP GET request.
     *
     * @param uri URI of the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException        When an underlying IO exception occurs.
     * @throws URISyntaxException When the syntax of the request is incorrect.
     */
    HttpResponse get(String uri) throws IOException, URISyntaxException;

    /**
     * Perform an HTTP POST request without a request entity.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    HttpResponse post(HttpRequest request) throws IOException;

    /**
     * Perform an HTTP POST request.
     *
     * @param uri URI of the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException        When an underlying IO exception occurs.
     * @throws URISyntaxException When the syntax of the request is incorrect.
     */
    HttpResponse post(String uri) throws IOException, URISyntaxException;

    /**
     * Perform an HTTP POST request with the given input stream.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity  An HTTP entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    HttpResponse post(HttpRequest request, HttpEntity entity) throws IOException;

    /**
     * Perform an HTTP POST request.
     *
     * @param uri    URI of the request.
     * @param entity An HTTP entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException        When an underlying IO exception occurs.
     * @throws URISyntaxException When the syntax of the request is incorrect.
     */
    HttpResponse post(String uri, HttpEntity entity) throws IOException, URISyntaxException;

    /**
     * Perform an HTTP POST request with the given input stream.
     *
     * @param request     Request properties to use with the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    HttpResponse post(HttpRequest request, InputStream inputStream) throws IOException;

    /**
     * Perform an HTTP POST request.
     *
     * @param uri         URI of the request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException        When an underlying IO exception occurs.
     * @throws URISyntaxException When the syntax of the request is incorrect.
     */
    HttpResponse post(String uri, InputStream inputStream) throws IOException, URISyntaxException;

    /**
     * Perform an HTTP POST request with the given entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity  Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    HttpResponse post(HttpRequest request, Object entity) throws IOException, UnsupportedConversionException;


    /**
     * Perform an HTTP POST request.
     *
     * @param uri    URI of the request.
     * @param entity Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws URISyntaxException             When the syntax of the request is incorrect.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    HttpResponse post(String uri, Object entity) throws IOException, URISyntaxException, UnsupportedConversionException;

    /**
     * Perform an HTTP POST request with the given entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity  Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    HttpResponse post(HttpRequest request, ConvertingHttpEntity entity) throws IOException, UnsupportedConversionException;

    /**
     * Perform an HTTP POST request.
     *
     * @param uri    URI of the request.
     * @param entity Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws URISyntaxException             When the syntax of the request is incorrect.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    HttpResponse post(String uri, ConvertingHttpEntity entity) throws IOException, URISyntaxException, UnsupportedConversionException;

    /**
     * Perform an HTTP PUT request without a request entity.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    HttpResponse put(HttpRequest request) throws IOException;

    /**
     * Perform an HTTP PUT request without a request entity.
     *
     * @param uri URI of the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException        When an underlying IO exception occurs.
     * @throws URISyntaxException When the syntax of the request is incorrect.
     */
    HttpResponse put(String uri) throws IOException, URISyntaxException;

    /**
     * Perform an HTTP PUT request with the given request entity.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity  An HTTP entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    HttpResponse put(HttpRequest request, HttpEntity entity) throws IOException;

    /**
     * Perform an HTTP PUT request with the given request entity.
     *
     * @param uri    URI of the request.
     * @param entity An HTTP entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException        When an underlying IO exception occurs.
     * @throws URISyntaxException When the syntax of the request is incorrect.
     */
    HttpResponse put(String uri, HttpEntity entity) throws IOException, URISyntaxException;

    /**
     * Perform an HTTP PUT request with the given input stream.
     *
     * @param request     Request properties to use with the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    HttpResponse put(HttpRequest request, InputStream inputStream) throws IOException;

    /**
     * Perform an HTTP PUT request with the given input stream.
     *
     * @param uri         URI of the request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException        When an underlying IO exception occurs.
     * @throws URISyntaxException When the syntax of the request is incorrect.
     */
    HttpResponse put(String uri, InputStream inputStream) throws IOException, URISyntaxException;

    /**
     * Perform an HTTP PUT request with the given entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity  Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    HttpResponse put(HttpRequest request, Object entity) throws IOException, UnsupportedConversionException;

    /**
     * Perform an HTTP PUT request with the given entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param uri    URI of the request.
     * @param entity Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws URISyntaxException             When the syntax of the request is incorrect.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    HttpResponse put(String uri, Object entity) throws IOException, URISyntaxException, UnsupportedConversionException;

    /**
     * Perform an HTTP PUT request with the given entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity  Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    HttpResponse put(HttpRequest request, ConvertingHttpEntity entity) throws IOException, UnsupportedConversionException;

    /**
     * Perform an HTTP PUT request with the given entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param uri    URI of the request.
     * @param entity Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws URISyntaxException             When the syntax of the request is incorrect.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    HttpResponse put(String uri, ConvertingHttpEntity entity) throws IOException, URISyntaxException, UnsupportedConversionException;

    /**
     * Perform an HTTP DELETE request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    HttpResponse delete(HttpRequest request) throws IOException;

    /**
     * Perform an HTTP DELETE request.
     *
     * @param uri URI of the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException        When an underlying IO exception occurs.
     * @throws URISyntaxException When the syntax of the request is incorrect.
     */
    HttpResponse delete(String uri) throws IOException, URISyntaxException;

    /**
     * Perform an HTTP OPTIONS request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    HttpResponse options(HttpRequest request) throws IOException;

    /**
     * Perform an HTTP OPTIONS request.
     *
     * @param uri URI of the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException        When an underlying IO exception occurs.
     * @throws URISyntaxException When the syntax of the request is incorrect.
     */
    HttpResponse options(String uri) throws IOException, URISyntaxException;

    /**
     * Perform an HTTP OPTIONS request with the given input stream.
     *
     * @param request     Request properties to use with the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    HttpResponse options(HttpRequest request, InputStream inputStream) throws IOException;

    /**
     * Perform an HTTP OPTIONS request with the given input stream.
     *
     * @param uri         URI of the request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException        When an underlying IO exception occurs.
     * @throws URISyntaxException When the syntax of the request is incorrect.
     */
    HttpResponse options(String uri, InputStream inputStream) throws IOException, URISyntaxException;

    /**
     * Perform an HTTP OPTIONS request with the given entity.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity  An HTTP entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    HttpResponse options(HttpRequest request, HttpEntity entity) throws IOException;

    /**
     * Perform an HTTP OPTIONS request with the given entity.
     *
     * @param uri    URI of the request.
     * @param entity An HTTP entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException        When an underlying IO exception occurs.
     * @throws URISyntaxException When the syntax of the request is incorrect.
     */
    HttpResponse options(String uri, HttpEntity entity) throws IOException, URISyntaxException;

    /**
     * Perform an HTTP OPTIONS request with the given entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity  Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    HttpResponse options(HttpRequest request, Object entity) throws IOException, UnsupportedConversionException;

    /**
     * Perform an HTTP OPTIONS request with the given entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param uri    URI of the request.
     * @param entity Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws URISyntaxException             When the syntax of the request is incorrect.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    HttpResponse options(String uri, Object entity) throws IOException, URISyntaxException, UnsupportedConversionException;

    /**
     * Perform an HTTP OPTIONS request with the given entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity  Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    HttpResponse options(HttpRequest request, ConvertingHttpEntity entity) throws IOException, UnsupportedConversionException;

    /**
     * Perform an HTTP OPTIONS request with the given entity.
     * <p>
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param uri    URI of the request.
     * @param entity Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException                    When an underlying IO exception occurs.
     * @throws URISyntaxException             When the syntax of the request is incorrect.
     * @throws UnsupportedConversionException When an error in entity conversion occurs.
     */
    HttpResponse options(String uri, ConvertingHttpEntity entity) throws IOException, URISyntaxException, UnsupportedConversionException;

    /**
     * Perform an HTTP HEAD request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    HttpResponse head(HttpRequest request) throws IOException;

    /**
     * Perform an HTTP HEAD request.
     *
     * @param uri URI of the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException        When an underlying IO exception occurs.
     * @throws URISyntaxException When the syntax of the request is incorrect.
     */
    HttpResponse head(String uri) throws IOException, URISyntaxException;

    /**
     * Perform an HTTP TRACE request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException When an underlying IO exception occurs.
     */
    HttpResponse trace(HttpRequest request) throws IOException;

    /**
     * Perform an HTTP TRACE request.
     *
     * @param uri URI of the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException        When an underlying IO exception occurs.
     * @throws URISyntaxException When the syntax of the request is incorrect.
     */
    HttpResponse trace(String uri) throws IOException, URISyntaxException;

    /**
     * Returns the entity converter manager associated with this client.
     *
     * @return The entity converter manager.
     */
    EntityConverterManager getConverterManager();
}