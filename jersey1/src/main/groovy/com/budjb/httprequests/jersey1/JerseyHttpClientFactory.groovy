package com.budjb.httprequests.jersey1

import com.budjb.httprequests.HttpClient
import com.budjb.httprequests.HttpClientFactory

class JerseyHttpClientFactory implements HttpClientFactory {
    /**
     * Singleton instance of the {@link JerseyHttpClientFactory}.
     */
    static final JerseyHttpClientFactory instance = new JerseyHttpClientFactory()

    /**
     * Create a new Jersey HTTP client.
     *
     * @return
     */
    @Override
    HttpClient createHttpClient() {
        return new JerseyHttpClient()
    }
}
