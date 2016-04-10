package com.budjb.httprequests.exception

import com.budjb.httprequests.HttpResponse

/**
 * An exception representing a non-2XX HTTP response. Contains both the
 * HTTP status of the response and the response object itself.
 */
class HttpResponseException extends RuntimeException {
    /**
     * The HTTP status of the response.
     */
    int status

    /**
     * Response of the request.
     */
    HttpResponse response

    /**
     * Constructor.
     *
     * @param httpResponse
     */
    HttpResponseException(HttpResponse httpResponse) {
        super("the HTTP request returned HTTP status ${httpResponse.getStatus()}")

        response = httpResponse
        status = httpResponse.getStatus()
    }
}
