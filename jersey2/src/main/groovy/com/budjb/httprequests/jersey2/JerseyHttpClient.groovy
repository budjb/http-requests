package com.budjb.httprequests.jersey2

import com.budjb.httprequests.*
import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.client.ClientProperties
import org.glassfish.jersey.filter.LoggingFilter

import javax.net.ssl.*
import javax.ws.rs.ProcessingException
import javax.ws.rs.client.*
import javax.ws.rs.core.Form
import javax.ws.rs.core.Response
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.logging.Logger

class JerseyHttpClient extends AbstractHttpClient {
    /**
     * Implements the logic to make an actual request with an HTTP client library.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
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
        return performRequest(method, request, Entity.entity(entity, request.getFullContentType()))
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
        return performRequest(method, request, Entity.entity(inputStream, request.getFullContentType()))
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
        return performRequest(method, request, Entity.form(buildForm(form)))
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
     * Performs the actual HTTP request.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param entity Entity of the request. Can be null if there is no entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    protected HttpResponse performRequest(HttpMethod method, HttpRequest request, Entity<?> entity) throws IOException {
        Client client = createClient(request)

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
        response.setContentType(clientResponse.getMediaType()?.getType())
        response.setCharset((clientResponse.getMediaType()?.getParameters()?.get('charset')))

        if (clientResponse.hasEntity()) {
            response.setInputStream(clientResponse.getEntity() as InputStream)
        }

        return response
    }

    /**
     * Create a {@link Form} object from the given {@link FormData} object.
     *
     * @param formData Form data to convert.
     * @return Converted form data.
     */
    protected Form buildForm(FormData formData) {
        Form form = new Form()

        formData.getFields().each { key, values ->
            values.each { value ->
                form.param(key, value)
            }
        }

        return form
    }
}
