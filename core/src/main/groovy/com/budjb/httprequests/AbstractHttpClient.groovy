package com.budjb.httprequests

import com.budjb.httprequests.exception.HttpResponseException
import com.budjb.httprequests.listener.HttpClientListener
import com.budjb.httprequests.listener.HttpClientRequestListener
import com.budjb.httprequests.listener.HttpClientResponseListener
import com.budjb.httprequests.listener.HttpClientRetryListener

/**
 * A base class that proxies all HTTP method-specific methods to their proper <code>execute</code> counterparts.
 * This class is useful to avoid significant amounts of boilerplate code when implementing new HTTP clients.
 */
abstract class AbstractHttpClient implements HttpClient {
    /**
     * List of registered {@link HttpClientListener} objects.
     */
    private List<HttpClientListener> listeners = []

    /**
     * Implements the logic to make an actual request with an HTTP client library.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    abstract HttpResponse doExecute(HttpMethod method, HttpRequest request) throws IOException

    /**
     * Implements the logic to make an actual request with an HTTP client library.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param entity A byte array to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    abstract HttpResponse doExecute(HttpMethod method, HttpRequest request, byte[] entity) throws IOException

    /**
     * Implements the logic to make an actual request with an HTTP client library.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    abstract HttpResponse doExecute(HttpMethod method, HttpRequest request, InputStream inputStream) throws IOException

    /**
     * Implements the logic to make an actual request with an HTTP client library.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param form Form data to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    abstract HttpResponse doExecute(HttpMethod method, HttpRequest request, FormData form) throws IOException

    /**
     * Execute an HTTP request with the given method and request parameters and without a request entity.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse execute(HttpMethod method, HttpRequest request) throws IOException {
        return run(request, { doExecute(method, request) })
    }

    /**
     * Executes an HTTP request with the given method, request parameters, and request entity.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param entity A byte array to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse execute(HttpMethod method, HttpRequest request, byte[] entity) throws IOException {
        return run(request, { doExecute(method, request, entity) })
    }

    /**
     * Executes an HTTP request with the given method, request parameters, and request entity.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param entity A <code>String</code> to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse execute(HttpMethod method, HttpRequest request, String entity) throws IOException {
        return execute(method, request, entity.getBytes())
    }

    /**
     * Executes an HTTP request with the given method, request parameters, and input stream.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse execute(HttpMethod method, HttpRequest request, InputStream inputStream) throws IOException {
        return run(request, { doExecute(method, request, inputStream) })
    }

    /**
     * Executes an HTTP request with the given method, request parameters, and form data.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param form Form data to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse execute(HttpMethod method, HttpRequest request, FormData form) throws IOException {
        return run(request, { doExecute(method, request, form) })
    }

    /**
     * Perform an HTTP GET request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse get(HttpRequest request) throws IOException {
        return execute(HttpMethod.GET, request)
    }

    /**
     * Perform an HTTP POST request without a request entity.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse post(HttpRequest request) throws IOException {
        return execute(HttpMethod.POST, request)
    }

    /**
     * Perform an HTTP POST request with the given request entity.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity A byte array to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse post(HttpRequest request, byte[] entity) throws IOException {
        return execute(HttpMethod.POST, request, entity)
    }

    /**
     * Perform an HTTP POST request with the given request entity.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity A <code>String</code> to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse post(HttpRequest request, String entity) throws IOException {
        return execute(HttpMethod.POST, request, entity)
    }

    /**
     * Perform an HTTP POST request with the given input stream.
     *
     * @param request Request properties to use with the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse post(HttpRequest request, InputStream inputStream) throws IOException {
        return execute(HttpMethod.POST, request, inputStream)
    }

    /**
     * Perform an HTTP POST request with the given form data.
     *
     * @param request Request properties to use with the HTTP request.
     * @param form Form data to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse post(HttpRequest request, FormData form) throws IOException {
        return execute(HttpMethod.POST, request, form)
    }

    /**
     * Perform an HTTP PUT request without a request entity.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse put(HttpRequest request) throws IOException {
        return execute(HttpMethod.PUT, request)
    }

    /**
     * Perform an HTTP PUT request with the given request entity.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity A byte array to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse put(HttpRequest request, byte[] entity) throws IOException {
        return execute(HttpMethod.PUT, request, entity)
    }

    /**
     * Perform an HTTP PUT request with the given request entity.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity A <code>String</code> to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse put(HttpRequest request, String entity) throws IOException {
        return execute(HttpMethod.PUT, request, entity)
    }

    /**
     * Perform an HTTP PUT request with the given input stream..
     *
     * @param request Request properties to use with the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse put(HttpRequest request, InputStream inputStream) throws IOException {
        return execute(HttpMethod.PUT, request, inputStream)
    }

    /**
     * Perform an HTTP PUT request with the given form data.
     *
     * @param request Request properties to use with the HTTP request.
     * @param form Form data to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse put(HttpRequest request, FormData form) throws IOException {
        return execute(HttpMethod.PUT, request, form)
    }

    /**
     * Perform an HTTP DELETE request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse delete(HttpRequest request) throws IOException {
        return execute(HttpMethod.DELETE, request)
    }

    /**
     * Perform an HTTP OPTIONS request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse options(HttpRequest request) throws IOException {
        return execute(HttpMethod.OPTIONS, request)
    }

    /**
     * Perform an HTTP OPTIONS request with the given request entity.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity A byte array to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse options(HttpRequest request, byte[] entity) throws IOException {
        return execute(HttpMethod.OPTIONS, request, entity)
    }

    /**
     * Perform an HTTP OPTIONS request with the given request entity.
     *
     * @param request Request properties to use with the HTTP request.
     * @param entity A <code>String</code> to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse options(HttpRequest request, String entity) throws IOException {
        return execute(HttpMethod.OPTIONS, request, entity)
    }

    /**
     * Perform an HTTP OPTIONS request with the given input stream.
     *
     * @param request Request properties to use with the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse options(HttpRequest request, InputStream inputStream) throws IOException {
        return execute(HttpMethod.OPTIONS, request, inputStream)
    }

    /**
     * Perform an HTTP HEAD request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse head(HttpRequest request) throws IOException {
        return execute(HttpMethod.HEAD, request)
    }

    /**
     * Perform an HTTP TRACE request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse trace(HttpRequest request) throws IOException {
        return execute(HttpMethod.TRACE, request)
    }

    /**
     * Adds a {@link HttpClientListener} to the HTTP client.
     *
     * @param listener Listener instance to register with the client.
     */
    @Override
    HttpClient addListener(HttpClientListener listener) {
        listeners.add(listener)
        return this
    }

