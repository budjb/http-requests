package com.budjb.httprequests

/**
 * A base class that proxies all HTTP method-specific methods to their proper <code>execute</code> counterparts.
 * This class is useful to avoid significant amounts of boilerplate code when implementing new HTTP clients.
 */
abstract class AbstractHttpClient implements HttpClient {
    /**
     * Perform an HTTP GET request.
     *
     * @param request
     * @return
     */
    @Override
    HttpResponse get(HttpRequest request) {
        return execute(HttpMethod.GET, request)
    }

    /**
     * Perform an HTTP POST request without a request entity.
     *
     * @param request
     * @return
     */
    @Override
    HttpResponse post(HttpRequest request) {
        return execute(HttpMethod.POST, request)
    }

    /**
     * Perform an HTTP POST request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     */
    @Override
    HttpResponse post(HttpRequest request, byte[] entity) {
        return execute(HttpMethod.POST, request, entity)
    }

    /**
     * Perform an HTTP POST request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     */
    @Override
    HttpResponse post(HttpRequest request, String entity) {
        return execute(HttpMethod.POST, request, entity)
    }

    /**
     * Perform an HTTP POST request with the given input stream.
     *
     * @param request
     * @param inputStream
     * @return
     */
    @Override
    HttpResponse post(HttpRequest request, InputStream inputStream) {
        return execute(HttpMethod.POST, request, inputStream)
    }

    /**
     * Perform an HTTP PUT request without a request entity.
     *
     * @param request
     * @return
     */
    @Override
    HttpResponse put(HttpRequest request) {
        return execute(HttpMethod.PUT, request)
    }

    /**
     * Perform an HTTP PUT request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     */
    @Override
    HttpResponse put(HttpRequest request, byte[] entity) {
        return execute(HttpMethod.PUT, request, entity)
    }

    /**
     * Perform an HTTP PUT request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     */
    @Override
    HttpResponse put(HttpRequest request, String entity) {
        return execute(HttpMethod.PUT, request, entity)
    }

    /**
     * Perform an HTTP PUT request with the given input stream..
     *
     * @param request
     * @param inputStream
     * @return
     */
    @Override
    HttpResponse put(HttpRequest request, InputStream inputStream) {
        return execute(HttpMethod.PUT, request, inputStream)
    }

    /**
     * Perform an HTTP DELETE request.
     *
     * @param request
     * @return
     */
    @Override
    HttpResponse delete(HttpRequest request) {
        return execute(HttpMethod.DELETE, request)
    }

    /**
     * Perform an HTTP OPTIONS request.
     *
     * @param request
     * @return
     */
    @Override
    HttpResponse options(HttpRequest request) {
        return execute(HttpMethod.OPTIONS, request)
    }

    /**
     * Perform an HTTP OPTIONS request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     */
    @Override
    HttpResponse options(HttpRequest request, byte[] entity) {
        return execute(HttpMethod.OPTIONS, request, entity)
    }

    /**
     * Perform an HTTP OPTIONS request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     */
    @Override
    HttpResponse options(HttpRequest request, String entity) {
        return execute(HttpMethod.OPTIONS, request, entity)
    }

    /**
     * Perform an HTTP OPTIONS request with the given input stream.
     *
     * @param request
     * @param inputStream
     * @return
     */
    @Override
    HttpResponse options(HttpRequest request, InputStream inputStream) {
        return execute(HttpMethod.OPTIONS, request, inputStream)
    }

    /**
     * Perform an HTTP HEAD request.
     *
     * @param request
     * @return
     */
    @Override
    HttpResponse head(HttpRequest request) {
        return execute(HttpMethod.HEAD, request)
    }

    /**
     * Perform an HTTP TRACE request.
     *
     * @param request
     * @return
     */
    @Override
    HttpResponse trace(HttpRequest request) {
        return execute(HttpMethod.TRACE, request)
    }
}
