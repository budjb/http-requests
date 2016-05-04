package com.budjb.httprequests

/**
 * A context for HTTP requests that can be used by filters.
 */
class HttpContext {
    /**
     * A map containing data relevant to the request.
     */
    private final Map<String, Object> data = [:]

    /**
     * The request configuration
     */
    HttpRequest request

    /**
     * Method of the request.
     */
    HttpMethod method

    /**
     * The response properties.
     */
    HttpResponse response

    /**
     * The number of retries that have been attempted.
     */
    private int retries = 0

    /**
     * Retrieve an object with the given name from the context.
     *
     * @param name Name of the object.
     * @return The requested object, or <code>null</code> if not found.
     */
    Object get(String name) {
        if (data.containsKey(name)) {
            return data.get(name)
        }
        return null
    }

    /**
     * Retrieve an object with the given name and class type from the context.
     *
     * @param name Name of the object.
     * @param type Type of the object.
     * @return The requested object if found and matches the given type, or <code>null</code> otherwise.
     */
    public <T> T get(String name, Class<T> type) {
        Object object = get(name)

        if (object != null && type.isAssignableFrom(object.getClass())) {
            return object as T
        }

        return null
    }

    /**
     * Sets an object with the given name in the context.
     *
     * @param name Name of the object.
     * @param object Object to store.
     */
    void set(String name, Object object) {
        data.put(name, object)
    }

    /**
     * Increases the number of attempted retries.
     */
    void incrementRetries() {
        retries++
    }

    /**
     * Returns the number of retries that have been attempted.
     *
     * @return The number of retries that have been attempted.
     */
    int getRetries() {
        return retries
    }
}
