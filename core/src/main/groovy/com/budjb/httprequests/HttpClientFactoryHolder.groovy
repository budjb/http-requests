package com.budjb.httprequests

/**
 * A holder class that is useful to contain an instance of {@link HttpClientFactory} and make it available to the rest
 * of an application as a global singleton.
 *
 * It also serves to isolate the rest of the application from having to be aware of the specific HTTP client
 * implementation, since the {@link HttpClientFactory} can be created and stored during an application's initialization,
 * and classes that use this holder will no longer need to create a factory instance any time an {@link HttpClient} is
 * required.
 *
 * This class is useful in applications that do not use Spring Framework, but for those applications that are using
 * Spring Framework, a better option is to register the {@link HttpClientFactory} implementation as a bean that can be
 * injected into other Spring beans. This holder effectively serves to provide global access to a factory object in
 * the absence of Spring Framework bean injection.
 */
class HttpClientFactoryHolder {
    /**
     * Singleton instance of the holder.
     */
    private static final instance = new HttpClientFactoryHolder()

    /**
     * Instance of the HTTP client factory contained in the holder.
     */
    private HttpClientFactory factory

    /**
     * Returns the {@link HttpClientFactory} registered with the holder.
     *
     * An {@link IllegalStateException} will be thrown if there is no client registered.
     *
     * @return The {@link HttpClientFactory} registered with the holder.
     */
    static HttpClientFactory getHttpClientFactory() {
        if (!instance.factory) {
            throw new IllegalStateException("no HttpClientFactory is registered with the HttpClientFactoryHolder")
        }
        return instance.factory
    }
}
