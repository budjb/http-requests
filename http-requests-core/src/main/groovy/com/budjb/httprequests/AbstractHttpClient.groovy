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
package com.budjb.httprequests

import com.budjb.httprequests.converter.EntityConverter
import com.budjb.httprequests.converter.EntityConverterManager
import com.budjb.httprequests.converter.EntityWriter
import com.budjb.httprequests.exception.UnsupportedConversionException
import com.budjb.httprequests.filter.HttpClientFilter
import com.budjb.httprequests.filter.HttpClientFilterManager

import javax.net.ssl.*
import java.security.SecureRandom
import java.security.cert.X509Certificate

/**
 * A base class for HTTP clients that implements most of the functionality of the {@link HttpClient} interface.
 *
 * Individual HTTP client library implementations should extend this class.
 */
abstract class AbstractHttpClient implements HttpClient {
    /**
     * Converter manager.
     */
    EntityConverterManager converterManager

    /**
     * Filter manager.
     */
    HttpClientFilterManager filterManager

    /**
     * Implements the logic to make an actual request with an HTTP client library.
     *
     * @param context HTTP request context.
     * @param inputStream An {@link InputStream} containing the response body. May be <code>null</code>.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    protected
    abstract HttpResponse doExecute(HttpContext context, InputStream inputStream) throws IOException

    /**
     * Execute an HTTP request with the given method and request parameters and without a request entity.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse execute(HttpMethod method, HttpRequest request) throws IOException {
        return run(method, request, null)
    }

    /**
     * Executes an HTTP request with the given method and closure to configure the request without a request entity.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse execute(HttpMethod method, @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(method, HttpRequest.build(requestClosure))
    }

    /**
     * Executes an HTTP request with the given method, request parameters, and input stream.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse execute(HttpMethod method, HttpRequest request, InputStream inputStream) throws IOException {
        return run(method, request, inputStream)
    }

    /**
     * Executes an HTTP request with the given method, closure to configure the request, and input stream.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse execute(HttpMethod method, InputStream inputStream,
                         @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(method, HttpRequest.build(requestClosure), inputStream)
    }

    /**
     * Executes an HTTP request with the given method, request parameters, and entity.
     *
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param entity Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     * @throws UnsupportedConversionException
     */
    @Override
    HttpResponse execute(HttpMethod method, HttpRequest request, Object entity) throws IOException, UnsupportedConversionException {
        return execute(method, request, converterManager.write(request, entity))
    }

