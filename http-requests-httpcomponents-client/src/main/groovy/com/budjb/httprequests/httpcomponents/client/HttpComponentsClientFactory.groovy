package com.budjb.httprequests.httpcomponents.client

import com.budjb.httprequests.AbstractHttpClientFactory
import com.budjb.httprequests.HttpClient

class HttpComponentsClientFactory extends AbstractHttpClientFactory {
    /**
     * Base constructor that automatically registers the default set of entity converters.
     */
    HttpComponentsClientFactory() {
        super()
    }

    /**
     * Constructor that can optionally register the default set of entity converters.
     *
     * @param registerDefaultConverters Whether to register the default set of entity converters.
     */
    HttpComponentsClientFactory(boolean registerDefaultConverters) {
        super(registerDefaultConverters)
    }

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
