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
package com.budjb.httprequests.filter

import com.budjb.httprequests.HttpContext

class HttpClientFilterManager {
    /**
     * List of registered filters.
     */
    private final List<HttpClientFilter> filters

    /**
     * Base constructor.
     */
    HttpClientFilterManager() {
        filters = []
    }

    /**
     * Creates a filter manager with the contents of another manager.
     *
     * @param other Other filter manager to make a copy of.
     */
    HttpClientFilterManager(HttpClientFilterManager other) {
        filters = []

        other.getAll().each {
            add(it)
        }
    }

    /**
     * Adds a filter to the manager.
     *
     * @param filter Filter to add to the manager.
     */
    void add(HttpClientFilter filter) {
        if (!filters.find { it.getClass() == filter.getClass() }) {
            filters.add(filter)
        }
    }

    /**
     * Returns the list of registered filters.
     *
     * @return List of registered filters.
     */
    List<HttpClientFilter> getAll() {
        return filters
    }

    /**
     * Remove a filter.
     *
     * @param filter Filter to remove.
     */
    void remove(HttpClientFilter filter) {
        filters.remove(filter)
    }

    /**
     * Remove all filters.
     */
    void clear() {
        filters.clear()
    }

    /**
     * Return a list of all registered {@link HttpClientRequestFilter} instances.
     *
     * @return All registered {@link HttpClientRequestFilter} instances.
     */
    List<HttpClientRequestFilter> getRequestFilters() {
        return filters.findAll { it instanceof HttpClientRequestFilter } as List<HttpClientRequestFilter>
    }

    /**
     * Return a list of all registered {@link HttpClientResponseFilter} instances.
     *
     * @return A list of all registered {@link HttpClientResponseFilter} instances.
     */
    List<HttpClientResponseFilter> getResponseFilters() {
        return filters.findAll { it instanceof HttpClientResponseFilter } as List<HttpClientResponseFilter>
    }

    /**
     * Return a list of all registered {@link HttpClientRetryFilter} instances.
     *
     * @return A list of all registered {@link HttpClientRetryFilter} instances.
     */
    List<HttpClientRetryFilter> getRetryFilters() {
        return filters.findAll { it instanceof HttpClientRetryFilter } as List<HttpClientRetryFilter>
    }

    /**
     * Return a list of all registered {@link HttpClientRequestEntityFilter} instances.
     *
     * @return A list of all registered {@link HttpClientRequestEntityFilter} instances.
     */
    List<HttpClientRequestEntityFilter> getRequestEntityFilters() {
        return filters.findAll { it instanceof HttpClientRequestEntityFilter } as List<HttpClientRequestEntityFilter>
    }

    /**
     * Return a list of all registered {@link HttpClientLifecycleFilter} instances.
     *
     * @return A list of all registered {@link HttpClientLifecycleFilter} instances.
     */
    List<HttpClientLifecycleFilter> getLifecycleFilters() {
        return filters.findAll { it instanceof HttpClientLifecycleFilter } as List<HttpClientLifecycleFilter>
    }

    /**
     * Calls {@link HttpClientRequestFilter#filterHttpRequest} for all registered filters.
     *
     * @param context HTTP request context.
     */
    void filterHttpRequest(HttpContext context) {
        getRequestFilters()*.filterHttpRequest(context)
    }

    /**
     * Calls {@link HttpClientLifecycleFilter#onRequest} for all registered filters.
     *
     * @param context HTTP request context.
     * @param outputStream The output stream of the request.
     */
    void onRequest(HttpContext context, OutputStream outputStream) {
        getLifecycleFilters()*.onRequest(context, outputStream)
    }

    /**
     * Calls {@link HttpClientLifecycleFilter#onResponse} for all registered filters.
     *
     * @param context HTTP request context.
     */
    void onResponse(HttpContext context) {
        getLifecycleFilters()*.onResponse(context)
    }

    /**
     * Calls {@link HttpClientResponseFilter#filterHttpResponse} for all registered filters.
     *
     * @param context HTTP request context.
     */
    void filterHttpResponse(HttpContext context) {
        getResponseFilters()*.filterHttpResponse(context)
    }

    /**
     * Filters the {@link OutputStream} of the request.
     *
     * @param context HTTP request properties.
     * @param outputStream {@link OutputStream} of the request.
     * @return Filtered {@link OutputStream}.
     */
    OutputStream filterRequestEntity(HttpContext context, OutputStream outputStream) {
        getRequestEntityFilters().each {
            outputStream = it.filterRequestEntity(context, outputStream)
        }
        return outputStream
    }
}
