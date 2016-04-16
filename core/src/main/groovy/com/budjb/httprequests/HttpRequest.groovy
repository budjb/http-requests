package com.budjb.httprequests

import com.budjb.httprequests.exception.HttpStatusException

/**
 * An object used to configure an HTTP request.
 */
class HttpRequest {
    /**
     * URI of the request.
     */
    String uri

    /**
     * Request headers.
     */
    private final Map<String, List<String>> headers = [:]

    /**
     * Query parameters.
     */
    private final Map<String, List<String>> queryParameters = [:]

    /**
     * Content type of the request.
     */
    String contentType

    /**
     * Requested content type of the response.
     */
    String accept

    /**
     * Character set.
     */
    String charset = 'UTF-8'

    /**
     * The read timeout of the HTTP connection, in milliseconds. Defaults to 0 (infinity).
     */
    int readTimeout = 0

    /**
     * The connection timeout of the HTTP connection, in milliseconds. Defaults to 0 (infinity).
     */
    int connectionTimeout = 0

    /**
     * Whether SSL certificates will be validated.
     */
    boolean sslValidated = true

    /**
     * Whether the client should throw an exception for a non-2XX HTTP status.
     */
    boolean throwStatusExceptions = true

    /**
     * Whether the client should automatically follow redirects.
     */
    boolean followRedirects = true

    /**
     * Whether the HTTP client should log the request communication.
     *
     * Note that the logging behavior is specific to the HTTP client implementation in use.
     */
    boolean logConversation = false

    /**
     * Whether to automatically buffer the response entity in this object as a byte array. When <code>true</code>,
     * the {@link HttpResponse#inputStream} is automatically closed.
     */
    boolean autoBufferEntity = true

    /**
     * Construct a new {@link HttpRequest} object, configured with the given closure.
     *
     * @param closure
     * @return
     */
    static HttpRequest build(Closure closure) {
        HttpRequest request = new HttpRequest()

        closure = closure.clone() as Closure
        closure.delegate = request
        closure.call()

        return request
    }

    /**
     * Base constructor.
     */
    HttpRequest() { void }

    /**
     * Constructor that builds the request from a URI.
     *
     * @param uri URI of the request.
     */
    HttpRequest(URI uri) {
        parseUri(uri)
    }

    /**
     * Constructor that builds the request from a URI string.
     *
     * @param uri URI of the request.
     */
    HttpRequest(String uri) {
        parseUri(uri)
    }

    /**
     * Sets the URI of the request.
     *
     * Note that query parameters will reset to what is contained in the URI string.
     *
     * @param uri URI of the request.
     * @return The instance of this class the method was called with.
     */
    HttpRequest setUri(String uri) {
        parseUri(uri)
        return this
    }

    /**
     * Sets the URI of the request.
     *
     * Note that query parameters will reset to what is contained in the URI.
     *
     * @param uri URI of the request.
     * @return The instance of this class the method was called with.
     */
    HttpRequest setUri(URI uri) {
        parseUri(uri)
        return this
    }

    /**
     * Returns the request headers.
     *
     * @return A copy of the map containing the request headers.
     */
    Map<String, List<String>> getHeaders() {
        return headers.clone() as Map<String, List<String>>
    }

    /**
     * Add a single header to the request.
     *
     * @param name Name of the header.
     * @param value Value of the header.
     * @return The instance of this class the method was called with.
     */
    HttpRequest addHeader(String name, String value) {
        if (!headers.containsKey(name)) {
            headers.put(name, [])
        }
        headers[name] << value
        return this
    }

    /**
     * Add several headers with the same name to the request.
     *
     * @param String Name of the header.
     * @param values A <code>List</code> of values of the header.
     * @return The instance of this class the method was called with.
     */
    HttpRequest addHeader(String name, List<String> values) {
        if (!headers.containsKey(name)) {
            headers.put(name, [])
        }
        headers[name].addAll(values)
        return this
    }

    /**
     * Add many headers to the request.
     *
     * @param headers A map of headers, where the key is the name of the header and the value is either a
     *                <code>String</code> or a <code>List</code> of <code>String</code>s.
     * @return The instance of this class the method was called with.
     */
    HttpRequest addHeaders(Map<String, List<String>> headers) {
        this.headers.putAll(headers)
        return this
    }

    /**
     * Overwrites the given header and sets it to the given value.
     *
     * @param name Name of the header.
     * @param value Value of the header.
     * @return The instance of this class the method was called with.
     */
    HttpRequest setHeader(String name, String value) {
        return setHeader(name, [value])
    }

    /**
     * Overwrites the given header and sets it to the given list of values.
     *
     * @param name Name of the header.
     * @param values A <code>List</code> of values of the header.
     * @return The instance of this class the method was called with.
     */
    HttpRequest setHeader(String name, List<String> values) {
        headers.put(name, values)
        return this
    }

    /**
     * Sets the request headers to the given map of headers.
     *
     * @param headers Map of headers.
     * @return The instance of this class the method was called with.
     */
    HttpRequest setHeaders(Map headers) {
        this.headers.clear()
        headers.each { name, values ->
            if (values instanceof Collection) {
                values.each { value ->
                    addHeader(name.toString(), value.toString())
                }
            }
            else {
                addHeader(name.toString(), values.toString())
            }
        }
        return this
    }

    /**
     * Returns the query parameters of the request.
     *
     * @return A copy of the map containing the query parameters.
     */
    Map<String, List<String>> getQueryParameters() {
        return queryParameters.clone() as Map<String, List<String>>
    }

