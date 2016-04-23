package com.budjb.httprequests.jersey2

import com.budjb.httprequests.HttpClient
import com.budjb.httprequests.HttpClientFactory
import com.budjb.httprequests.listener.AbstractHttpClientFactory

class JerseyHttpClientFactory extends AbstractHttpClientFactory {
    /**
     * Create a new HTTP client.
     *
     * @return An {@link HttpClient} implementation instance.
     */
    @Override
    protected HttpClient createClientImplementation() {
        return new JerseyHttpClient()
    }
}
