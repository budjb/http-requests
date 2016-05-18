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

import com.budjb.httprequests.converter.EntityConverterManager
import com.budjb.httprequests.filter.HttpClientFilterManager

/**
 * An implementation of {@link HttpClient} that does not make an actual HTTP request; rather it allows
 * the contents of the response to be injected into the client and returned as if a request had been made
 * and those properties were returned in the response. This is useful when mocking requests in unit or
 * integration tests.
 */
class MockHttpClient extends AbstractHttpClient {
    /**
     * Headers of the response.
     */
    Map<String, Object> headers = [:]

    /**
     * Content type of the response.
     */
    String contentType

    /**
     * Character set of the response.
     */
    String charset

    /**
     * HTTP status code of the response.
     */
    int status

    /**
     * Input stream of the response.
     */
    InputStream responseInputStream

    /**
     * A buffer containing the contents of the request input stream.
     */
    byte[] requestBuffer

    /**
     * HTTP request context.
     */
    HttpContext httpContext

    /**
     * Constructor.
     */
    MockHttpClient() {
        filterManager = new HttpClientFilterManager()
        converterManager = new EntityConverterManager()
    }

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
        httpContext = context

        if (inputStream) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
            transmit(inputStream, filterOutputStream(context, outputStream))
            requestBuffer = outputStream.toByteArray()
        }

        String contentType = this.contentType
        if (contentType && charset) {
            contentType += ";charset=${charset}"
        }

        return new MockHttpResponse(context.request, converterManager, status, headers, contentType, responseInputStream)
    }

    /**
     * Converts the entity input stream to an output stream.
     *
     * @param inputStream
     * @param outputStream
     */
    private static void transmit(InputStream inputStream, OutputStream outputStream) {
        int read
        byte[] buffer = new byte[8192]

        while ((read = inputStream.read(buffer, 0, 8192)) != -1) {
            outputStream.write(buffer, 0, read)
        }

        inputStream.close()
    }
}
