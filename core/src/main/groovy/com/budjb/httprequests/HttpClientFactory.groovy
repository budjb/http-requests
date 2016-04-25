package com.budjb.httprequests

import com.budjb.httprequests.converter.EntityConverter
import com.budjb.httprequests.filter.HttpClientFilter

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

    /**
     * Adds a {@link HttpClientFilter}.
     *
     * Filters that are added to the factory are added to {@link HttpClient} objects that the factory creates.
     *
     * @param filter Filter instance to register.
     */
    void addFilter(HttpClientFilter filter)

    /**
     * Returns the list of all registered {@link HttpClientFilter} instances.
     *
     * @return The list of registered filter instances.
     */
    List<HttpClientFilter> getFilters()

    /**
     * Unregisters a {@link HttpClientFilter}.
     *
     * @param filter Filter instance to remove.
     */
    void removeFilter(HttpClientFilter filter)

    /**
     * Removes all registered filters.
     */
    void clearFilters()

    /**
     * Adds an entity converter to the factory.
     *
     * @param converter Converter to add to the factory.
     */
    void addEntityConverter(EntityConverter converter)

    /**
     * Returns the list of entity converters.
     *
     * @return List of entity converters.
     */
    List<EntityConverter> getEntityConverters()

    /**
     * Remove an entity converter.
     *
     * @param converter Entity converter to remove.
     */
    void removeEntityConverter(EntityConverter converter)

    /**
     * Remove all entity converters.
     */
    void clearEntityConverters()
}
