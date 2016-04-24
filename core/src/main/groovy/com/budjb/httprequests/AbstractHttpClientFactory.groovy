package com.budjb.httprequests

import com.budjb.httprequests.converter.*
import com.budjb.httprequests.listener.HttpClientListener
import com.budjb.httprequests.listener.ListenerManager

abstract class AbstractHttpClientFactory implements HttpClientFactory {
    /**
     * Instance of {@link ConverterManager} which will be used by the {@link HttpClient} and {@link HttpResponse}..
     */
    ConverterManager converterManager = new ConverterManager()

    /**
     * Listener manager.
     */
    ListenerManager listenerManager = new ListenerManager()

    /**
     * Implementation factories should implement this method to create a concrete {@link HttpClient} instance specific
     * to the HTTP client implementation.
     *
     * @return A new {@link HttpClient} implementation instance.
     */
    abstract protected HttpClient createClientImplementation()

    /**
     * Constructor.
     */
    AbstractHttpClientFactory() {
        registerDefaultEntityConverters()
    }

    /**
     * Add a default set of entity converters for commonly used types.
     */
    protected void registerDefaultEntityConverters() {
        addEntityConverter(new StringEntityReader())
        addEntityConverter(new StringEntityWriter())
        addEntityConverter(new GStringEntityWriter())
        addEntityConverter(new JsonEntityReader())
        addEntityConverter(new JsonEntityWriter())
    }

    /**
     * Create a new HTTP client.
     *
     * @return An {@link HttpClient} implementation instance.
     */
    @Override
    HttpClient createHttpClient() {
        HttpClient client = createClientImplementation()

        client.setConverterManager(new ConverterManager(converterManager))
        client.setListenerManager(new ListenerManager(listenerManager))

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
        listenerManager.add(listener)
    }

    /**
     * Returns the list of all registered {@link HttpClientListener} instances.
     *
     * @return The list of registered listener instances.
     */
    @Override
    List<HttpClientListener> getListeners() {
        return listenerManager.getAll()
    }

    /**
     * Unregisters a {@link HttpClientListener} from the HTTP client.
     *
     * @param listener Listener instance to remove from the client.
     * @return The object the method was called on.
     */
    @Override
    void removeListener(HttpClientListener listener) {
        listenerManager.remove(listener)
    }

    /**
     * Removes all registered listeners.
     */
    void clearListeners() {
        listenerManager.clear()
    }

    /**
     * Adds an entity converter to the factory.
     *
     * @param converter Converter to add to the factory.
     */
    void addEntityConverter(EntityConverter converter) {
        converterManager.add(converter)
    }

    /**
     * Returns the list of entity converters.
     *
     * @return List of entity converters.
     */
    List<EntityConverter> getEntityConverters() {
        return converterManager.getAll()
    }

    /**
     * Remove an entity converter.
     *
     * @param converter Entity converter to remove.
     */
    void removeEntityConverter(EntityConverter converter) {
        converterManager.remove(converter)
    }

    /**
     * Remove all entity converters.
     */
    void clearEntityConverters() {
        converterManager.clear()
    }
}
