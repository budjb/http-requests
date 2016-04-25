package com.budjb.httprequests

import com.budjb.httprequests.converter.*
import com.budjb.httprequests.filter.HttpClientFilter
import com.budjb.httprequests.filter.FilterManager

abstract class AbstractHttpClientFactory implements HttpClientFactory {
    /**
     * Instance of {@link ConverterManager} which will be used by the {@link HttpClient} and {@link HttpResponse}..
     */
    ConverterManager converterManager = new ConverterManager()

    /**
     * Filter manager.
     */
    FilterManager filterManager = new FilterManager()

    /**
     * Implementation factories should implement this method to create a concrete {@link HttpClient} instance specific
     * to the HTTP client implementation.
     *
     * @return A new {@link HttpClient} implementation instance.
     */
    abstract protected HttpClient createClientImplementation()

    /**
     * Base constructor that automatically registers the default set of entity converters.
     */
    AbstractHttpClientFactory() {
        this(true)
    }

    /**
     * Constructor that can optionally register the default set of entity converters.
     *
     * @param registerDefaultConverters Whether to register the default set of entity converters.
     */
    AbstractHttpClientFactory(boolean registerDefaultConverters) {
        if (registerDefaultConverters) {
            registerDefaultEntityConverters()
        }
    }

    /**
     * Add a default set of entity converters for commonly used types.
     */
    void registerDefaultEntityConverters() {
        addEntityConverter(new ByteArrayEntityReader())
        addEntityConverter(new ByteArrayEntityWriter())
        addEntityConverter(new StringEntityReader())
        addEntityConverter(new StringEntityWriter())
        addEntityConverter(new GStringEntityWriter())
        addEntityConverter(new JsonEntityReader())
        addEntityConverter(new JsonEntityWriter())
        addEntityConverter(new FormDataEntityWriter())
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
        client.setFilterManager(new FilterManager(filterManager))

        getFilters().each {
            client.addFilter(it)
        }

        return client
    }

    /**
     * Adds a {@link HttpClientFilter} to the HTTP client factory.
     *
     * Filters that are added to the factory are added to {@link HttpClient} objects that the factory creates.
     *
     * @param filter Filter instance to register with the client.
     * @return The object the method was called on.
     */
    @Override
    void addFilter(HttpClientFilter filter) {
        filterManager.add(filter)
    }

    /**
     * Returns the list of all registered {@link HttpClientFilter} instances.
     *
     * @return The list of registered filter instances.
     */
    @Override
    List<HttpClientFilter> getFilters() {
        return filterManager.getAll()
    }

    /**
     * Unregisters a {@link HttpClientFilter} from the HTTP client.
     *
     * @param filter Filter instance to remove from the client.
     * @return The object the method was called on.
     */
    @Override
    void removeFilter(HttpClientFilter filter) {
        filterManager.remove(filter)
    }

    /**
     * Removes all registered filters.
     */
    void clearFilters() {
        filterManager.clear()
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
