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
package com.budjb.httprequests.jersey1

import com.budjb.httprequests.*
import com.sun.jersey.api.client.*
import com.sun.jersey.api.client.config.ClientConfig
import com.sun.jersey.api.client.config.DefaultClientConfig
import com.sun.jersey.api.client.filter.ClientFilter
import com.sun.jersey.client.urlconnection.HTTPSProperties
import groovy.util.logging.Slf4j

/**
 * An implementation of {@link HttpClient} that uses the Jersey Client 1.x library.
 */
@Slf4j
class JerseyHttpClient extends AbstractHttpClient {
    /**
     * Implements the logic to make an actual request with an HTTP client library.
     *
     * @param context HTTP request context.
     * @param inputStream An {@link InputStream} containing the response body. May be <code>null</code>.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    protected HttpResponse doExecute(HttpContext context, InputStream inputStream) throws IOException {
        HttpRequest request = context.getRequest()
        HttpMethod method = context.getMethod()

        Client client = createClient(request)

        client.addFilter(createClientFilter(context))

        client.setReadTimeout(request.getReadTimeout())
        client.setConnectTimeout(request.getConnectionTimeout())
        client.setFollowRedirects(request.isFollowRedirects())

        WebResource resource = client.resource(request.getUri())

        request.getQueryParameters().each { name, values ->
            if (values instanceof Collection) {
                values.each { value ->
                    resource = resource.queryParam(name, value.toString())
                }
            }
            else {
                resource = resource.queryParam(name, values.toString())
            }
        }

        WebResource.Builder builder = resource.getRequestBuilder()

        request.getHeaders().each { name, values ->
            if (values instanceof Collection) {
                values.each { value ->
                    builder = builder.header(name, value.toString())
                }
            }
            else {
                builder = builder.header(name, values.toString())
            }
        }

        if (inputStream && request.getFullContentType()) {
            builder = builder.type(request.getFullContentType())
        }

        if (request.getAccept()) {
            builder = builder.accept(request.getAccept())
        }

        ClientResponse response
        try {
            if (inputStream != null) {
                response = builder.method(method.toString(), ClientResponse, inputStream)
            }
            else {
                response = builder.method(method.toString(), ClientResponse)
            }
        }
        catch (ClientHandlerException e) {
            if (e.getCause() instanceof IOException) {
                throw e.getCause()
            }
            throw e
        }

        return new JerseyHttpResponse(request, converterManager, response)
    }

    /**
     * Creates the Jersey {@link Client} instance.
     *
     * @return Configured Jersey {@link Client}.
     */
    protected Client createClient(HttpRequest request) {
        if (request.isSslValidated()) {
            return Client.create()
        }

        ClientConfig config = new DefaultClientConfig()
        config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(
            createTrustingHostnameVerifier(),
            createTrustingSSLContext()
        ))

        return Client.create(config)
    }

    /**
     * Creates the client filter that allows the request {@link OutputStream} to be filtered.
     *
     * @param context HTTP request context.
     * @return A new client filter.
     */
    protected ClientFilter createClientFilter(HttpContext context) {
        return new ClientFilter() {
            @Override
            ClientResponse handle(ClientRequest clientRequest) throws ClientHandlerException {
                if (clientRequest.getEntity()) {
                    clientRequest.setAdapter(new AbstractClientRequestAdapter(clientRequest.getAdapter()) {
                        /**
                         * Adapt the output stream of the client request.
                         *
                         * @param rq the client request
                         * @param out the output stream to write the request entity.
                         * @return the adapted output stream to write the request entity.
                         * @throws java.io.IOException
                         */
                        @Override
                        OutputStream adapt(ClientRequest rq, OutputStream out) throws IOException {
                            return filterOutputStream(context, out)
                        }
                    })
                }

                return getNext().handle(clientRequest)
            }
        }
    }
}
