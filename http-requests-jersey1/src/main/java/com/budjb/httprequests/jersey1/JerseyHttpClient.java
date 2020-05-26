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
package com.budjb.httprequests.jersey1;

import com.budjb.httprequests.*;
import com.budjb.httprequests.converter.EntityConverterManager;
import com.budjb.httprequests.filter.HttpClientFilterProcessor;
import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * An implementation of {@link HttpClient} that uses the Jersey Client 1.x library.
 */
public class JerseyHttpClient extends AbstractHttpClient {
    /**
     * Constructor.
     *
     * @param converterManager Entity converter manager.
     */
    JerseyHttpClient(EntityConverterManager converterManager) {
        super(converterManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected HttpResponse execute(HttpContext context, HttpEntity entity, HttpClientFilterProcessor filterProcessor) throws IOException, GeneralSecurityException {
        HttpRequest request = context.getRequest();
        HttpMethod method = context.getMethod();

        Client client = createClient(request);

        client.addFilter(createClientFilter(filterProcessor));

        client.setReadTimeout(request.getReadTimeout());
        client.setConnectTimeout(request.getConnectionTimeout());
        client.setFollowRedirects(request.isFollowRedirects());

        WebResource resource = client.resource(request.getUri());
        resource = applyQueryParameters(resource, request.getQueryParameters());

        WebResource.Builder builder = resource.getRequestBuilder();

        builder = applyHeaders(builder, request.getHeaders());

        if (entity != null) {
            builder = builder.type(entity.getFullContentType());
        }

        ClientResponse response;
        try {
            if (entity != null) {
                response = builder.method(method.toString(), ClientResponse.class, entity.getInputStream());
            }
            else {
                response = builder.method(method.toString(), ClientResponse.class);
            }
        }
        catch (ClientHandlerException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw e;
        }

        return new JerseyHttpResponse(request, getConverterManager(), response);
    }

    /**
     * Creates the Jersey {@link Client} instance.
     *
     * @return Configured Jersey {@link Client}.
     */
    private Client createClient(HttpRequest request) throws GeneralSecurityException {
        if (request.isSslValidated()) {
            return Client.create();
        }

        ClientConfig config = new DefaultClientConfig();
        config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(
            createTrustingHostnameVerifier(),
            createTrustingSSLContext()
        ));

        return Client.create(config);
    }

    /**
     * Creates the client filter that allows the request {@link OutputStream} to be filtered.
     *
     * @return A new client filter.
     */
    private ClientFilter createClientFilter(HttpClientFilterProcessor filterProcessor) {
        return new ClientFilter() {
            @Override
            public ClientResponse handle(ClientRequest clientRequest) throws ClientHandlerException {
                if (clientRequest.getEntity() != null) {
                    clientRequest.setAdapter(new AbstractClientRequestAdapter(clientRequest.getAdapter()) {
                        @Override
                        public OutputStream adapt(ClientRequest rq, OutputStream out) {
                            return filterProcessor.filterOutputStream(out);
                        }
                    });
                }

                return getNext().handle(clientRequest);
            }
        };
    }

    /**
     * Applies the request's query parameters to the web resource.
     *
     * @param resource        Web resource.
     * @param queryParameters Query parameters.
     * @return The new web resource with query parameters applied.
     */
    private WebResource applyQueryParameters(WebResource resource, MultiValuedMap queryParameters) {
        for (String name : queryParameters.keySet()) {
            List<String> values = queryParameters.get(name);

            if (values.size() == 0) {
                resource = resource.queryParam(name, "");
            }
            else {
                for (String value : queryParameters.get(name)) {
                    resource = resource.queryParam(name, value);
                }
            }
        }

        return resource;
    }

    /**
     * Applies the request's headers to the web resource builder.
     *
     * @param builder Web resource builder.
     * @param headers Headers.
     * @return The web resource builder with headers applied.
     */
    private WebResource.Builder applyHeaders(WebResource.Builder builder, MultiValuedMap headers) {
        for (String name : headers.keySet()) {
            List<String> values = headers.get(name);

            if (values.size() == 0) {
                builder = builder.header(name, "");
            }
            else {
                for (String value : headers.get(name)) {
                    builder = builder.header(name, value);
                }
            }
        }

        return builder;
    }
}
