package com.budjb.httprequests.httpcomponents.client

import com.budjb.httprequests.AbstractHttpClientFactory
import com.budjb.httprequests.HttpClient

class HttpComponentsClientFactory extends AbstractHttpClientFactory {
    /**
     * Implementation factories should implement this method to create a concrete {@link HttpClient} instance specific
     * to the HTTP client implementation.
     *
     * @return A new {@link HttpClient} implementation instance.
     */
    @Override
    protected HttpClient createClientImplementation() {
        return new HttpComponentsHttpClient()
    }
}
