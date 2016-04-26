package com.budjb.httprequests.filter

/**
 * An HTTP client filter that allows modification of the request entity before it is
 * transmitted with the request.
 */
interface HttpClientEntityFilter extends HttpClientFilter {
    /**
     * Filters a request entity in {@link OutputStream} form.
     *
     * @param outputStream Output stream of the request.
     * @return Filtered request input stream.
     */
    OutputStream filterEntity(OutputStream outputStream)
}
