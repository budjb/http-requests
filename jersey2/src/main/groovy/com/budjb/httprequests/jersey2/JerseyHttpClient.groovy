package com.budjb.httprequests.jersey2

import com.budjb.httprequests.AbstractHttpClient
import com.budjb.httprequests.HttpMethod
import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.HttpResponse
import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.client.ClientProperties
import org.glassfish.jersey.filter.LoggingFilter

import javax.net.ssl.*
import javax.ws.rs.ProcessingException
import javax.ws.rs.WebApplicationException
import javax.ws.rs.client.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.WriterInterceptor
import javax.ws.rs.ext.WriterInterceptorContext
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.logging.Logger

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

        client = client.register(createWriterInterceptor())

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

        Entity<InputStream> entity = Entity.entity(inputStream, request.getFullContentType())

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

        return buildResponse(request, clientResponse)
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
            TrustManager[] certs = [new X509TrustManager() {
                X509Certificate[] getAcceptedIssuers() {
                    null
                }

                void checkClientTrusted(X509Certificate[] certs, String authType) {}

                void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }]

            SSLContext sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, certs, new SecureRandom())

            HostnameVerifier verifier = new HostnameVerifier() {
                boolean verify(String hostname, SSLSession session) {
                    return true
                }
            }

            builder = builder.hostnameVerifier(verifier).sslContext(sslContext)
        }

        ClientConfig clientConfig = new ClientConfig()
        if (request.isLogConversation()) {
            clientConfig.register(new LoggingFilter(Logger.getLogger(getClass().getName()), true))
        }

        return builder.withConfig(clientConfig).build()
    }

    /**
     * Builds an {@link HttpResponse} object from Jersey's {@link Response}.
     *
     * @param request Request properties to use with the HTTP request.
     * @param clientResponse Jersey response object to build the {@link HttpResponse} object from.
     * @return A fully configured {@HttpResponse} object representing the response of the request.
     */
    protected HttpResponse buildResponse(HttpRequest request, Response clientResponse) {
        HttpResponse response = createResponse(request)

        response.setStatus(clientResponse.getStatus())
        response.setHeaders(clientResponse.getHeaders())

        if (clientResponse.getMediaType()) {
            MediaType type = clientResponse.getMediaType()
            String contentType = type.toString()
            response.setContentType(contentType.substring(0, contentType.indexOf(';')))
            response.setCharset(type.getParameters()?.get('charset'))
        }

        if (clientResponse.hasEntity()) {
            response.setEntity(clientResponse.getEntity() as InputStream)
        }

        return response
    }

    /**
     * Creates a writer interceptor so that the {@link OutputStream} can be filtered.
     *
     * @return A new writer interceptor.
     */
    protected WriterInterceptor createWriterInterceptor() {
        return new WriterInterceptor() {
            @Override
            void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
                context.setOutputStream(filterOutputStream(context.getOutputStream()))
                context.proceed()
            }
        }
    }
}
