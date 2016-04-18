package com.budjb.httprequests

import com.budjb.httprequests.exception.HttpStatusException
import com.budjb.httprequests.listener.HttpClientListener
import com.budjb.httprequests.listener.HttpClientRequestListener
import com.budjb.httprequests.listener.HttpClientResponseListener
import com.budjb.httprequests.listener.HttpClientRetryListener

/**
 * A base class for HTTP clients that implements most of the functionality of the {@link HttpClient} interface.
 *
 * Individual HTTP client library implementations should extend this class.
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
    protected abstract HttpResponse doExecute(HttpMethod method, HttpRequest request) throws IOException

    /**
     * Implements the logic to make an actual request with an HTTP client library.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param entity A byte array to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    protected abstract HttpResponse doExecute(HttpMethod method, HttpRequest request, byte[] entity) throws IOException

    /**
     * Implements the logic to make an actual request with an HTTP client library.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    protected abstract HttpResponse doExecute(HttpMethod method, HttpRequest request, InputStream inputStream) throws IOException

    /**
     * Implements the logic to make an actual request with an HTTP client library.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @param form Form data to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    protected abstract HttpResponse doExecute(HttpMethod method, HttpRequest request, FormData form) throws IOException

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
     * Executes an HTTP request with the given method and closure to configure the request without a request entity.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse execute(HttpMethod method, @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(method, HttpRequest.build(requestClosure))
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
     * Executes an HTTP request with the given method, closure to configure the request, and request entity.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param entity A byte array to send with the request.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse execute(HttpMethod method, byte[] entity, @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(method, HttpRequest.build(requestClosure), entity)
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
     * Executes an HTTP request with the given method, closure to configure the request, and request entity.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param entity A <code>String</code> to send with the request.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse execute(HttpMethod method, String entity, @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(method, HttpRequest.build(requestClosure), entity)
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
     * Executes an HTTP request with the given method, closure to configure the request, and input stream.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param inputStream An {@link InputStream} containing the response body.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse execute(HttpMethod method, InputStream inputStream, @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(method, HttpRequest.build(requestClosure), inputStream)
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
        if (!request.getContentType()) {
            request.setContentType('application/x-www-form-urlencoded')
        }
        return run(request, { doExecute(method, request, form) })
    }

    /**
     * Executes an HTTP request with the given method, closure to configure the request, and form data.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param form Form data to send with the request.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse execute(HttpMethod method, FormData form, @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(method, HttpRequest.build(requestClosure), form)
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
     * Perform an HTTP GET request.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse get(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.GET, requestClosure)
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
     * Perform an HTTP POST request without a request entity.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse post(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.POST, requestClosure)
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
     * @param entity A byte array to send with the request.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse post(byte[] entity, @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.POST, entity, requestClosure)
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
     * Perform an HTTP POST request with the given request entity.
     *
     * @param entity A <code>String</code> to send with the request.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse post(String entity, @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.POST, entity, requestClosure)
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
     * Perform an HTTP POST request with the given input stream.
     *
     * @param inputStream An {@link InputStream} containing the response body.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse post(InputStream inputStream, @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.POST, inputStream, requestClosure)
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
     * Perform an HTTP POST request with the given form data.
     *
     * @param form Form data to send with the request.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse post(FormData form, @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.POST, form, requestClosure)
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
     * Perform an HTTP PUT request without a request entity.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse put(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.PUT, requestClosure)
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
     * @param entity A byte array to send with the request.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse put(byte[] entity, @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.PUT, entity, requestClosure)
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
     * Perform an HTTP PUT request with the given request entity.
     *
     * @param entity A <code>String</code> to send with the request.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse put(String entity, @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.PUT, entity, requestClosure)
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
     * Perform an HTTP PUT request with the given input stream.
     *
     * @param inputStream An {@link InputStream} containing the response body.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse put(InputStream inputStream, @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.PUT, inputStream, requestClosure)
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
     * Perform an HTTP PUT request with the given form data.
     *
     * @param form Form data to send with the request.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse put(FormData form, @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.PUT, form, requestClosure)
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
     * Perform an HTTP DELETE request.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse delete(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.DELETE, requestClosure)
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
     * Perform an HTTP OPTIONS request.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse options(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.OPTIONS, requestClosure)
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
     * @param entity A byte array to send with the request.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse options(byte[] entity, @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.OPTIONS, entity, requestClosure)
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
     * Perform an HTTP OPTIONS request with the given request entity.
     *
     * @param entity A <code>String</code> to send with the request.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse options(String entity, @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.OPTIONS, entity, requestClosure)
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
     * Perform an HTTP OPTIONS request with the given input stream.
     *
     * @param inputStream An {@link InputStream} containing the response body.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse options(InputStream inputStream, @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.OPTIONS, inputStream, requestClosure)
    }

    /**
     * Perform an HTTP OPTIONS request with the given form data.
     *
     * @param request Request properties to use with the HTTP request.
     * @param form Form data to send with the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse options(HttpRequest request, FormData form) throws IOException {
        return execute(HttpMethod.OPTIONS, request, form)
    }

    /**
     * Perform an HTTP OPTIONS request with the given form data.
     *
     * @param form Form data to send with the request.
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse options(FormData form, @DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.OPTIONS, form, requestClosure)
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
     * Perform an HTTP HEAD request.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse head(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.HEAD, requestClosure)
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
     * Perform an HTTP TRACE request.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    HttpResponse trace(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException {
        return execute(HttpMethod.TRACE, requestClosure)
    }

    /**
     * Adds a {@link HttpClientListener} to the HTTP client.
     *
     * @param listener Listener instance to register with the client.
     * @return The object the method was called on.
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
     * @return The object the method was called on.
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
            throw HttpStatusException.build(response)
        }

        return response
    }
}
