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
package com.budjb.httprequests.filter.bundled

import com.budjb.httprequests.HttpContext
import com.budjb.httprequests.HttpMethod
import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.HttpResponse
import com.budjb.httprequests.filter.HttpClientLifecycleFilter
import com.budjb.httprequests.filter.HttpClientRequestEntityFilter
import com.budjb.httprequests.filter.HttpClientResponseEntityFilter

/**
 * A filter that captures both the request and response and logs it.
 *
 * This class is based on Jersey Client 1.x's <code>LoggingFilter</code>.
 */
abstract class LoggingFilter implements HttpClientRequestEntityFilter, HttpClientResponseEntityFilter, HttpClientLifecycleFilter {
    /**
     * The maximum number of bytes to logger from request and response entities.
     */
    private static final int MAX_ENTITY_LENGTH = 10000

    /**
     * Name of the {@link StringBuilder} instance in the {@link HttpContext}.
     */
    private static final String STRING_BUILDER_NAME = 'com.budjb.httprequests.filter.logging.StringBuilder'

    /**
     * Name of the {@link LoggingOutputStream} instance in the {@link HttpContext}.
     */
    private static final String OUTPUT_STREAM_NAME = 'com.budjb.httprequests.filter.logging.LoggingOutputStream'

    /**
     * Filters a request entity's {@link OutputStream}
     *
     * @param context HTTP context.
     * @param outputStream The {@link OutputStream} of the request.
     * @return Filtered request {@link OutputStream}.
     */
    @Override
    OutputStream filterRequestEntity(HttpContext context, OutputStream outputStream) {
        LoggingOutputStream loggingOutputStream = new LoggingOutputStream(outputStream)
        context.set(OUTPUT_STREAM_NAME, loggingOutputStream)
        return loggingOutputStream
    }

    /**
     * Request listener.
     *
     * @param context
     * @param outputStream
     */
    void onRequest(HttpContext context, OutputStream outputStream) {
        StringBuilder stringBuilder = new StringBuilder()
        context.set(STRING_BUILDER_NAME, stringBuilder)
        logRequestInformation(context, stringBuilder)
    }

    /**
     * Response listener.
     *
     * @param context
     */
    void onResponse(HttpContext context) {
        StringBuilder stringBuilder = context.get(STRING_BUILDER_NAME, StringBuilder)

        HttpResponse response = context.getResponse()

        logRequestEntity(context, stringBuilder)

        stringBuilder.append('\n')

        logResponseInformation(context, stringBuilder)

        if (response.hasEntity()) {
            logResponseEntity(context, stringBuilder)
        }

        log(stringBuilder.toString())
    }

    /**
     * Logs request information to a {@link StringBuilder}, including request URI and headers.
     *
     * @param context HTTP request context.
     * @param stringBuilder {@link StringBuilder} instance to logger to.
     */
    protected void logRequestInformation(HttpContext context, StringBuilder stringBuilder) {
        HttpRequest request = context.getRequest()
        HttpMethod method = context.getMethod()

        stringBuilder.append('Sending HTTP client request with the following data:\n')
        stringBuilder.append("> ${method.toString()} ${new URI(request.getUri()).toASCIIString()}\n")

        if (request.getContentType()) {
            stringBuilder.append("> Content-Type: ${request.getFullContentType()}\n")
        }
        if (request.getAccept()) {
            stringBuilder.append("> Accept: ${request.getAccept()}\n")
        }

        request.getHeaders().each { key, values ->
            stringBuilder.append("> ${key}: ")
            if (values instanceof Collection) {
                stringBuilder.append(values.join(','))
            }
            else {
                stringBuilder.append(values.toString())
            }
            stringBuilder.append("\n")
        }
    }

    /**
     * Logs the request entity, if one exists.
     *
     * @param context HTTP request context.
     * @param stringBuilder {@link StringBuilder} instance to logger to.
     */
    protected void logRequestEntity(HttpContext context, StringBuilder stringBuilder) {
        LoggingOutputStream loggingOutputStream = context.get(OUTPUT_STREAM_NAME, LoggingOutputStream)

        if (loggingOutputStream) {
            byte[] output = loggingOutputStream.getLoggingStream().toByteArray()
            stringBuilder.append(new String(output, 0, Math.min(output.size(), MAX_ENTITY_LENGTH)))
            if (output.size() > MAX_ENTITY_LENGTH) {
                stringBuilder.append(" ...more...")
            }
            stringBuilder.append("\n")
        }
    }

