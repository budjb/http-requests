/*
 * Copyright 2016 Bud Byrd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.budjb.httprequests

import com.budjb.httprequests.converter.*
import com.budjb.httprequests.converter.bundled.ByteArrayEntityReader
import com.budjb.httprequests.converter.bundled.ByteArrayEntityWriter
import com.budjb.httprequests.converter.bundled.FormDataEntityWriter
import com.budjb.httprequests.converter.bundled.GStringEntityWriter
import com.budjb.httprequests.converter.bundled.JsonEntityReader
import com.budjb.httprequests.converter.bundled.JsonEntityWriter
import com.budjb.httprequests.converter.bundled.StringEntityReader
import com.budjb.httprequests.converter.bundled.StringEntityWriter
import com.budjb.httprequests.filter.HttpClientFilter
import com.budjb.httprequests.filter.HttpClientFilterManager

abstract class AbstractHttpClientFactory implements HttpClientFactory {
    /**
     * Instance of {@link EntityConverterManager} which will be used by the {@link HttpClient} and {@link HttpResponse}..
     */
    EntityConverterManager converterManager = new EntityConverterManager()

    /**
     * Filter manager.
     */
    HttpClientFilterManager filterManager = new HttpClientFilterManager()

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

        client.setConverterManager(new EntityConverterManager(converterManager))
        client.setFilterManager(new HttpClientFilterManager(filterManager))

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
