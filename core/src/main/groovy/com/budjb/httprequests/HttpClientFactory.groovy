package com.budjb.httprequests

interface HttpClientFactory {
    /**
     * Create a new HTTP client.
     *
     * @return
     */
    HttpClient createHttpClient()
}
