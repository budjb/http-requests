package com.budjb.httprequests.listener

import com.budjb.httprequests.HttpRequest

/**
 * An {@link HttpClientListener} that provides Basic Authentication support.
 */
class BasicAuthListener implements HttpClientRequestListener {
    /**
     * Name of the header for basic authentication.
     */
    private static final String HEADER = 'Authorization'

    /**
     * Contains the encoded credentials
     */
    private String credentials

    /**
     * Constructor.
     *
     * @param username User name to authentication with.
     * @param password Password to authenticate with.
     */
    BasicAuthListener(String username, String password) {
        credentials = "$username:$password".getBytes().encodeBase64()
    }

    /**
     * Provides an opportunity to modify the request object before the HTTP request is made.
     *
     * @param request Request object that will be used to make the HTTP request.
     */
    @Override
    void filterRequest(HttpRequest request) {
        request.setHeader(HEADER, credentials)
    }
}
