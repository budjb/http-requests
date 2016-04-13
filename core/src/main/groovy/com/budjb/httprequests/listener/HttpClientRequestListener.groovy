package com.budjb.httprequests.listener

import com.budjb.httprequests.HttpRequest

/**
 * An {@link HttpClientListener} that allows modification of the {@link HttpRequest} instance before
 * the request is transmitted.
 */
interface HttpClientRequestListener extends HttpClientListener {
    /**
     * Provides an opportunity to modify the {@link HttpRequest} before it is transmitted.
     *
     * @param request Request object that will be used to make the HTTP request.
     */
    void doWithRequest(HttpRequest request)
}
