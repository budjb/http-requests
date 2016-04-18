package com.budjb.httprequests.jersey2

import com.budjb.httprequests.HttpClient
import com.budjb.httprequests.HttpClientFactory

class JerseyHttpClientFactory implements HttpClientFactory {
    /**
     * Create a new HTTP client.
     *
     * @return An {@link HttpClient} implementation instance.
     */
    @Override
    HttpClient createHttpClient() {
        return new JerseyHttpClient()
    }
}
