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
     * @throws IOException
     */
    @Override
    HttpResponse get(HttpRequest request) throws IOException {
        return execute(HttpMethod.GET, request)
    }

    /**
     * Perform an HTTP POST request without a request entity.
     *
     * @param request
     * @return
     * @throws IOException
     */
    @Override
    HttpResponse post(HttpRequest request) throws IOException {
        return execute(HttpMethod.POST, request)
    }

    /**
     * Perform an HTTP POST request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     * @throws IOException
     */
    @Override
    HttpResponse post(HttpRequest request, byte[] entity) throws IOException {
        return execute(HttpMethod.POST, request, entity)
    }

    /**
     * Perform an HTTP POST request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     * @throws IOException
     */
    @Override
    HttpResponse post(HttpRequest request, String entity) throws IOException {
        return execute(HttpMethod.POST, request, entity)
    }

    /**
     * Perform an HTTP POST request with the given input stream.
     *
     * @param request
     * @param inputStream
     * @return
     * @throws IOException
     */
    @Override
    HttpResponse post(HttpRequest request, InputStream inputStream) throws IOException {
        return execute(HttpMethod.POST, request, inputStream)
    }

    /**
     * Perform an HTTP POST request with the given form data.
     *
     * @param request
     * @param form
     * @return
     * @throws IOException
     */
    @Override
    HttpResponse post(HttpRequest request, FormData form) throws IOException {
        return execute(HttpMethod.POST, request, form)
    }

    /**
     * Perform an HTTP PUT request without a request entity.
     *
     * @param request
     * @return
     * @throws IOException
     */
    @Override
    HttpResponse put(HttpRequest request) throws IOException {
        return execute(HttpMethod.PUT, request)
    }

    /**
     * Perform an HTTP PUT request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     * @throws IOException
     */
    @Override
    HttpResponse put(HttpRequest request, byte[] entity) throws IOException {
        return execute(HttpMethod.PUT, request, entity)
    }

    /**
     * Perform an HTTP PUT request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     * @throws IOException
     */
    @Override
    HttpResponse put(HttpRequest request, String entity) throws IOException {
        return execute(HttpMethod.PUT, request, entity)
    }

    /**
     * Perform an HTTP PUT request with the given input stream..
     *
     * @param request
     * @param inputStream
     * @return
     * @throws IOException
     */
    @Override
    HttpResponse put(HttpRequest request, InputStream inputStream) throws IOException {
        return execute(HttpMethod.PUT, request, inputStream)
    }

    /**
     * Perform an HTTP PUT request with the given input stream.
     *
     * @param request
     * @param form
     * @return
     * @throws IOException
     */
    @Override
    HttpResponse put(HttpRequest request, FormData form) throws IOException {
        return execute(HttpMethod.PUT, request, form)
    }

    /**
     * Perform an HTTP DELETE request.
     *
     * @param request
     * @return
     * @throws IOException
     */
    @Override
    HttpResponse delete(HttpRequest request) throws IOException {
        return execute(HttpMethod.DELETE, request)
    }

    /**
     * Perform an HTTP OPTIONS request.
     *
     * @param request
     * @return
     * @throws IOException
     */
    @Override
    HttpResponse options(HttpRequest request) throws IOException {
        return execute(HttpMethod.OPTIONS, request)
    }

    /**
     * Perform an HTTP OPTIONS request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     * @throws IOException
     */
    @Override
    HttpResponse options(HttpRequest request, byte[] entity) throws IOException {
        return execute(HttpMethod.OPTIONS, request, entity)
    }

    /**
     * Perform an HTTP OPTIONS request with the given request entity.
     *
     * @param request
     * @param entity
     * @return
     * @throws IOException
     */
    @Override
    HttpResponse options(HttpRequest request, String entity) throws IOException {
        return execute(HttpMethod.OPTIONS, request, entity)
    }

    /**
     * Perform an HTTP OPTIONS request with the given input stream.
     *
     * @param request
     * @param inputStream
     * @return
     * @throws IOException
     */
    @Override
    HttpResponse options(HttpRequest request, InputStream inputStream) throws IOException {
        return execute(HttpMethod.OPTIONS, request, inputStream)
    }

    /**
     * Perform an HTTP HEAD request.
     *
     * @param request
     * @return
     * @throws IOException
     */
    @Override
    HttpResponse head(HttpRequest request) throws IOException {
        return execute(HttpMethod.HEAD, request)
    }

    /**
     * Perform an HTTP TRACE request.
     *
     * @param request
     * @return
     * @throws IOException
     */
    @Override
    HttpResponse trace(HttpRequest request) throws IOException {
        return execute(HttpMethod.TRACE, request)
    }
}
