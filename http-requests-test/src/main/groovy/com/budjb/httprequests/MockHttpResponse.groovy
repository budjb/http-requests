package com.budjb.httprequests

import com.budjb.httprequests.converter.EntityConverterManager

/**
 * A standalone {@link HttpResponse} implementation where its properties are injected rather
 * than read from an HTTP client's response.
 */
class MockHttpResponse extends HttpResponse {
    /**
     * Constructor.
     *
     * @param request Request properties used to make the request.
     * @param converterManager Converter manager.
     * @param status HTTP status of the response.
     * @param headers Response headers.
     * @param contentType Content-Type of the response.
     * @param entity Entity of the response.
     */
    MockHttpResponse(HttpRequest request, EntityConverterManager converterManager, int status, Map<String, Object> headers, String contentType, InputStream entity) {
        super(request, converterManager)

        setStatus(status)
        setHeaders(headers)
        setContentType(contentType)
        setEntity(entity)
    }
}
