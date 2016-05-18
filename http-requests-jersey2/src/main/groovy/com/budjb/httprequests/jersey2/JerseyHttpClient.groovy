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
package com.budjb.httprequests.jersey2

import com.budjb.httprequests.*
import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.client.ClientProperties

import javax.ws.rs.ProcessingException
import javax.ws.rs.WebApplicationException
import javax.ws.rs.client.*
import javax.ws.rs.core.Response
import javax.ws.rs.ext.WriterInterceptor
import javax.ws.rs.ext.WriterInterceptorContext

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

        client = client.register(createWriterInterceptor(context))

        client = client.property(ClientProperties.CONNECT_TIMEOUT, request.getConnectionTimeout())
        client = client.property(ClientProperties.READ_TIMEOUT, request.getReadTimeout())
        client = client.property(ClientProperties.FOLLOW_REDIRECTS, request.isFollowRedirects())
        client = client.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true)

        WebTarget target = client.target(request.getUri())

        request.getQueryParameters().each { name, values ->
            if (values instanceof Collection) {
                values.each { value ->
                    target = target.queryParam(name, value.toString())
                }
            }
            else {
                target = target.queryParam(name, values.toString())
            }
        }

        Invocation.Builder builder = target.request()

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

        if (request.getAccept()) {
            builder = builder.accept(request.getAccept())
        }

        Entity<InputStream> entity = null
        if (inputStream) {
            entity = Entity.entity(inputStream, request.getFullContentType())
        }

        Response clientResponse
        try {
            if (entity != null) {
                clientResponse = builder.method(method.toString(), entity, Response)
            }
            else {
                clientResponse = builder.method(method.toString(), Response)
            }
        }
        catch (ProcessingException e) {
            if (e.getCause() && e.getCause() instanceof IOException) {
                throw e.getCause()
            }
            throw e
        }

        return new JerseyHttpResponse(request, converterManager, clientResponse)
    }

    /**
     * Creates a new Jersey {@link Client} instance, configured by the request properties.
     *
     * @param request Used to configure the {@link Client} instance.
     * @return A new Jersey {@link Client} instance.
     */
    protected Client createClient(HttpRequest request) {
        ClientBuilder builder = ClientBuilder.newBuilder()

        if (!request.isSslValidated()) {
            builder = builder.hostnameVerifier(createTrustingHostnameVerifier()).sslContext(createTrustingSSLContext())
        }

        ClientConfig clientConfig = new ClientConfig()

        return builder.withConfig(clientConfig).build()
    }

    /**
     * Creates a writer interceptor so that the {@link OutputStream} can be filtered.
     *
     * @param context HTTP request context.
     * @return A new writer interceptor.
     */
    protected WriterInterceptor createWriterInterceptor(HttpContext context) {
        return new WriterInterceptor() {
            @Override
            void aroundWriteTo(WriterInterceptorContext interceptorContext) throws IOException, WebApplicationException {
                interceptorContext.setOutputStream(filterOutputStream(context, interceptorContext.getOutputStream()))
                interceptorContext.proceed()
            }
        }
    }
}
