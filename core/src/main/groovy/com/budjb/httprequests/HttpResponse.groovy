package com.budjb.httprequests

import groovy.json.JsonSlurper

/**
 * An object that represents the response of an HTTP request.
 *
 * This object contains the entity of the response, if present, in memory. If an {@link InputStream} is desired,
 * configure the {@link HttpRequest#setThrowStatusExceptions} to <code>true</code>. When <code>true</code> an
 * implementation of this object that exposes the stream is returned from HTTP requests.
 */
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
     * Sets the character set of the response.
     *
     * @param charset Character set of the response.
     */
    void setCharset(String charset) {
        if (charset) {
            this.charset = charset
        }
    }

    /**
     * Return the entity of the response as a <code>String</code>.
     *
     * @return The entity of the response converted to a <code>String</code>.
     */
    String getEntityAsString() {
        return new String(getEntity(), charset)
    }

    /**
     * Parses the entity of the response as JSON and returns the resulting object.
     *
     * @return The entity of the response parsed as JSON.
     */
    Object getEntityAsJson() {
        return new JsonSlurper().parse(getEntity(), charset)
    }

    /**
     * Sets the response headers from the given map.
     *
     * @param headers Response headers of the request.
     */
    void setHeaders(Map<String, ?> headers) {
        headers.each { name, values ->
            setHeader(name, values)
        }
    }

    /**
     * Sets the response header with the given name, parsing out individual values.
     *
     * @param name Name of the header.
     * @param value Value(s) of the header.
     */
    void setHeader(String name, Object value) {
        if (!headers.containsKey(name)) {
            headers.put(name, [])
        }

        if (value instanceof Collection) {
            value.each {
                headers.get(name).add(it.toString())
            }
        }
        else {
            value.toString().split(/,\s*/).each {
                headers.get(name).add(it.toString())
            }
        }
    }

    /**
     * Returns the first value of the header with the given name, or null if it doesn't exist.
     *
     * @param name Name of the header.
     * @return The first value of the requested header, or null if it doesn't exist.
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
     * @param name Name of the header.
     * @return A list of values of the requested header, or null if it doesn't exist.
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
     * @return A copy of the map containing the response headers.
     */
    Map<String, List<String>> getHeaders() {
        return headers.clone() as Map<String, List<String>>
    }

    /**
     * Return all response headers.
     *
     * This method returns a map where the key is the name of the header, and the value is either the single value
     * for that header or a list of values if multiple were received.
     *
     * @return A copy of the map containing the response headers.
     */
    Map<String, Object> getFlattenedHeaders() {
        return headers.collectEntries { name, values ->
            if (values.size() == 1) {
                return [(name): values[0]]
            }
            else {
                return [(name): values]
            }
        } as Map<String, Object>
    }
}
