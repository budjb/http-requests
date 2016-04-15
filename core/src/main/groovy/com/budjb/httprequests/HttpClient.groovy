package com.budjb.httprequests

import com.budjb.httprequests.listener.HttpClientListener

/**
 * An interface that describes the common structure and methods of an HTTP client.
 *
 * Various listener classes are supported.
 */
interface HttpClient {
    /**
     * Execute an HTTP request with the given method and request parameters and without a request entity.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse execute(HttpMethod method, HttpRequest request) throws IOException

    /**
     * Executes an HTTP request with the given method and closure to configure the request without a request entity.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse execute(HttpMethod method, @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException

    /**
     * Executes an HTTP request with the given method, request parameters, and request entity.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param entity A byte array to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse execute(HttpMethod method, HttpRequest request, byte[] entity) throws IOException

    /**
     * Executes an HTTP request with the given method, closure to configure the request, and request entity.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param requestClosure Closure that configures the request.
     * @param entity A byte array to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse execute(HttpMethod method, @DelegatesTo(HttpRequest) Closure requestClosure, byte[] entity) throws IOException

    /**
     * Executes an HTTP request with the given method, request parameters, and request entity.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param entity A <code>String</code> to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse execute(HttpMethod method, HttpRequest request, String entity) throws IOException

    /**
     * Executes an HTTP request with the given method, closure to configure the request, and request entity.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param requestClosure Closure that configures the request.
     * @param entity A <code>String</code> to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse execute(HttpMethod method, @DelegatesTo(HttpRequest) Closure requestClosure, String entity) throws IOException

    /**
     * Executes an HTTP request with the given method, request parameters, and input stream.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse execute(HttpMethod method, HttpRequest request, InputStream inputStream) throws IOException

    /**
     * Executes an HTTP request with the given method, closure to configure the request, and input stream.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param requestClosure Closure that configures the request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse execute(HttpMethod method, @DelegatesTo(HttpRequest) Closure requestClosure, InputStream inputStream) throws IOException

    /**
     * Executes an HTTP request with the given method, request parameters, and form data.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param form Form data to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse execute(HttpMethod method, HttpRequest request, FormData form) throws IOException

    /**
     * Executes an HTTP request with the given method, closure to configure the request, and form data.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param requestClosure Closure that configures the request.
     * @param form Form data to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse execute(HttpMethod method, @DelegatesTo(HttpRequest) Closure requestClosure, FormData form) throws IOException

    /**
     * Perform an HTTP GET request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse get(HttpRequest request) throws IOException

    /**
     * Perform an HTTP GET request.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse get(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException

    /**
     * Perform an HTTP POST request without a request entity.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse post(HttpRequest request) throws IOException

    /**
     * Perform an HTTP POST request without a request entity.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse post(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException

    /**
     * Perform an HTTP POST request with the given request entity.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity A byte array to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse post(HttpRequest request, byte[] entity) throws IOException

    /**
     * Perform an HTTP POST request with the given request entity.
     *
     * @param requestClosure Closure that configures the request.
     * @param entity A byte array to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse post(@DelegatesTo(HttpRequest) Closure requestClosure, byte[] entity) throws IOException

    /**
     * Perform an HTTP POST request with the given request entity.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity A <code>String</code> to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse post(HttpRequest request, String entity) throws IOException

    /**
     * Perform an HTTP POST request with the given request entity.
     *
     * @param requestClosure Closure that configures the request.
     * @param entity A <code>String</code> to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse post(@DelegatesTo(HttpRequest) Closure requestClosure, String entity) throws IOException

    /**
     * Perform an HTTP POST request with the given input stream.
     *
     * @param request Request properties to use with the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse post(HttpRequest request, InputStream inputStream) throws IOException

    /**
     * Perform an HTTP POST request with the given input stream.
     *
     * @param requestClosure Closure that configures the request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse post(@DelegatesTo(HttpRequest) Closure requestClosure, InputStream inputStream) throws IOException

    /**
     * Perform an HTTP POST request with the given form data.
     *
     * @param request Request properties to use with the HTTP request.
     * @param form Form data to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse post(HttpRequest request, FormData form) throws IOException

    /**
     * Perform an HTTP POST request with the given form data.
     *
     * @param requestClosure Closure that configures the request.
     * @param form Form data to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse post(@DelegatesTo(HttpRequest) Closure requestClosure, FormData form) throws IOException

    /**
     * Perform an HTTP PUT request without a request entity.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse put(HttpRequest request) throws IOException

    /**
     * Perform an HTTP PUT request without a request entity.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse put(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException

    /**
     * Perform an HTTP PUT request with the given request entity.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity A byte array to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse put(HttpRequest request, byte[] entity) throws IOException

    /**
     * Perform an HTTP PUT request with the given request entity.
     *
     * @param requestClosure Closure that configures the request.
     * @param entity A byte array to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse put(@DelegatesTo(HttpRequest) Closure requestClosure, byte[] entity) throws IOException

    /**
     * Perform an HTTP PUT request with the given request entity.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity A <code>String</code> to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse put(HttpRequest request, String entity) throws IOException

    /**
     * Perform an HTTP PUT request with the given request entity.
     *
     * @param requestClosure Closure that configures the request.
     * @param entity A <code>String</code> to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse put(@DelegatesTo(HttpRequest) Closure requestClosure, String entity) throws IOException

    /**
     * Perform an HTTP PUT request with the given input stream..
     *
     * @param request Request properties to use with the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse put(HttpRequest request, InputStream inputStream) throws IOException

    /**
     * Perform an HTTP PUT request with the given input stream.
     *
     * @param requestClosure Closure that configures the request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse put(@DelegatesTo(HttpRequest) Closure requestClosure, InputStream inputStream) throws IOException

    /**
     * Perform an HTTP PUT request with the given form data.
     *
     * @param request Request properties to use with the HTTP request.
     * @param form Form data to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse put(HttpRequest request, FormData form) throws IOException

    /**
     * Perform an HTTP PUT request with the given form data.
     *
     * @param requestClosure Closure that configures the request.
     * @param form Form data to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse put(@DelegatesTo(HttpRequest) Closure requestClosure, FormData form) throws IOException

    /**
     * Perform an HTTP DELETE request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse delete(HttpRequest request) throws IOException

    /**
     * Perform an HTTP DELETE request.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse delete(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException

    /**
     * Perform an HTTP OPTIONS request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse options(HttpRequest request) throws IOException

    /**
     * Perform an HTTP OPTIONS request.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse options(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException

    /**
     * Perform an HTTP OPTIONS request with the given request entity.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity A byte array to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse options(HttpRequest request, byte[] entity) throws IOException

    /**
     * Perform an HTTP OPTIONS request with the given request entity.
     *
     * @param requestClosure Closure that configures the request.
     * @param entity A byte array to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse options(@DelegatesTo(HttpRequest) Closure requestClosure, byte[] entity) throws IOException

    /**
     * Perform an HTTP OPTIONS request with the given request entity.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity A <code>String</code> to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse options(HttpRequest request, String entity) throws IOException

    /**
     * Perform an HTTP OPTIONS request with the given request entity.
     *
     * @param requestClosure Closure that configures the request.
     * @param entity A <code>String</code> to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse options(@DelegatesTo(HttpRequest) Closure requestClosure, String entity) throws IOException

    /**
     * Perform an HTTP OPTIONS request with the given input stream.
     *
     * @param request Request properties to use with the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse options(HttpRequest request, InputStream inputStream) throws IOException

    /**
     * Perform an HTTP OPTIONS request with the given input stream.
     *
     * @param requestClosure Closure that configures the request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse options(@DelegatesTo(HttpRequest) Closure requestClosure, InputStream entity) throws IOException

    /**
     * Perform an HTTP OPTIONS request with the given form data.
     *
     * @param requestClosure Closure that configures the request.
     * @param form Form data to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse options(@DelegatesTo(HttpRequest) Closure requestClosure, FormData form) throws IOException

    /**
     * Perform an HTTP HEAD request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse head(HttpRequest request) throws IOException

    /**
     * Perform an HTTP HEAD request.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse head(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException

    /**
     * Perform an HTTP TRACE request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse trace(HttpRequest request) throws IOException

    /**
     * Perform an HTTP TRACE request.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse trace(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException

    /**
     * Adds a {@link HttpClientListener} to the HTTP client.
     *
     * @param listener Listener instance to register with the client.
     * @return The object the method was called on.
     */
    HttpClient addListener(HttpClientListener listener)

    /**
     * Returns the list of all registered {@link HttpClientListener} instances.
     *
     * @return The list of registered listener instances.
     */
    List<HttpClientListener> getListeners()

    /**
     * Unregisters a {@link HttpClientListener} from the HTTP client.
     *
     * @param listener Listener instance to remove from the client.
     * @return The object the method was called on.
     */
    HttpClient removeListener(HttpClientListener listener)
}