    /**
     * Add a single query parameter to the request.
     *
     * @param name Name of the query parameter.
     * @param value Value of the query parameter.
     * @return The instance of this class the method was called with.
     */
    HttpRequest addQueryParameter(String name, String value) {
        if (!queryParameters.containsKey(name)) {
            queryParameters.put(name, [])
        }
        queryParameters[name] << value
        return this
    }

    /**
     * Add several query parameters with the same name to the request.
     *
     * @param String Name of the query parameter.
     * @param values A <code>List</code> of values of the query parameter.
     * @return The instance of this class the method was called with.
     */
    HttpRequest addQueryParameter(String name, List<String> values) {
        if (!queryParameters.containsKey(name)) {
            queryParameters.put(name, [])
        }
        queryParameters[name].addAll(values)
        return this
    }

    /**
     * Add many query parameters to the request.
     *
     * @param parameters A map of query parameters, where the key is the name of the query parameter and the value is
     *                   either a <code>String</code> or a <code>List</code> of <code>String</code>s.
     * @return The instance of this class the method was called with.
     */
    HttpRequest addQueryParameters(Map<String, List<String>> parameters) {
        queryParameters.putAll(parameters)
        return this
    }

    /**
     * Sets the query parameter with the given name.
     *
     * Note that this will overwrite any existing query parameter value(s).
     *
     * @param name Name of the query parameter.
     * @param value Value of the query parameter.
     * @return The instance of this class the method was called with.
     */
    HttpRequest setQueryParameter(String name, String value) {
        return setQueryParameter(name, [value])
    }

    /**
     * Sets the query parameter with the given name.
     *
     * Note that this will overwrite any existing query parameter value(s).

     * @param name Name of the query parameter.
     * @param values A <code>List</code> of values of the query parameter.
     * @return The instance of this class the method was called with.
     */
    HttpRequest setQueryParameter(String name, List<String> values) {
        queryParameters.put(name, values)
        return this
    }

    /**
     * Sets the request query parameters to the given map of query parameters.
     *
     * @param queryParameters Map of headers.
     * @return The instance of this class the method was called with.
     */
    HttpRequest setQueryParameters(Map queryParameters) {
        this.queryParameters.clear()
        queryParameters.each { name, values ->
            if (values instanceof Collection) {
                values.each { value ->
                    addQueryParameter(name.toString(), value.toString())
                }
            }
            else {
                addQueryParameter(name.toString(), values.toString())
            }
        }
        return this
    }

    /**
     * Sets the Content-Type of the request.
     *
     * @param contentType Content-Type of the request.
     * @return The instance of this class the method was called with.
     */
    HttpRequest setContentType(String contentType) {
        this.contentType = contentType
        return this
    }

    /**
     * Sets the requested Content-Type of the response.
     *
     * @param accept Requested Content-Type of the response.
     * @return The instance of this class the method was called with.
     */
    HttpRequest setAccept(String accept) {
        this.accept = accept
        return this
    }

    /**
     * Sets whether SSL will be validated.
     *
     * @param sslValidated Whether SSL will be validated.
     * @return The instance of this class the method was called with.
     */
    HttpRequest setSslValidated(boolean sslValidated) {
        this.sslValidated = sslValidated
        return this
    }

    /**
     * Sets whether the client will throw {@link HttpStatusException} for non-2XX HTTP statuses.
     *
     * @param throwStatusExceptions Whether the client will throw
     * @return The instance of this class the method was called with.
     */
    HttpRequest setThrowStatusExceptions(boolean throwStatusExceptions) {
        this.throwStatusExceptions = throwStatusExceptions
        return this
    }

    /**
     * Set the character set of the request.
     *
     * @param charSet Character set of the request.
     * @return The instance of this class the method was called with.
     */
    HttpRequest setCharset(String charSet) {
        this.charset = charSet
        return this
    }

    /**
     * Sets the read timeout, in milliseconds. 0 means infinity.
     *
     * @param timeout Read timeout of the request, in milliseconds.
     * @return The instance of this class the method was called with.
     */
    HttpRequest setReadTimeout(int timeout) {
        this.readTimeout = timeout
        return this
    }

    /**
     * Sets the connection timeout, in milliseconds. 0 means infinity.
     *
     * @param timeout Connection timeout of the request, in milliseconds.
     * @return The instance of this class the method was called with.
     */
    HttpRequest setConnectionTimeout(int timeout) {
        this.connectionTimeout = timeout
        return this
    }

    /**
     * Sets whether the client should follow redirects.
     *
     * @param followRedirects Whether the client should follow redirects.
     * @return The instance of this class the method was called with.
     */
    HttpRequest setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects
        return this
    }

    /**
     * Parses the given URI and applies its properties to the HTTP request properties.
     *
     * @param uri URI as a <code>String</code>.
     */
    protected void parseUri(String uri) {
        parseUri(new URI(uri))
    }

    /**
     * Parses the given URI and applies its properties to the HTTP request properties.
     *
     * @param uri URI object to parse.
     */
    protected void parseUri(URI uri) {
        queryParameters.clear()

        String scheme = uri.getScheme()
        String host = uri.getHost()
        int port = uri.getPort()
        String path = uri.getPath()
        String query = uri.getQuery()

        StringBuilder builder = new StringBuilder(scheme).append('://').append(host)

        if (port != -1 && !(scheme == 'https' && port == 443) && !(scheme == 'http' && port == 80)) {
            builder = builder.append(':').append(port)
        }

        builder.append(path)

        this.uri = builder.toString()

        if (query) {
            query.split('&').each {
                List<String> parts = it.split('=')

                if (parts.size() == 1) {
                    addQueryParameter(parts[0], '')
                }
                else {
                    String name = parts.remove(0)
                    addQueryParameter(name, parts.join('='))
                }
            }
        }
    }
}