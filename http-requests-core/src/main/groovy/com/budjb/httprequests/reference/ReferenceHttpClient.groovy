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
package com.budjb.httprequests.reference

import com.budjb.httprequests.*

import javax.net.ssl.HttpsURLConnection

class ReferenceHttpClient extends AbstractHttpClient {
    /**
     * Implements the logic to make an actual request with an HTTP client library.
     *
     * @param context HTTP request context.
     * @param inputStream An {@link InputStream} containing the response body. May be <code>null</code>.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    protected HttpResponse doExecute(HttpContext context, InputStream inputStream) throws IOException {
        HttpRequest request = context.getRequest()

        URI uri = createURI(request)

        HttpURLConnection connection = uri.toURL().openConnection() as HttpURLConnection

        if (connection instanceof HttpsURLConnection && !request.isSslValidated()) {
            connection.setHostnameVerifier(createTrustingHostnameVerifier())
            connection.setSSLSocketFactory(createTrustingSSLContext().getSocketFactory())
        }

        connection.setRequestMethod(context.getMethod().toString())
        connection.setConnectTimeout(request.getConnectionTimeout())
        connection.setReadTimeout(request.getReadTimeout())
        connection.setInstanceFollowRedirects(request.isFollowRedirects())

        request.getHeaders().each { key, values ->
            if (values instanceof Collection) {
                connection.setRequestProperty(key, values.join(','))
            }
            else {
                connection.setRequestProperty(key, values.toString())
            }
        }

        if (inputStream && request.getFullContentType()) {
            connection.setRequestProperty('Content-Type', request.getFullContentType())
        }

        if (request.getAccept()) {
            connection.setRequestProperty('Accept', request.getAccept())
        }

        if (methodSupportsResponseEntity(context.getMethod()) && inputStream != null) {
            connection.setDoInput(true)
        }

        if (inputStream) {
            if (methodSupportsRequestEntity(context.getMethod())) {
                connection.setDoOutput(true)
                OutputStream outputStream = filterOutputStream(context, connection.getOutputStream())
                StreamUtils.shovel(inputStream, outputStream)
                inputStream.close()
                outputStream.close()
            }
            else {
                inputStream.close()
            }
        }


        return new ReferenceHttpResponse(request, converterManager, connection)
    }

    /**
     * Determines whether the given method supports a request entity.
     *
     * @param method
     * @return
     */
    protected methodSupportsRequestEntity(HttpMethod method) {
        if (method in [HttpMethod.GET, HttpMethod.DELETE]) {
            return false
        }
        return true
    }

    protected methodSupportsResponseEntity(HttpMethod method) {
        if (method in [HttpMethod.HEAD]) {
            return false
        }
        return true
    }

    protected URI createURI(HttpRequest request) {
        URI uri = new URI(request.getUri())

        Map<String, List<String>> queryParameters = [:]

        String existingQuery = uri.getQuery()
        if (existingQuery) {
            existingQuery.tokenize('&').each {
                List parts = it.tokenize('=')
                if (!queryParameters.containsKey(parts[0])) {
                    queryParameters.put(parts[0], [])
                }
                if (parts.size() > 1) {
                    queryParameters.get(parts[0]).add(parts[1])
                }
            }
        }
        request.getQueryParameters().each { key, values ->
            if (!queryParameters.containsKey(key)) {
                queryParameters.put(key, [])
            }
            if (values instanceof Collection) {
                values.each { value ->
                    queryParameters.get(key).add(value)
                }
            }
            else {
                queryParameters.get(key).add(values.toString())
            }
        }

        List<String> parts = []
        queryParameters.each { key, values ->
            if (!values.size()) {
                parts.add("${key}=")
            }
            else {
                values.each {
                    parts.add("${key}=${it}")
                }
            }
        }
        String newQuery = parts.join('&')

        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder.append(uri.getScheme()).append('://').append(uri.getHost())
        if (!(uri.getScheme() == 'https' && uri.getPort() == 443) && !(uri.getScheme() == 'http' && uri.getPort() == 80)) {
            stringBuilder.append(':').append(uri.getPort())
        }
        stringBuilder.append(uri.getPath())
        if (newQuery) {
            stringBuilder.append('?').append(newQuery)
        }

        return new URI(stringBuilder.toString())
    }
}
