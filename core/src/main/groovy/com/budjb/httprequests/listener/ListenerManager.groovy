package com.budjb.httprequests.listener

class ListenerManager {
    /**
     * List of registered listeners.
     */
    private final List<HttpClientListener> listeners

    /**
     * Base constructor.
     */
    ListenerManager() {
        listeners = []
    }

    /**
     * Creates a listener manager with the contents of another manager.
     *
     * @param other Other listener manager to make a copy of.
     */
    ListenerManager(ListenerManager other) {
        listeners = []

        other.getAll().each {
            add(it)
        }
    }

    /**
     * Adds a listener to the manager.
     *
     * @param listener Listener to add to the manager.
     */
    void add(HttpClientListener listener) {
        if (!listeners.find { it.getClass() == listener.getClass() }) {
            listeners.add(listener)
        }
    }

    /**
     * Returns the list of registered listeners.
     *
     * @return List of registered listeners.
     */
    List<HttpClientListener> getAll() {
        return listeners
    }

    /**
     * Remove a listener.
     *
     * @param listener listener to remove.
     */
    void remove(HttpClientListener listener) {
        listeners.remove(listener)
    }

    /**
     * Remove all listeners.
     */
    void clear() {
        listeners.clear()
    }

    /**
     * Return a list of all registered {@link HttpClientRequestListener} instances.
     *
     * @return All registered {@link HttpClientRequestListener} instances.
     */
    List<HttpClientRequestListener> getRequestListeners() {
        return listeners.findAll { it instanceof HttpClientRequestListener } as List<HttpClientRequestListener>
    }

    /**
     * Return a list of all registered {@link HttpClientResponseListener} instances.
     *
     * @return A list of all registered {@link HttpClientResponseListener} instances.
     */
    List<HttpClientResponseListener> getResponseListeners() {
        return listeners.findAll { it instanceof HttpClientResponseListener } as List<HttpClientResponseListener>
    }

    /**
     * Return a list of all registered {@link HttpClientRetryListener} instances.
     *
     * @return A list of all registered {@link HttpClientRetryListener} instances.
     */
    List<HttpClientRetryListener> getRetryListeners() {
        return listeners.findAll { it instanceof HttpClientRetryListener } as List<HttpClientRetryListener>
    }
}
