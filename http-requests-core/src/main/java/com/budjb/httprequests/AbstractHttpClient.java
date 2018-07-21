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
import com.budjb.httprequests.exception.UnsupportedConversionException;
import com.budjb.httprequests.filter.HttpClientFilterManager;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * A base class for HTTP clients that implements most of the functionality of the {@link HttpClient} interface.
 * <p>
 * Individual HTTP client library implementations should extend this class.
 */
public abstract class AbstractHttpClient implements HttpClient {
    /**
     * Converter manager.
     */
    private final EntityConverterManager converterManager;

    /**
     * Filter manager.
     */
    private final HttpClientFilterManager filterManager;

    /**
     * Constructor.
     *
     * @param entityConverterManager  Converter manager.
     * @param httpClientFilterManager Filter manager.
     */
    protected AbstractHttpClient(EntityConverterManager entityConverterManager, HttpClientFilterManager httpClientFilterManager) {
        this.converterManager = entityConverterManager;
        this.filterManager = httpClientFilterManager;
    }

    /**
     * Implements the logic to make an actual request with an HTTP client library.
     *
     * @param context    HTTP request context.
     * @param httpEntity An HTTP entity. May be {@code null} if no request entity is required.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException              When an underlying IO exception occurs.
     * @throws URISyntaxException       When a problem parsing a URI occurs.
     * @throws GeneralSecurityException When an issue with SSL configuration occurs.
     */
    protected abstract HttpResponse execute(HttpContext context, HttpEntity httpEntity) throws IOException, URISyntaxException, GeneralSecurityException;

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse execute(HttpMethod method, HttpRequest request) throws IOException {
        return run(method, request, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse execute(HttpMethod method, HttpRequest request, InputStream inputStream) throws IOException {
        return run(method, request, new HttpEntity(inputStream));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse execute(HttpMethod method, HttpRequest request, Object entity) throws IOException, UnsupportedConversionException {
        return execute(method, request, converterManager.write(entity, null, null));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse execute(HttpMethod method, HttpRequest request, HttpEntity entity) throws IOException {
        return run(method, request, entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse get(HttpRequest request) throws IOException {
        return execute(HttpMethod.GET, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse post(HttpRequest request) throws IOException {
        return execute(HttpMethod.POST, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse post(HttpRequest request, HttpEntity entity) throws IOException {
        return execute(HttpMethod.POST, request, entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse post(HttpRequest request, InputStream inputStream) throws IOException {
        return execute(HttpMethod.POST, request, inputStream);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse post(HttpRequest request, Object entity) throws IOException, UnsupportedConversionException {
        return execute(HttpMethod.POST, request, entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse put(HttpRequest request) throws IOException {
        return execute(HttpMethod.PUT, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse put(HttpRequest request, HttpEntity entity) throws IOException {
        return execute(HttpMethod.PUT, request, entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse put(HttpRequest request, InputStream inputStream) throws IOException {
        return execute(HttpMethod.PUT, request, inputStream);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse put(HttpRequest request, Object entity) throws IOException, UnsupportedConversionException {
        return execute(HttpMethod.PUT, request, entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse delete(HttpRequest request) throws IOException {
        return execute(HttpMethod.DELETE, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse options(HttpRequest request) throws IOException {
        return execute(HttpMethod.OPTIONS, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse options(HttpRequest request, HttpEntity entity) throws IOException {
        return execute(HttpMethod.OPTIONS, request, entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse options(HttpRequest request, InputStream inputStream) throws IOException {
        return execute(HttpMethod.OPTIONS, request, inputStream);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse options(HttpRequest request, Object entity) throws IOException, UnsupportedConversionException {
        return execute(HttpMethod.OPTIONS, request, entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse head(HttpRequest request) throws IOException {
        return execute(HttpMethod.HEAD, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse trace(HttpRequest request) throws IOException {
        return execute(HttpMethod.TRACE, request);
    }

    /**
     * Orchestrates making the HTTP request. Fires appropriate filter events and hands off to the implementation
     * to perform the actual HTTP request.
     *
     * @param method  HTTP request method.
     * @param request {@link HttpRequest} object to configure the request.
     * @param entity  Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     */
    private HttpResponse run(HttpMethod method, HttpRequest request, HttpEntity entity) throws IOException {
        HttpContext context = new HttpContext();

        filterManager.onStart(context);

        context.setMethod(method);

        HttpEntity originalEntity = null;
        boolean hasRetryFilters = filterManager.hasRetryFilters();

        // Requests whose client contains a retry filter must have their entity buffered.
        // If it is not, the retried request will either throw an error due to the entity
        // input stream being closed, or the entity will not actually transmit. So, requests
        // that could potentially be retried are automatically buffered so we can copy their
        // entities multiple times.
        if (entity != null && hasRetryFilters) {
            originalEntity = entity;
            originalEntity.buffer();
        }

        while (true) {
            if (entity != null && originalEntity != null) {
                entity = new HttpEntity(originalEntity.getInputStream(), originalEntity.getContentType(), originalEntity.getCharSet());
            }

            HttpRequest newRequest;
            try {
                newRequest = (HttpRequest) request.clone();
            }
            catch (CloneNotSupportedException e) {
                throw new RuntimeException(e); // TODO: a better exception
            }

            context.setRequest(newRequest);
            context.setResponse(null);

            filterManager.filterHttpRequest(newRequest);
            filterManager.onRequest(context);

            // Note that OutputStreamFilter#filter should be called by the client implementation,
            // and will occur during the execution started below.
            HttpResponse response;
            try {
                response = execute(context, entity);
            }
            catch (IOException e) {
                throw e;
            }
            catch (Exception e) {
                throw new RuntimeException(e); // TODO: a better exception
            }

            context.setResponse(response);
            filterManager.filterHttpResponse(response);
            filterManager.onResponse(context);

            if (!filterManager.isRetryRequired(context)) {
                filterManager.onComplete(context);
                return context.getResponse();
            }

            context.incrementRetries();
        }
    }

    /**
     * Returns the entity converter manager.
     *
     * @return The entity converter manager.
     */
    @Override
    public EntityConverterManager getConverterManager() {
        return converterManager;
    }

    /**
     * Returns the filter manager.
     *
     * @return The filter manager.
     */
    @Override
    public HttpClientFilterManager getFilterManager() {
        return filterManager;
    }

    /**
     * Create and return an all-trusting TLS {@link SSLContext}.
     *
     * @return An all-trusting TLS {@link SSLContext}.
     * @throws GeneralSecurityException When an issue with SSL configuration occurs.
     */
    protected SSLContext createTrustingSSLContext() throws GeneralSecurityException {
        TrustManager[] certs = {new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, certs, new SecureRandom());

        return sslContext;
    }

    /**
     * Create and return an all-trusting {@link HostnameVerifier}.
     *
     * @return An all-trusting {@link HostnameVerifier}.
     */
    protected HostnameVerifier createTrustingHostnameVerifier() {
        return (hostname, session) -> true;
    }
}