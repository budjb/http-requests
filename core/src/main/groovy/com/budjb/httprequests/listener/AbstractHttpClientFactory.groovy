package com.budjb.httprequests.listener

import com.budjb.httprequests.HttpClient
import com.budjb.httprequests.HttpClientFactory

abstract class AbstractHttpClientFactory implements HttpClientFactory {
    /**
     * List of registered {@link HttpClientListener} objects.
     */
    private final List<HttpClientListener> listeners = []

    /**
     * Implementation factories should implement this method to create a concrete {@link HttpClient} instance specific
     * to the HTTP client implementation.
     *
     * @return A new {@link HttpClient} implementation instance.
     */
    abstract protected HttpClient createClientImplementation()

    /**
     * Create a new HTTP client.
     *
     * @return An {@link HttpClient} implementation instance.
     */
    @Override
    HttpClient createHttpClient() {
        HttpClient client = createClientImplementation()

        getListeners().each {
            client.addListener(it)
        }

        return client
    }

    /**
     * Adds a {@link com.budjb.httprequests.listener.HttpClientListener} to the HTTP client factory.
     *
     * Listeners that are added to the factory are added to {@link HttpClient} objects that the factory creates.
     *
     * @param listener Listener instance to register with the client.
     * @return The object the method was called on.
     */
    @Override
    void addListener(HttpClientListener listener) {
        listeners.add(listener)
    }

    /**
     * Returns the list of all registered {@link HttpClientListener} instances.
     *
     * @return The list of registered listener instances.
     */
    @Override
    List<HttpClientListener> getListeners() {
        return listeners
    }

    /**
     * Unregisters a {@link HttpClientListener} from the HTTP client.
     *
     * @param listener Listener instance to remove from the client.
     * @return The object the method was called on.
     */
    @Override
    void removeListener(HttpClientListener listener) {
        listeners.remove(listener)
    }

    /**
     * Removes all registered listeners.
     */
    void clearListeners() {
        listeners.clear()
    }
}
