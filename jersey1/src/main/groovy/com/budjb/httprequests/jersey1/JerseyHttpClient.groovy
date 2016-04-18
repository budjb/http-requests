package com.budjb.httprequests.jersey1

import com.budjb.httprequests.*
import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientHandlerException
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.api.client.WebResource
import com.sun.jersey.api.client.config.ClientConfig
import com.sun.jersey.api.client.config.DefaultClientConfig
import com.sun.jersey.api.client.filter.LoggingFilter
import com.sun.jersey.api.representation.Form
import com.sun.jersey.client.urlconnection.HTTPSProperties
import groovy.util.logging.Slf4j

import javax.net.ssl.*
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
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    protected HttpResponse doExecute(HttpMethod method, HttpRequest request) throws IOException {
        return performRequest(method, request, null)
    }

    /**
     * Implements the logic to make an actual request with an HTTP client library.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param entity A byte array to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    protected HttpResponse doExecute(HttpMethod method, HttpRequest request, byte[] entity) throws IOException {
        return performRequest(method, request, entity)
    }

    /**
     * Implements the logic to make an actual request with an HTTP client library.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    protected HttpResponse doExecute(HttpMethod method, HttpRequest request, InputStream inputStream) throws IOException {
        return performRequest(method, request, inputStream)
    }

    /**
     * Implements the logic to make an actual request with an HTTP client library.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param form Form data to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    protected HttpResponse doExecute(HttpMethod method, HttpRequest request, FormData form) throws IOException {
        return performRequest(method, request, form.getFields() as Form)
    }

    /**
     * Perform the request.
     *
     * @param method HTTP method tuse with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param entity Entity of the request. Can be null if there is no entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    protected HttpResponse performRequest(HttpMethod method, HttpRequest request, Object entity) throws IOException {
        Client client = createClient(request)

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

        if (request.getContentType()) {
            String contentType = request.getContentType()
            if (request.getCharset()) {
                contentType += ";charset=${request.getCharset()}"
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
        return new HttpResponse(
            request,
            clientResponse.getStatus(),
            clientResponse.getHeaders(),
            clientResponse.hasEntity() ? clientResponse.getEntityInputStream() : null
        )
    }
}