    /**
     * Executes an HTTP request with the given method, closure to configure the request, and entity.
     *
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param entity Request entity.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     * @throws UnsupportedConversionException
     */
    @Override
    HttpResponse execute(HttpMethod method, Object entity,
                         @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException, UnsupportedConversionException {
        return execute(method, HttpRequest.build(requestClosure), entity)
    }

    /**
     * Perform an HTTP GET request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse get(HttpRequest request) throws IOException {
        return execute(HttpMethod.GET, request)
    }

    /**
     * Perform an HTTP GET request.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse get(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.GET, requestClosure)
    }

    /**
     * Perform an HTTP POST request without a request entity.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse post(HttpRequest request) throws IOException {
        return execute(HttpMethod.POST, request)
    }

    /**
     * Perform an HTTP POST request without a request entity.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse post(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.POST, requestClosure)
    }

    /**
     * Perform an HTTP POST request with the given input stream.
     *
     * @param request Request properties to use with the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse post(HttpRequest request, InputStream inputStream) throws IOException {
        return execute(HttpMethod.POST, request, inputStream)
    }

    /**
     * Perform an HTTP POST request with the given input stream.
     *
     * @param inputStream An {@link InputStream} containing the response body.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse post(InputStream inputStream, @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.POST, inputStream, requestClosure)
    }

    /**
     * Perform an HTTP POST request with the given entity.
     *
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     * @throws UnsupportedConversionException
     */
    @Override
    HttpResponse post(HttpRequest request, Object entity) throws IOException, UnsupportedConversionException {
        return execute(HttpMethod.POST, request, entity)
    }

    /**
     * Perform an HTTP POST request with the given entity.
     *
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param entity Request entity.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     * @throws UnsupportedConversionException
     */
    @Override
    HttpResponse post(Object entity,
                      @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException, UnsupportedConversionException {
        return execute(HttpMethod.POST, entity, requestClosure)
    }

    /**
     * Perform an HTTP PUT request without a request entity.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse put(HttpRequest request) throws IOException {
        return execute(HttpMethod.PUT, request)
    }

    /**
     * Perform an HTTP PUT request without a request entity.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse put(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.PUT, requestClosure)
    }

    /**
     * Perform an HTTP PUT request with the given input stream..
     *
     * @param request Request properties to use with the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse put(HttpRequest request, InputStream inputStream) throws IOException {
        return execute(HttpMethod.PUT, request, inputStream)
    }

    /**
     * Perform an HTTP PUT request with the given input stream.
     *
     * @param inputStream An {@link InputStream} containing the response body.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse put(InputStream inputStream, @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.PUT, inputStream, requestClosure)
    }

    /**
     * Perform an HTTP PUT request with the given entity.
     *
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     * @throws UnsupportedConversionException
     */
    @Override
    HttpResponse put(HttpRequest request, Object entity) throws IOException, UnsupportedConversionException {
        return execute(HttpMethod.PUT, request, entity)
    }

    /**
     * Perform an HTTP PUT request with the given entity.
     *
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param entity Request entity.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     * @throws UnsupportedConversionException
     */
    @Override
    HttpResponse put(Object entity,
                     @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException, UnsupportedConversionException {
        return execute(HttpMethod.PUT, entity, requestClosure)
    }

    /**
     * Perform an HTTP DELETE request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse delete(HttpRequest request) throws IOException {
        return execute(HttpMethod.DELETE, request)
    }

    /**
     * Perform an HTTP DELETE request.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse delete(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.DELETE, requestClosure)
    }

    /**
     * Perform an HTTP OPTIONS request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse options(HttpRequest request) throws IOException {
        return execute(HttpMethod.OPTIONS, request)
    }

    /**
     * Perform an HTTP OPTIONS request.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse options(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.OPTIONS, requestClosure)
    }

    /**
     * Perform an HTTP OPTIONS request with the given input stream.
     *
     * @param request Request properties to use with the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse options(HttpRequest request, InputStream inputStream) throws IOException {
        return execute(HttpMethod.OPTIONS, request, inputStream)
    }

    /**
     * Perform an HTTP OPTIONS request with the given input stream.
     *
     * @param inputStream An {@link InputStream} containing the response body.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse options(InputStream inputStream, @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.OPTIONS, inputStream, requestClosure)
    }

    /**
     * Perform an HTTP OPTIONS request with the given entity.
     *
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     * @throws UnsupportedConversionException
     */
    @Override
    HttpResponse options(HttpRequest request, Object entity) throws IOException, UnsupportedConversionException {
        return execute(HttpMethod.OPTIONS, request, entity)
    }

    /**
     * Perform an HTTP OPTIONS request with the given entity.
     *
     * The entity will be converted if an appropriate {@link EntityWriter} can be found. If no
     * writer can be found, an {@link UnsupportedConversionException} will be thrown.
     *
     * @param entity Request entity.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     * @throws UnsupportedConversionException
     */
    @Override
    HttpResponse options(Object entity,
                         @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException, UnsupportedConversionException {
        return execute(HttpMethod.OPTIONS, entity, requestClosure)
    }

    /**
     * Perform an HTTP HEAD request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse head(HttpRequest request) throws IOException {
        return execute(HttpMethod.HEAD, request)
    }

    /**
     * Perform an HTTP HEAD request.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse head(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.HEAD, requestClosure)
    }

    /**
     * Perform an HTTP TRACE request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse trace(HttpRequest request) throws IOException {
        return execute(HttpMethod.TRACE, request)
    }

    /**
     * Perform an HTTP TRACE request.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse trace(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.TRACE, requestClosure)
    }

    /**
     * Adds a {@link HttpClientFilter} to the HTTP client.
     *
     * @param filter Filter instance to register with the client.
     * @return The object the method was called on.
     */
    @Override
    HttpClient addFilter(HttpClientFilter filter) {
        filterManager.add(filter)
        return this
    }

    /**
     * Unregisters a {@link HttpClientFilter} from the HTTP client.
     *
     * @param filter Filter instance to remove from the client.
     * @return The object the method was called on.
     */
    @Override
    HttpClient removeFilter(HttpClientFilter filter) {
        filterManager.remove(filter)
        return this
    }

    /**
     * Returns the list of all registered {@link HttpClientFilter} instances.
     *
     * @return The list of registered filter instances.
     */
    @Override
    List<HttpClientFilter> getFilters() {
        return filterManager.getAll()
    }

    /**
     * Removes all registered filters.
     *
     * @return The object the method was called on.
     */
    @Override
    HttpClient clearFilters() {
        filterManager.clear()
        return this
    }

    /**
     * Orchestrates making the HTTP request. Fires appropriate filter events and hands off to the implementation
     * to perform the actual HTTP request.
     *
     * @param method HTTP request method.
     * @param request {@link HttpRequest} object to configure the request.
     * @param entity Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     */
    protected HttpResponse run(HttpMethod method, HttpRequest request, InputStream entity) {
        HttpContext context = new HttpContext()
        context.setMethod(method)

        while (true) {
            HttpRequest newRequest = request.clone() as HttpRequest
            context.setRequest(newRequest)
            context.setResponse(null)

            filterManager.filterHttpRequest(context)

            // Requests that do not include an entity should still have their
            // {@link HttpClientLifecycleFilter#onRequest} method called. If the request does
            // contain an entity, it is the responsibility of the implementation to make a call
            // to {@link #filterOutputStream}.
            if (!entity) {
                filterManager.onRequest(context, null)
            }

            // Note that {@link HttpClientRequestEntityFilter#filterRequestEntity} and
            // {@link HttpClientLifecycleFilter#onRequest} should be initiated from the
            // client implementation, and will occur during the execution started below.
            HttpResponse response = doExecute(context, entity)

            context.setResponse(response)

            filterManager.filterHttpResponse(context)

            filterManager.onResponse(context)

            if (!filterManager.onRetry(context)) {
                filterManager.onComplete(context)
                return response
            }

            context.incrementRetries()
        }
    }

    /**
     * Filter the output stream.
     *
     * @param context HTTP request context.
     * @param outputStream Output stream of the request.
     */
    protected OutputStream filterOutputStream(HttpContext context, OutputStream outputStream) {
        outputStream = filterManager.filterRequestEntity(context, outputStream)

        filterManager.onRequest(context, outputStream)

        return outputStream
    }

    /**
     * Adds an entity converter to the factory.
     *
     * @param converter Converter to add to the factory.
     */
    void addEntityConverter(EntityConverter converter) {
        converterManager.add(converter)
    }

    /**
     * Returns the list of entity converters.
     *
     * @return List of entity converters.
     */
    List<EntityConverter> getEntityConverters() {
        return converterManager.getAll()
    }

    /**
     * Remove an entity converter.
     *
     * @param converter Entity converter to remove.
     */
    void removeEntityConverter(EntityConverter converter) {
        converterManager.remove(converter)
    }

    /**
     * Remove all entity converters.
     */
    void clearEntityConverters() {
        converterManager.clear()
    }

    /**
     * Create and return an all-trusting TLS {@link SSLContext}.
     *
     * @return An all-trusting TLS {@link SSLContext}.
     */
    protected SSLContext createTrustingSSLContext() {
        TrustManager[] certs = [new X509TrustManager() {
            X509Certificate[] getAcceptedIssuers() {
                null
            }

            void checkClientTrusted(X509Certificate[] certs, String authType) {}

            void checkServerTrusted(X509Certificate[] certs, String authType) {}
        }]

        SSLContext sslContext = SSLContext.getInstance('TLS')
        sslContext.init(null, certs, new SecureRandom())

        return sslContext
    }

    /**
     * Create and return an all-trusting {@link HostnameVerifier}.
     *
     * @return An all-trusting {@link HostnameVerifier}.
     */
    protected HostnameVerifier createTrustingHostnameVerifier() {
        return new HostnameVerifier() {
            boolean verify(String hostname, SSLSession session) {
                return true
            }
        }
    }
}
