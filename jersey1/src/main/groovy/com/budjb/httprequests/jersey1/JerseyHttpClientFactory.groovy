package com.budjb.httprequests.jersey1

import com.budjb.httprequests.HttpClient
import com.budjb.httprequests.HttpClientFactory

/**
 * An {@link HttpClientFactory} implementation that creates Jersey 1.x HTTP clients.
 */
class JerseyHttpClientFactory implements HttpClientFactory {
    /**
     * Singleton instance of the {@link JerseyHttpClientFactory}.
     */
    static final JerseyHttpClientFactory instance = new JerseyHttpClientFactory()

    /**
     * Create a new Jersey HTTP client.
     *
     * @return A new Jersey 1.x {@link HttpClient} implementation.
     */
    @Override
    HttpClient createHttpClient() {
        return new JerseyHttpClient()
    }
}
