package com.budjb.httprequests.jersey1

import com.budjb.httprequests.AbstractHttpClient
import com.budjb.httprequests.HttpMethod
import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.HttpResponse
import com.budjb.httprequests.exception.HttpResponseException
import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientHandlerException
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.api.client.WebResource
import com.sun.jersey.api.client.config.ClientConfig
import com.sun.jersey.api.client.config.DefaultClientConfig
import com.sun.jersey.client.urlconnection.HTTPSProperties

import javax.net.ssl.*
import java.security.SecureRandom
import java.security.cert.X509Certificate

/**
 * An implementation of HTTP client that uses the Jersey Client 1.x library.
 */
class JerseyHttpClient extends AbstractHttpClient {
    /**
     * Execute an HTTP request with the given method and request parameters and without a request entity.
     *
     * @param method
     * @param request
     * @return
     * @throws IOException
     */
    @Override
    HttpResponse execute(HttpMethod method, HttpRequest request) throws IOException {
        return doExecute(method, request)
    }

    /**
     * Executes an HTTP request with the given method, request parameters, and request entity.
     *
     * @param method
     * @param request
     * @param entity
     * @return
     * @throws IOException
     */
    @Override
    HttpResponse execute(HttpMethod method, HttpRequest request, byte[] entity) throws IOException {
        return doExecute(method, request, entity)
    }

    /**
     * Executes an HTTP request with the given method, request parameters, and request entity.
     *
     * @param method
     * @param request
     * @param entity
     * @return
     * @throws IOException
     */
    @Override
    HttpResponse execute(HttpMethod method, HttpRequest request, String entity) throws IOException {
        return doExecute(method, request, entity)
    }

    /**
     * Executes an HTTP request with the given method, request parameters, and input stream.
     *
     * @param method
     * @param request
     * @param inputStream
     * @return
     * @throws IOException
     */
    @Override
    HttpResponse execute(HttpMethod method, HttpRequest request, InputStream inputStream) throws IOException {
        return doExecute(method, request, inputStream)
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
     * Perform the request.
     *
     * @param method
     * @param request
     * @return
     */
    protected HttpResponse doExecute(HttpMethod method, HttpRequest request) {
        return doExecute(method, request, null)
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
    protected HttpResponse doExecute(HttpMethod method, HttpRequest request, Object entity) throws IOException {
        Client client = createClient(request)

        WebResource resource = client.resource(request.getUri())

        request.addQueryParameters(request.getQueryParameters())
        request.addHeaders(request.getHeaders())

        WebResource.Builder builder = resource.getRequestBuilder()

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
     * Builds an HttpResponse object from Jersey's ClientResponse.
     *
     * @param request
     * @param clientResponse
     * @return
     */
    protected HttpResponse buildResponse(HttpRequest request, ClientResponse clientResponse) {
        HttpResponse response = new HttpResponse()

        response.setStatus(clientResponse.getStatus())
        response.setEntity(clientResponse.getEntity(byte[]))
        response.setContentType(clientResponse.getType().toString())
        response.setHeaders(clientResponse.getHeaders())
        if (clientResponse.getType().getParameters().containsKey('charset')) {
            response.setCharset(clientResponse.getType().getParameters().get('charset'))
        }

        if (request.isThrowStatusExceptions() && clientResponse.getStatus() >= 400) {
            throw HttpResponseException.build(response)
        }

        return response
    }
}
