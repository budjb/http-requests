package com.budjb.httprequests.listener

import com.budjb.httprequests.HttpRequest

/**
 * An HTTP client listener that allows modification of the request entity before it is
 * transmitted with the request.
 */
interface HttpClientEntityListener extends HttpClientListener {
    /**
     * Filters a request entity in byte array form.
     *
     * @param request HTTP request properties.
     * @param entity Request entity.
     * @return Filtered request entity.
     */
    byte[] filterEntity(HttpRequest request, byte[] entity)

    /**
     * Filters a request entity in {@link InputStream} form.
     *
     * @param request HTTP request properties.
     * @param inputStream Input stream of the request.
     * @return Filtered request input stream.
     */
    InputStream filterEntity(HttpRequest request, InputStream inputStream)
}
