package com.budjb.httprequests.jersey1

import com.budjb.httprequests.*
import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientHandlerException
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.api.client.WebResource
import com.sun.jersey.api.client.config.ClientConfig
import com.sun.jersey.api.client.config.DefaultClientConfig
import com.sun.jersey.api.representation.Form
import com.sun.jersey.client.urlconnection.HTTPSProperties

import javax.net.ssl.*
import java.security.SecureRandom
import java.security.cert.X509Certificate

/**
 * An implementation of HTTP client that uses the Jersey Client 1.x library.
 */
class JerseyHttpClient extends AbstractHttpClient {
    /**
     * Perform the request.
     *
     * @param method
     * @param request
     * @return
     */
    HttpResponse doExecute(HttpMethod method, HttpRequest request) throws IOException {
        return performRequest(method, request, null)
    }

    /**
     * Implements the logic to make an actual request with an HTTP client library.
     *
     * @param method
     * @param request
     * @param entity
     * @return
     */
    @Override
    HttpResponse doExecute(HttpMethod method, HttpRequest request, byte[] entity) throws IOException {
        return performRequest(method, request, entity)
    }

    /**
     * Implements the logic to make an actual request with an HTTP client library.
     *
     * @param method
     * @param request
     * @param stream
     * @return
     */
    @Override
    HttpResponse doExecute(HttpMethod method, HttpRequest request, InputStream stream) throws IOException {
        return performRequest(method, request, stream)
    }

    /**
     * Implements the logic to make an actual request with an HTTP client library.
     *
     * @param method
     * @param request
     * @param form
     * @return
     */
    @Override
    HttpResponse doExecute(HttpMethod method, HttpRequest request, FormData form) throws IOException {
        return performRequest(method, request, form.getFields() as Form)
    }

    /**
     * Perform the request.
     *
     * @param method
     * @param request
     * @param entity
     * @return
     * @throws IOException
     */
    HttpResponse performRequest(HttpMethod method, HttpRequest request, Object entity) throws IOException {
        Client client = createClient(request)

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

        if (request.getContentType()) {
            String contentType = request.getContentType()
            if (request.getCharset()) {
                contentType += ";charset=${request.getContentType()}"
            }
            builder = builder.type(contentType)
        }

        if (request.getAccept()) {
            builder = builder.accept(request.getAccept())
        }

        ClientResponse response
        try {
            if (entity != null) {
                response = builder.method(method.toString(), ClientResponse, entity)
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

        return buildResponse(request, response)
    }

    /**
     * Creates the Jersey Client instance.
     *
     * @return Configured jersey client
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
     * Builds an HttpResponse object from Jersey's ClientResponse.
     *
     * @param request
     * @param clientResponse
     * @return
     */
    protected HttpResponse buildResponse(HttpRequest request, ClientResponse clientResponse) {
        HttpResponse response
        if (request.isStreamingResponse()) {
            response = new StreamingHttpResponse()
            response.setInputStream(clientResponse.getEntityInputStream())
        }
        else {
            response = new HttpResponse()
            response.setEntity(clientResponse.getEntity(byte[]))
        }

        response.setStatus(clientResponse.getStatus())
        response.setContentType(clientResponse.getType()?.toString())
        response.setHeaders(clientResponse.getHeaders())
        if (clientResponse.getType()?.getParameters()?.containsKey('charset')) {
            response.setCharset(clientResponse.getType().getParameters().get('charset'))
        }

        return response
    }
}
