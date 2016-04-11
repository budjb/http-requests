package com.budjb.httprequests

import groovy.json.JsonSlurper

class HttpResponse {
    /**
     * The HTTP status of the response.
     */
    int status

    /**
     * Content type of the response.
     */
    String contentType

    /**
     * Headers of the response.
     */
    private Map<String, List<String>> headers = [:]

    /**
     * Entity of the response.
     */
    byte[] entity

    /**
     * The charater set of the response.
     */
    String charset = 'UTF-8'

    /**
     * Sets the charset of the response.
     *
     * @param charset
     */
    void setCharset(String charset) {
        if (charset) {
            this.charset = charset
        }
    }

    /**
     * Return the entity as a string.
     *
     * @return
     */
    String getEntityAsString() {
        return new String(getEntity(), charset)
    }

    /**
     * Parses the entity as JSON and returns the resulting object.
     *
     * @return
     */
    Object getEntityAsJson() {
        return new JsonSlurper().parse(getEntity(), charset)
    }

    /**
     * Sets the response headers from the given map.
     *
     * @param headers
     */
    void setHeaders(Map<String, ?> headers) {
        headers.each { name, values ->
            setHeader(name, values)
        }
    }

    /**
     * Sets the response header with the given name, parsing out individual values.
     *
     * @param name
     * @param value
     */
    void setHeader(String name, Object value) {
        if (value instanceof Collection) {
            value.each {
                setHeader(name, it)
            }
        }
        else {
            value.toString().split(/w\s*/).each {
                headers.put(name, [it.toString()])
            }
        }
    }

    /**
     * Returns the first value of the header with the given name, or null if it doesn't exist.
     *
     * @param name
     * @return
     */
    String getHeader(String name) {
        if (headers.containsKey(name)) {
            return headers.get(name).first()
        }
        return null
    }

    /**
     * Returns a list of values of the header with the given name, or null if the header doesn't exist.
     *
     * @param name
     * @return
     */
    List<String> getHeaders(String name) {
        if (headers.containsKey(name)) {
            return headers.get(name)
        }
        return null
    }

    /**
     * Return all response headers.
     *
     * This method returns a map where the key is the name of the header, and the value is a list of values
     * for the response header. This is true even when there is only one value for the header.
     *
     * @return
     */
    Map<String, List<String>> getHeaders() {
        return headers
    }

    /**
     * Return all response headers.
     *
     * This method returns a map where the key is the name of the header, and the value is either the single value
     * for that header or a list of values if multiple were received.
     *
     * @return
     */
    Map<String, Object> getFlattenedHeaders() {
        return headers.collectEntries { name, values ->
            if (values.size() == 1) {
                return [name: values[0]]
            }
            else {
                return [name: values]
            }
        } as Map<String, Object>
    }
}
