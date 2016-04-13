package com.budjb.httprequests

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
     * Whether the response should expose the response entity as an input stream. When true, a response of type
     * {@link StreamingHttpResponse} is returned. It is important to note that these object needs to be closed
     * before they are disposed of.
     */
    boolean streamingResponse = false

    /**
     * Base constructor.
     */
    HttpRequest() { void }

    /**
     * Constructor that builds the request from a URI.
     *
     * @param uri
     */
    HttpRequest(URI uri) {
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

    /**
     * Sets the URI of the request.
     *
     * @param uri
     */
    HttpRequest setUri(String uri) {
        this.uri = uri
        return this
    }

    /**
     * Returns the request headers.
     *
     * @return
     */
    Map<String, List<String>> getHeaders() {
        return headers.clone() as Map<String, List<String>>
    }

    /**
     * Add a single header to the request.
     *
     * @param name
     * @param value
     * @return
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
     * @param String
     * @param values
     * @return
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
     * @param headers
     * @return
     */
    HttpRequest addHeader(Map<String, List<String>> headers) {
        this.headers.putAll(headers)
        return this
    }

    /**
     * Overwrites the given header and sets it to the given value.
     *
     * @param name
     * @param value
     * @return
     */
    HttpRequest setHeader(String name, String value) {
        return setHeader(name, [value])
    }

    /**
     * Overwrites the given header and sets it to the given list of values.
     *
     * @param name
     * @param values
     * @return
     */
    HttpRequest setHeader(String name, List<String> values) {
        headers.put(name, values)
        return this
    }

    /**
     * Returns the query parameters of the request.
     *
     * @return
     */
    Map<String, List<String>> getQueryParameters() {
        return queryParameters.clone() as Map<String, List<String>>
    }

    /**
     * Add a single query parameter to the request.
     *
     * @param name
     * @param value
     * @return
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
     * @param String
     * @param values
     * @return
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
     * @param parameters
     * @return
     */
    HttpRequest addQueryParameter(Map<String, List<String>> parameters) {
        queryParameters.putAll(parameters)
        return this
    }

    /**
     * Sets the query parameter with the given name.
     *
     * Note that this will overwrite any existing query parameter value(s).
     *
     * @param name
     * @param value
     * @return
     */
    HttpRequest setQueryParameter(String name, String value) {
        return setQueryParameter(name, [value])
    }

    /**
     * Sets the query parameter with the given name.
     *
     * Note that this will overwrite any existing query parameter value(s).

     * @param name
     * @param values
     * @return
     */
    HttpRequest setQueryParameter(String name, List<String> values) {
        queryParameters.put(name, values)
        return this
    }

    /**
     * Sets the content type of the request.
     *
     * @param contentType
     * @return
     */
    HttpRequest setContentType(String contentType) {
        this.contentType = contentType
        return this
    }

    /**
     * Sets the requested content type of the response.
     *
     * @param accept
     * @return
     */
    HttpRequest setAccept(String accept) {
        this.accept = accept
        return this
    }

    /**
     * Sets whether SSL will be validated.
     *
     * @param sslValidated
     * @return
     */
    HttpRequest setSslValidated(boolean sslValidated) {
        this.sslValidated = sslValidated
        return this
    }

    /**
     * Sets whether the client will throw HTTP status exceptions for non-2XX HTTP statuses.
     *
     * @param throwStatusExceptions
     * @return
     */
    HttpRequest setThrowStatusExceptions(boolean throwStatusExceptions) {
        this.throwStatusExceptions = throwStatusExceptions
        return this
    }

    /**
     * Set the character set of the request.
     *
     * @param charSet
     * @return
     */
    HttpRequest setCharset(String charSet) {
        this.charset = charSet
        return this
    }

    /**
     * Sets the read timeout, in milliseconds. 0 means infinity.
     *
     * @param timeout
     * @return
     */
    HttpRequest setReadTimeout(int timeout) {
        this.readTimeout = timeout
        return this
    }

    /**
     * Sets the connection timeout, in milliseconds. 0 means infinity.
     *
     * @param timeout
     * @return
     */
    HttpRequest setConnectionTimeout(int timeout) {
        this.connectionTimeout = timeout
        return this
    }

    /**
     * Sets whether the client should follow redirects.
     *
     * @param followRedirects
     * @return
     */
    HttpRequest setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects
        return this
    }
}