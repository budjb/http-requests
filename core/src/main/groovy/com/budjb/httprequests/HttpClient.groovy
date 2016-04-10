package com.budjb.httprequests

interface HttpClient {
    /**
     * Execute an HTTP request with the given method and request parameters and without a request entity.
     *
     * @param method
     * @param request
     * @return
     */
    HttpResponse execute(HttpMethod method, HttpRequest request)

    /**
     * Executes an HTTP request with the given method, request parameters, and request entity.
     *
     * @param method
     * @param request
     * @param entity
     * @return
     */
    HttpResponse execute(HttpMethod method, HttpRequest request, byte[] entity)

    /**
     * Executes an HTTP request with the given method, request parameters, and request entity.
     *
     * @param method
     * @param request
     * @param entity
     * @return
     */
    HttpResponse execute(HttpMethod method, HttpRequest request, String entity)

    /**
     * Executes an HTTP request with the given method, request parameters, and input stream.
     *
     * @param method
     * @param request
     * @param inputStream
     * @return
     */
    HttpResponse execute(HttpMethod method, HttpRequest request, InputStream inputStream)

    /**
     * Perform an HTTP GET request.
     *
     * @param request
     * @return
     */
    HttpResponse get(HttpRequest request)

    /**
     * Perform an HTTP POST request without a request entity.
     *
     * @param request
     * @return
     */
    HttpResponse post(HttpRequest request)

    /**
     * Perform an HTTP POST request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     */
    HttpResponse post(HttpRequest request, byte[] entity)

    /**
     * Perform an HTTP POST request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     */
    HttpResponse post(HttpRequest request, String entity)

    /**
     * Perform an HTTP POST request with the given input stream.
     *
     * @param request
     * @param inputStream
     * @return
     */
    HttpResponse post(HttpRequest request, InputStream inputStream)

    /**
     * Perform an HTTP PUT request without a request entity.
     *
     * @param request
     * @return
     */
    HttpResponse put(HttpRequest request)

    /**
     * Perform an HTTP PUT request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     */
    HttpResponse put(HttpRequest request, byte[] entity)

    /**
     * Perform an HTTP PUT request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     */
    HttpResponse put(HttpRequest request, String entity)

    /**
     * Perform an HTTP PUT request with the given input stream..
     *
     * @param request
     * @param inputStream
     * @return
     */
    HttpResponse put(HttpRequest request, InputStream inputStream)

    /**
     * Perform an HTTP DELETE request.
     *
     * @param request
     * @return
     */
    HttpResponse delete(HttpRequest request)

    /**
     * Perform an HTTP OPTIONS request.
     *
     * @param request
     * @return
     */
    HttpResponse options(HttpRequest request)

    /**
     * Perform an HTTP OPTIONS request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     */
    HttpResponse options(HttpRequest request, byte[] entity)

    /**
     * Perform an HTTP OPTIONS request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     */
    HttpResponse options(HttpRequest request, String entity)

    /**
     * Perform an HTTP OPTIONS request with the given input stream.
     *
     * @param request
     * @param inputStream
     * @return
     */
    HttpResponse options(HttpRequest request, InputStream inputStream)

    /**
     * Perform an HTTP HEAD request.
     *
     * @param request
     * @return
     */
    HttpResponse head(HttpRequest request)

    /**
     * Perform an HTTP TRACE request.
     *
     * @param request
     * @return
     */
    HttpResponse trace(HttpRequest request)
}