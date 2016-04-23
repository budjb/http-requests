package com.budjb.httprequests.support

import com.budjb.httprequests.AbstractHttpClient
import com.budjb.httprequests.FormData
import com.budjb.httprequests.HttpMethod
import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.HttpResponse

class NullHttpClient extends AbstractHttpClient {
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
        return null
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
        return null
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
        return null
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
        return null
    }
}
