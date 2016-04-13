package com.budjb.httprequests.listener

import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.HttpResponse

/**
 * An {@link HttpClientListener} that supports retrying HTTP requests.
 */
interface HttpClientRetryListener extends HttpClientListener {
    /**
     * Returns whether this listener requests a retry of the HTTP request.
     *
     * Note that this listener will receive a call to {@link #onRetry(HttpRequest, HttpResponse)} only if this method
     * returns true, even if a retry is otherwise attempted.
     *
     * @param request Configuration of the request.
     * @param response Response properties.
     * @param retries The number of retries that have occurred before this method is run.
     * @return <code>true</code> if it is determined that a retry of the request should be attempted.
     */
    boolean shouldRetry(HttpRequest request, HttpResponse response, int retries)

    /**
     * Called before the attempt to retry the HTTP request.
     *
     * This method is only called when {@link #shouldRetry(HttpRequest, HttpResponse, int)} returns true, even if a
     * retry is otherwise attempted.
     *
     * @param request Configuration of the request.
     * @param response Response properties.
     */
    void onRetry(HttpRequest request, HttpResponse response)
}