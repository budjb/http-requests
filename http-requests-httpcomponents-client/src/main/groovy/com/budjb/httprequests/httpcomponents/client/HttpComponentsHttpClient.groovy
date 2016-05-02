package com.budjb.httprequests.httpcomponents.client

import com.budjb.httprequests.AbstractHttpClient
import com.budjb.httprequests.HttpMethod
import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.HttpResponse
import org.apache.http.HttpEntity
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.*
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.AbstractHttpEntity
import org.apache.http.entity.InputStreamEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.ContentEncodingHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.HttpClients

class HttpComponentsHttpClient extends AbstractHttpClient {
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
        CloseableHttpClient client = createClient(request)

        URIBuilder uriBuilder = new URIBuilder(request.getUri())
        request.getQueryParameters().each { key, values ->
            if (values instanceof Collection) {
                values.each { value ->
                    uriBuilder.addParameter(key, value)
                }
            }
            else {
                uriBuilder.addParameter(key, values as String)
            }
        }

        HttpRequestBase httpRequest = createHttpRequest(method, uriBuilder.build())

        request.getHeaders().each { key, values ->
            if (values instanceof Collection) {
                values.each { value ->
                    httpRequest.addHeader(key, value)
                }
            }
            else {
                httpRequest.addHeader(key, values as String)
            }
        }

        if (request.getAccept()) {
            httpRequest.setHeader('Accept', request.getAccept())
        }

        if (inputStream && httpRequest instanceof HttpEntityEnclosingRequestBase) {
            HttpEntity entity = new InputStreamEntity(inputStream) {
                @Override
                public void writeTo(final OutputStream outstream) throws IOException {
                    OutputStream filtered = filterOutputStream(outstream)
                    super.writeTo(filtered)

                    // This is a bit of a hack since HTTP components client does not give
                    // applications the ability to change the OutputStream being written to
                    // inside the library. For compression to work correctly, close() must
                    // be called. Ultimately, the next thing the client does after the call
                    // to this method is to close the original stream anyway.
                    filtered.close()
                }
            }
            entity.setContentType(request.getFullContentType())
            httpRequest.setEntity(entity)
        }

        CloseableHttpResponse clientResponse = client.execute(httpRequest)

        HttpResponse response = createResponse(request)
        response.setStatus(clientResponse.getStatusLine().getStatusCode())
        clientResponse.getAllHeaders().each {
            response.addHeader(it.getName(), it.getValue())
        }

        if (clientResponse.getEntity() && clientResponse.getEntity().getContentLength() != 0) {
            response.setEntity(clientResponse.getEntity().getContent())
            response.setContentType(clientResponse.getEntity().getContentType()?.getValue())
        }

        return response
    }

    /**
     * Creates a client.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A new http client.
     */
    protected CloseableHttpClient createClient(HttpRequest request) {
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(request.getConnectionTimeout())
            .setSocketTimeout(request.getReadTimeout())
            .setRedirectsEnabled(request.isFollowRedirects())
            .build()

        HttpClientBuilder builder = HttpClients.custom()

        builder.setDefaultRequestConfig(requestConfig)

        if (!request.isSslValidated()) {
            builder.setSSLContext(createTrustingSSLContext())
            builder.setSSLHostnameVerifier(createTrustingHostnameVerifier())
        }

        return builder.build()
    }

    /**
     * Creates the appropriate HTTP request type for the given {@link HttpMethod}.
     *
     * @param method HTTP method of the request.
     * @param uri URI of the request.
     * @return The appropriate HTTP request type.
     */
    protected HttpRequestBase createHttpRequest(HttpMethod method, URI uri) {
        switch (method) {
            case HttpMethod.GET:
                return new HttpGet(uri)

            case HttpMethod.POST:
                return new HttpPost(uri)

            case HttpMethod.PUT:
                return new HttpPut(uri)

            case HttpMethod.DELETE:
                return new HttpDelete(uri)

            case HttpMethod.HEAD:
                return new HttpHead(uri)

            case HttpMethod.OPTIONS:
                return new HttpOptions(uri)

            case HttpMethod.TRACE:
                return new HttpTrace(uri)

            default:
                throw new IllegalArgumentException("HTTP method ${method.toString()} is unsupported")
        }
    }
}
