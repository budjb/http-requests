package com.budjb.httprequests

/**
 * Describes a factory class that creates an {@link HttpClient} instance. Individual HTTP client libraries
 * should implement this factory for the creation of HTTP clients.
 *
 * These factories are suitable for instantiation as Spring beans so that the factory can be injected where needed.
 */
interface HttpClientFactory {
    /**
     * Create a new HTTP client.
     *
     * @return An {@link HttpClient} implementation instance.
     */
    HttpClient createHttpClient()
}
