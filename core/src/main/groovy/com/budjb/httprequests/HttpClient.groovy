package com.budjb.httprequests

interface HttpClient {
    /**
     * Execute an HTTP request with the given method and request parameters and without a request entity.
     *
     * @param method
     * @param request
     * @return
     * @throws IOException
     */
    HttpResponse execute(HttpMethod method, HttpRequest request) throws IOException

    /**
     * Executes an HTTP request with the given method, request parameters, and request entity.
     *
     * @param method
     * @param request
     * @param entity
     * @return
     * @throws IOException
     */
    HttpResponse execute(HttpMethod method, HttpRequest request, byte[] entity) throws IOException

    /**
     * Executes an HTTP request with the given method, request parameters, and request entity.
     *
     * @param method
     * @param request
     * @param entity
     * @return
     * @throws IOException
     */
    HttpResponse execute(HttpMethod method, HttpRequest request, String entity) throws IOException

    /**
     * Executes an HTTP request with the given method, request parameters, and input stream.
     *
     * @param method
     * @param request
     * @param inputStream
     * @return
     * @throws IOException
     */
    HttpResponse execute(HttpMethod method, HttpRequest request, InputStream inputStream) throws IOException

    /**
     * Perform an HTTP GET request.
     *
     * @param request
     * @return
     * @throws IOException
     */
    HttpResponse get(HttpRequest request) throws IOException

    /**
     * Perform an HTTP POST request without a request entity.
     *
     * @param request
     * @return
     * @throws IOException
     */
    HttpResponse post(HttpRequest request) throws IOException

    /**
     * Perform an HTTP POST request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     * @throws IOException
     */
    HttpResponse post(HttpRequest request, byte[] entity) throws IOException

    /**
     * Perform an HTTP POST request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     * @throws IOException
     */
    HttpResponse post(HttpRequest request, String entity) throws IOException

    /**
     * Perform an HTTP POST request with the given input stream.
     *
     * @param request
     * @param inputStream
     * @return
     * @throws IOException
     */
    HttpResponse post(HttpRequest request, InputStream inputStream) throws IOException

    /**
     * Perform an HTTP PUT request without a request entity.
     *
     * @param request
     * @return
     * @throws IOException
     */
    HttpResponse put(HttpRequest request) throws IOException

    /**
     * Perform an HTTP PUT request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     * @throws IOException
     */
    HttpResponse put(HttpRequest request, byte[] entity) throws IOException

    /**
     * Perform an HTTP PUT request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     * @throws IOException
     */
    HttpResponse put(HttpRequest request, String entity) throws IOException

    /**
     * Perform an HTTP PUT request with the given input stream..
     *
     * @param request
     * @param inputStream
     * @return
     * @throws IOException
     */
    HttpResponse put(HttpRequest request, InputStream inputStream) throws IOException

    /**
     * Perform an HTTP DELETE request.
     *
     * @param request
     * @return
     * @throws IOException
     */
    HttpResponse delete(HttpRequest request) throws IOException

    /**
     * Perform an HTTP OPTIONS request.
     *
     * @param request
     * @return
     * @throws IOException
     */
    HttpResponse options(HttpRequest request) throws IOException

    /**
     * Perform an HTTP OPTIONS request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     * @throws IOException
     */
    HttpResponse options(HttpRequest request, byte[] entity) throws IOException

    /**
     * Perform an HTTP OPTIONS request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     * @throws IOException
     */
    HttpResponse options(HttpRequest request, String entity) throws IOException

    /**
     * Perform an HTTP OPTIONS request with the given input stream.
     *
     * @param request
     * @param inputStream
     * @return
     * @throws IOException
     */
    HttpResponse options(HttpRequest request, InputStream inputStream) throws IOException

    /**
     * Perform an HTTP HEAD request.
     *
     * @param request
     * @return
     * @throws IOException
     */
    HttpResponse head(HttpRequest request) throws IOException

    /**
     * Perform an HTTP TRACE request.
     *
     * @param request
     * @return
     * @throws IOException
     */
    HttpResponse trace(HttpRequest request) throws IOException
}