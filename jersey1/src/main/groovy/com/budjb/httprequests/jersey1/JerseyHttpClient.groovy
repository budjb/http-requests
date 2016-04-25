package com.budjb.httprequests.jersey1

import com.budjb.httprequests.*
import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientHandlerException
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.api.client.WebResource
import com.sun.jersey.api.client.config.ClientConfig
import com.sun.jersey.api.client.config.DefaultClientConfig
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
}
