package com.budjb.httprequests.support

import com.budjb.httprequests.AbstractHttpClient
import com.budjb.httprequests.HttpMethod
import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.HttpResponse

class NullHttpClient extends AbstractHttpClient {
    /**
     * Response object that will be returned from any HTTP request.
     *
     * Use this field to effectively mock a request and response.
     */
    HttpResponse response

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
        return response
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
        return response
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
        return response
    }
}
