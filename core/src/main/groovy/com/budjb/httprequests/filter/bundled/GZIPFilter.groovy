package com.budjb.httprequests.filter.bundled

import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.filter.HttpClientEntityFilter

import java.util.zip.GZIPInputStream

/**
 * A filter that encodes the HTTP request with GZIP.
 */
class GZIPFilter implements HttpClientEntityFilter {
    /**
     * Filters a request entity in {@link InputStream} form.
     *
     * @param request HTTP request properties.
     * @param inputStream Input stream of the request.
     * @return Filtered request input stream.
     */
    @Override
    InputStream filterEntity(HttpRequest request, InputStream inputStream) {
        request.setHeader('Content-Encoding', 'gzip')
        return new GZIPInputStream(inputStream)
    }
}
