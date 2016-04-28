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
package com.budjb.httprequests.jersey1

import com.budjb.httprequests.*
import com.sun.jersey.api.client.*
import com.sun.jersey.api.client.config.ClientConfig
import com.sun.jersey.api.client.config.DefaultClientConfig
import com.sun.jersey.api.client.filter.ClientFilter
import com.sun.jersey.api.client.filter.LoggingFilter
import com.sun.jersey.client.urlconnection.HTTPSProperties
import groovy.util.logging.Slf4j

import javax.net.ssl.*
import javax.ws.rs.core.MediaType
import java.security.SecureRandom
import java.security.cert.X509Certificate

/**
 * An implementation of {@link HttpClient} that uses the Jersey Client 1.x library.
 */
@Slf4j
class JerseyHttpClient extends AbstractHttpClient {
    /**
     * Implements the logic to make an actual request with an HTTP client library.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body. May be <code>null</code>.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    protected HttpResponse doExecute(HttpMethod method, HttpRequest request, InputStream inputStream) throws IOException {
        Client client = createClient(request)

        client.addFilter(createClientFilter())

        ByteArrayOutputStream logStream = null
        if (request.isLogConversation()) {
            logStream = new ByteArrayOutputStream()
            client.addFilter(new LoggingFilter(new PrintStream(logStream)))
        }

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

        if (request.getFullContentType()) {
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

        if (logStream) {
            log.debug("HTTP Conversation:\n${logStream.toString()}")
        }

        return buildResponse(request, response)
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

        TrustManager[] certs = [new X509TrustManager() {
            X509Certificate[] getAcceptedIssuers() {
                null
            }

            void checkClientTrusted(X509Certificate[] certs, String authType) {}

            void checkServerTrusted(X509Certificate[] certs, String authType) {}
        }]

        SSLContext ctx = SSLContext.getInstance("TLS")
        ctx.init(null, certs, new SecureRandom())

        ClientConfig config = new DefaultClientConfig()
        config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(
            new HostnameVerifier() {
                boolean verify(String hostname, SSLSession session) {
                    return true
                }
            },
            ctx
        ))

        return Client.create(config)
    }

    /**
     * Builds an {@link HttpResponse} object from Jersey's {@link ClientResponse}.
     *
     * @param request Request properties to use with the HTTP request.
     * @param clientResponse Jersey response object to build the {@link HttpResponse} object from.
     * @return A fully configured {@HttpResponse} object representing the response of the request.
     */
    protected HttpResponse buildResponse(HttpRequest request, ClientResponse clientResponse) {
        HttpResponse response = createResponse(request)

        response.setStatus(clientResponse.getStatus())
        response.setHeaders(clientResponse.getHeaders())

        if (clientResponse.getType()) {
            MediaType type = clientResponse.getType()
            String contentType = type.toString()
            response.setContentType(contentType.substring(0, contentType.indexOf(';')))
            response.setCharset(type.getParameters()?.get('charset'))
        }

        if (clientResponse.hasEntity()) {
            response.setEntity(clientResponse.getEntityInputStream())
        }

        return response
    }

    /**
     * Creates the client filter that allows the request {@link OutputStream} to be filtered.
     *
     * @return A new client filter.
     */
    protected ClientFilter createClientFilter() {
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
                            return filterOutputStream(out)
                        }
                    })
                }

                return getNext().handle(clientRequest)
            }
        }
    }
}
