package com.budjb.httprequests.filter.bundled

import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.filter.HttpClientEntityFilter
import com.budjb.httprequests.filter.HttpClientRequestFilter

import java.util.zip.GZIPOutputStream

/**
 * A filter that encodes the HTTP request with GZIP.
 */
class GZIPFilter implements HttpClientEntityFilter, HttpClientRequestFilter {
    /**
     * Filters a request entity in {@link OutputStream} form.
     *
     * @param request HTTP request properties.
     * @param outputStream Output stream of the request.
     * @return Filtered request input stream.
     */
    @Override
    OutputStream filterEntity(OutputStream outputStream) {
        return new GZIPOutputStream(outputStream)
    }

    /**
     * Provides an opportunity to modify the {@link HttpRequest} before it is transmitted.
     *
     * @param request Request object that will be used to make the HTTP request.
     */
    @Override
    void filterRequest(HttpRequest request) {
        request.setHeader('Content-Encoding', 'gzip')
    }
}