    /**
     * Logs response information into a {@link StringBuilder}, including response status and headers.
     *
     * @param context HTTP request context.
     * @param stringBuilder {@link StringBuilder} instance to logger to.
     */
    protected void logResponseInformation(HttpContext context, StringBuilder stringBuilder) {
        HttpResponse response = context.getResponse()

        stringBuilder.append('Received HTTP server response with the following data:\n')
        stringBuilder.append("< ${response.getStatus()}\n")

        response.getHeaders().each { key, values ->
            stringBuilder.append("< ${key}: ")
            if (values instanceof Collection) {
                stringBuilder.append(values.join(','))
            }
            else {
                stringBuilder.append(values.toString())
            }
            stringBuilder.append("\n")
        }
    }

    /**
     * Logs the response entity, if there is one.
     *
     * @param context HTTP request context.
     * @param stringBuilder {@link StringBuilder} instance to logger to.
     */
    protected void logResponseEntity(HttpContext context, StringBuilder stringBuilder) {
        HttpResponse response = context.getResponse()

        InputStream inputStream = response.getEntity()

        if (!inputStream.markSupported()) {
            inputStream = new BufferedInputStream(response.getEntity(), MAX_ENTITY_LENGTH + 1)
        }

        inputStream.mark(MAX_ENTITY_LENGTH + 1)
        byte[] buffer = new byte[MAX_ENTITY_LENGTH + 1]

        int entitySize = inputStream.read(buffer)

        if (entitySize > -1) {
            stringBuilder.append(new String(buffer, 0, Math.min(entitySize, MAX_ENTITY_LENGTH)))

            if (entitySize > MAX_ENTITY_LENGTH) {
                stringBuilder.append(' ...more...')
            }

            stringBuilder.append("\n")

            inputStream.reset()
        }

        response.setEntity(inputStream)
    }

    /**
     * Filters a response entity's {@link InputStream}.
     *
     * @param context HTTP context.
     * @param inputStream The {@link InputStream} of the response.
     * @return Filtered response {@link InputStream}.
     */
    @Override
    InputStream readResponseEntity(HttpContext context, InputStream inputStream) {
        return inputStream
    }

    /**
     * An {@link OutputStream} that wraps an actual stream and writes to both that stream
     * and another local stream so that the request can be captured.
     */
    static class LoggingOutputStream extends OutputStream {
        /**
         * The output stream that will contain the info to logger.
         */
        final ByteArrayOutputStream loggingStream

        /**
         * The output stream being wrapped by logging output stream.
         */
        final OutputStream outputStream

        /**
         * Constructor.
         *
         * @param outputStream Output stream to wrap and logger.
         */
        LoggingOutputStream(OutputStream outputStream) {
            loggingStream = new ByteArrayOutputStream()
            this.outputStream = outputStream
        }

        /**
         * Writes the specified byte to this output stream. The general
         * contract for <code>write</code> is that one byte is written
         * to the output stream. The byte to be written is the eight
         * low-order bits of the argument <code>b</code>. The 24
         * high-order bits of <code>b</code> are ignored.
         * <p>
         * Subclasses of <code>OutputStream</code> must provide an
         * implementation for this method.
         *
         * @param b the <code>byte</code>.
         * @exception IOException  if an I/O error occurs. In particular,
         *             an <code>IOException</code> may be thrown if the
         *             output stream has been closed.
         */
        @Override
        void write(int b) throws IOException {
            outputStream.write(b)

            if (loggingStream.size() <= MAX_ENTITY_LENGTH) {
                loggingStream.write(b)
            }
        }
    }

    /**
     * Writes the contents of the conversation.
     *
     * @param content Contents of the conversation.
     */
    protected abstract void log(String content)
}
