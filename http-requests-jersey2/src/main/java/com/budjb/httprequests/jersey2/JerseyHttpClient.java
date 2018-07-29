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
package com.budjb.httprequests.jersey2;

import com.budjb.httprequests.*;
import com.budjb.httprequests.converter.EntityConverterManager;
import com.budjb.httprequests.filter.HttpClientFilterProcessor;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.WriterInterceptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

public class JerseyHttpClient extends AbstractHttpClient {
    /**
     * Constructor.
     *
     * @param entityConverterManager Converter manager.
     */
    JerseyHttpClient(EntityConverterManager entityConverterManager) {
        super(entityConverterManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected HttpResponse execute(HttpContext context, HttpEntity httpEntity, HttpClientFilterProcessor filterProcessor) throws IOException, GeneralSecurityException {
        HttpRequest request = context.getRequest();
        HttpMethod method = context.getMethod();

        Client client = createClient(request);

        client = client.register(createWriterInterceptor(filterProcessor));

        client = client.property(ClientProperties.CONNECT_TIMEOUT, request.getConnectionTimeout());
        client = client.property(ClientProperties.READ_TIMEOUT, request.getReadTimeout());
        client = client.property(ClientProperties.FOLLOW_REDIRECTS, request.isFollowRedirects());
        client = client.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);

        WebTarget target = client.target(request.getUri());
        target = applyQueryParameters(target, request.getQueryParameters());

        Invocation.Builder builder = target.request();

        builder = applyHeaders(builder, request.getHeaders());

        Entity<InputStream> entity = null;
        if (httpEntity != null) {
            entity = Entity.entity(httpEntity.getInputStream(), MediaType.valueOf(httpEntity.getFullContentType()));
        }

        Response clientResponse;
        try {
            if (entity != null) {
                clientResponse = builder.method(method.toString(), entity, Response.class);
            }
            else {
                clientResponse = builder.method(method.toString(), Response.class);
            }
        }
        catch (ProcessingException e) {
            if (e.getCause() != null && e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw e;
        }

        return new JerseyHttpResponse(request, getConverterManager(), clientResponse);
    }

    /**
     * Creates a new Jersey {@link Client} instance, configured by the request properties.
     *
     * @param request Used to configure the {@link Client} instance.
     * @return A new Jersey {@link Client} instance.
     */
    private Client createClient(HttpRequest request) throws GeneralSecurityException {
        ClientBuilder builder = ClientBuilder.newBuilder();

        if (!request.isSslValidated()) {
            builder = builder.hostnameVerifier(createTrustingHostnameVerifier()).sslContext(createTrustingSSLContext());
        }

        ClientConfig clientConfig = new ClientConfig();

        return builder.withConfig(clientConfig).build();
    }

    /**
     * Creates a writer interceptor so that the {@link OutputStream} can be filtered.
     *
     * @param filterProcessor Filter processor.
     * @return A new writer interceptor.
     */
    private WriterInterceptor createWriterInterceptor(HttpClientFilterProcessor filterProcessor) {
        return interceptorContext -> {
            interceptorContext.setOutputStream(filterProcessor.filterOutputStream(interceptorContext.getOutputStream()));
            interceptorContext.proceed();
        };
    }

    /**
     * Applies the request's query parameters to the web target.
     *
     * @param target          Web target.
     * @param queryParameters Query parameters.
     * @return The new web resource with query parameters applied.
     */
    private WebTarget applyQueryParameters(WebTarget target, MultiValuedMap queryParameters) {
        for (String name : queryParameters.keySet()) {
            for (String value : queryParameters.get(name)) {
                target = target.queryParam(name, value);
            }
        }

        return target;
    }

    /**
     * Applies the request's headers to the invocation builder.
     *
     * @param builder Invocation builder.
     * @param headers Headers.
     * @return The invocation builder with headers applied.
     */
    private Invocation.Builder applyHeaders(Invocation.Builder builder, MultiValuedMap headers) {
        for (String name : headers.keySet()) {
            for (String value : headers.get(name)) {
                builder = builder.header(name, value);
            }
        }

        return builder;
    }
}
