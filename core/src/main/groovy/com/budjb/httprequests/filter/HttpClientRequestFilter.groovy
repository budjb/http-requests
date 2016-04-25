package com.budjb.httprequests.filter

import com.budjb.httprequests.HttpRequest

/**
 * An {@link HttpClientFilter} that allows modification of the {@link HttpRequest} instance before
 * the request is transmitted.
 */
interface HttpClientRequestFilter extends HttpClientFilter {
    /**
     * Provides an opportunity to modify the {@link HttpRequest} before it is transmitted.
     *
     * @param request Request object that will be used to make the HTTP request.
     */
    void filterRequest(HttpRequest request)
}
