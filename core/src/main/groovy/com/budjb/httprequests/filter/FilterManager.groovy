package com.budjb.httprequests.filter

class FilterManager {
    /**
     * List of registered filters.
     */
    private final List<HttpClientFilter> filters

    /**
     * Base constructor.
     */
    FilterManager() {
        filters = []
    }

    /**
     * Creates a filter manager with the contents of another manager.
     *
     * @param other Other filter manager to make a copy of.
     */
    FilterManager(FilterManager other) {
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
     * Return a list of all registered {@link HttpClientEntityFilter} instances.
     *
     * @return A list of all registered {@link HttpClientEntityFilter} instances.
     */
    List<HttpClientEntityFilter> getEntityFilters() {
        return filters.findAll { it instanceof HttpClientEntityFilter } as List<HttpClientEntityFilter>
    }
}
