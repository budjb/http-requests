package com.budjb.httprequests.listener

import com.budjb.httprequests.HttpClient
import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.HttpResponse

/**
 * An {@link HttpClientListener} that allows modification of the {@link HttpResponse} instance before
 * it is returned from the {@link HttpClient}.
 */
interface HttpClientResponseListener extends HttpClientListener {
    /**
     * Provides an opportunity to modify the {@link HttpResponse} before it is returned.
     *
     * @param request The request object used to make the request.
     * @param response The response object created from the response of the HTTP request.
     */
    void doWithResponse(HttpRequest request, HttpResponse response)
}
