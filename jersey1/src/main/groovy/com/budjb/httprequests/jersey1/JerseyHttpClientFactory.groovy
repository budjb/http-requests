package com.budjb.httprequests.jersey1

import com.budjb.httprequests.HttpClient
import com.budjb.httprequests.HttpClientFactory
import com.budjb.httprequests.AbstractHttpClientFactory

/**
 * An {@link HttpClientFactory} implementation that creates Jersey 1.x HTTP clients.
 */
class JerseyHttpClientFactory extends AbstractHttpClientFactory {
    /**
     * Create a new Jersey HTTP client.
     *
     * @return A new Jersey 1.x {@link HttpClient} implementation.
     */
    @Override
    protected HttpClient createClientImplementation() {
        return new JerseyHttpClient()
    }
}