    /**
     * Unregisters a {@link HttpClientListener} from the HTTP client.
     *
     * @param listener Listener instance to remove from the client.
     */
    @Override
    HttpClient removeListener(HttpClientListener listener) {
        listeners.remove(listener)
        return this
    }

    /**
     * Returns the list of all registered {@link HttpClientListener} instances.
     *
     * @return The list of registered listener instances.
     */
    @Override
    List<HttpClientListener> getListeners() {
        return listeners
    }

    /**
     * Return a list of all registered {@link HttpClientRequestListener} instances.
     *
     * @return All registered {@link HttpClientRequestListener} instances.
     */
    protected List<HttpClientRequestListener> getRequestListeners() {
        return getListeners().findAll { it instanceof HttpClientRequestListener } as List<HttpClientRequestListener>
    }

    /**
     * Return a list of all registered {@link HttpClientResponseListener} instances.
     *
     * @return A list of all registered {@link HttpClientResponseListener} instances.
     */
    protected List<HttpClientResponseListener> getResponseListeners() {
        return getListeners().findAll { it instanceof HttpClientResponseListener } as List<HttpClientResponseListener>
    }

    /**
     * Return a list of all registered {@link HttpClientRetryListener} instances.
     *
     * @return A list of all registered {@link HttpClientRetryListener} instances.
     */
    protected List<HttpClientRetryListener> getRetryListeners() {
        return getListeners().findAll { it instanceof HttpClientRetryListener } as List<HttpClientRetryListener>
    }

    /**
     * Orchestrates making the HTTP request. Fires appropriate listener events and hands off to the implementation
     * to perform the actual HTTP request.
     *
     * @param request {@link HttpRequest} object to configure the request.
     * @param action A closure containing the logic to run against the HTTP client implementation.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     */
    protected HttpResponse run(HttpRequest request, Closure action) {
        getRequestListeners()*.doWithRequest(request)

        HttpResponse response
        int retries = 0
        while (true) {
            response = action.call() as HttpResponse

            List<HttpClientRetryListener> requestRetry = getRetryListeners().findAll {
                it.shouldRetry(request, response, retries)
            }

            if (!requestRetry.size()) {
                break
            }

            requestRetry.each {
                it.onRetry(request, response)
            }

            retries++
        }

        getResponseListeners()*.doWithResponse(request, response)

        if (request.isThrowStatusExceptions() && response.getStatus() >= 300) {
            throw HttpResponseException.build(response)
        }

        return response
    }
}
