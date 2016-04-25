package com.budjb.httprequests.filter

import com.budjb.httprequests.HttpRequest

/**
 * An HTTP client filter that allows modification of the request entity before it is
 * transmitted with the request.
 */
interface HttpClientEntityFilter extends HttpClientFilter {
    /**
     * Filters a request entity in {@link InputStream} form.
     *
     * @param request HTTP request properties.
     * @param inputStream Input stream of the request.
     * @return Filtered request input stream.
     */
    InputStream filterEntity(HttpRequest request, InputStream inputStream)
}
