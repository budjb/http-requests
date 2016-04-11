package com.budjb.httprequests.listener

import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.HttpResponse

interface HttpClientResponseListener extends HttpClientListener {
    /**
     * Provides an opportunity to modify the response object before the HTTP response is returned.
     *
     * @param request The request object used to make the request.
     * @param response The response object created from the response of the HTTP request.
     */
    void doWithResponse(HttpRequest request, HttpResponse response)
}
