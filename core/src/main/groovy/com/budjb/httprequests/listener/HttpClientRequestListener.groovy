package com.budjb.httprequests.listener

import com.budjb.httprequests.HttpRequest

interface HttpClientRequestListener extends HttpClientListener {
    /**
     * Provides an opportunity to modify the request object before the HTTP request is made.
     *
     * @param request Request object that will be used to make the HTTP request.
     */
    void doWithRequest(HttpRequest request)
}
