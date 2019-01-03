/*
 * Copyright 2016-2018 the original author or authors.
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
package com.budjb.httprequests.filter.bundled;

import com.budjb.httprequests.*;
import com.budjb.httprequests.exception.EntityException;
import com.budjb.httprequests.exception.HttpClientException;
import com.budjb.httprequests.filter.LifecycleFilter;
import com.budjb.httprequests.filter.OutputStreamFilter;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * A filter that captures both the request and response and logs it.
 * <p>
 * This class is based on Jersey Client 1.x's {@code LoggingFilter}.
 */
public abstract class LoggingFilter implements OutputStreamFilter, LifecycleFilter, Closeable {
    /**
     * The maximum number of bytes to logger from request and response entities.
     */
    private static final int MAX_ENTITY_LENGTH = 10000;

    /**
     * Name of the {@link StringBuilder} instance in the {@link HttpContext}.
     */
    private static final String STRING_BUILDER_NAME = "com.budjb.httprequests.filter.logging.StringBuilder";

    /**
     * Name of the {@link LoggingOutputStream} instance in the {@link HttpContext}.
     */
    private static final String OUTPUT_STREAM_NAME = "com.budjb.httprequests.filter.logging.LoggingOutputStream";

    /**
     * Thread local that stores the  HTTP context of the request in the current thread.
     */
    private final ThreadLocal<HttpContext> context = new ThreadLocal<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart(HttpContext context) {
        this.context.set(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream filter(OutputStream outputStream) {
        LoggingOutputStream loggingOutputStream = new LoggingOutputStream(outputStream);
        context.get().set(OUTPUT_STREAM_NAME, loggingOutputStream);
        return loggingOutputStream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRequest(HttpContext context) {
        StringBuilder stringBuilder = new StringBuilder();
        context.set(STRING_BUILDER_NAME, stringBuilder);
        logRequestInformation(context, stringBuilder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResponse(HttpContext context) {
        StringBuilder stringBuilder = context.get(STRING_BUILDER_NAME, StringBuilder.class);

        logRequestEntity(context.get(OUTPUT_STREAM_NAME, LoggingOutputStream.class), stringBuilder);

        stringBuilder.append('\n');

        logResponseInformation(context.getResponse(), stringBuilder);

        if (context.getResponse().hasEntity()) {
            logResponseEntity(context.getResponse(), stringBuilder);
        }

        write(stringBuilder.toString());
    }

    /**
     * Logs request information to a {@link StringBuilder}, including request URI and headers.
     *
     * @param context       HTTP request context.
     * @param stringBuilder {@link StringBuilder} instance to logger to.
     */
    private void logRequestInformation(HttpContext context, StringBuilder stringBuilder) {
        HttpRequest request = context.getRequest();
        HttpMethod method = context.getMethod();
        HttpEntity entity = context.getRequestEntity();

        stringBuilder.append("Sending HTTP client request with the following data:\n");
        try {
            stringBuilder.append("> ").append(method.toString()).append(" ").append(new URI(request.getUri()).toASCIIString());

            if (request.getQueryParameters().size() > 0) {
                List<String> pairs = new ArrayList<>();

                for (String key : request.getQueryParameters().keySet()) {
                    for (String value : request.getQueryParameters().get(key)) {
                        try {
                            pairs.add(URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"));
                        }
                        catch (UnsupportedEncodingException e) {
                            throw new HttpClientException(e);
                        }
                    }
                }

                stringBuilder.append("?").append(String.join("&", pairs));
            }
            stringBuilder.append("\n");
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        if (entity != null) {
            String contentType = entity.getFullContentType();
            if (contentType != null) {
                stringBuilder.append("> Content-Type: ").append(entity.getFullContentType()).append("\n");
            }
        }

        request.getHeaders().forEach((k, v) -> {
            stringBuilder.append("> ").append(k).append(": ");
            stringBuilder.append(String.join(",", v));
            stringBuilder.append("\n");
        });
    }

    /**
     * Logs the request entity, if one exists.
     *
     * @param loggingOutputStream Output stream containing the entity log.
     * @param stringBuilder       {@link StringBuilder} instance to logger to.
     */
    private void logRequestEntity(LoggingOutputStream loggingOutputStream, StringBuilder stringBuilder) {
        if (loggingOutputStream == null) {
            return;
        }

        byte[] output = loggingOutputStream.getLoggingStream().toByteArray();

        if (output.length == 0) {
            return;
        }

        stringBuilder.append("\n");
        stringBuilder.append(new String(output, 0, Math.min(output.length, MAX_ENTITY_LENGTH)));
        if (output.length > MAX_ENTITY_LENGTH) {
            stringBuilder.append(" ...more...");
        }
        stringBuilder.append("\n");
    }

    /**
     * Logs response information into a {@link StringBuilder}, including response status and headers.
     *
     * @param response      HTTP response.
     * @param stringBuilder {@link StringBuilder} instance to logger to.
     */
    private void logResponseInformation(HttpResponse response, StringBuilder stringBuilder) {
        stringBuilder.append("Received HTTP server response with the following data:\n");
        stringBuilder.append("< ").append(response.getStatus()).append("\n");

        response.getHeaders().forEach((k, v) -> {
            stringBuilder.append("< ").append(k).append(": ");
            stringBuilder.append(String.join(",", v));
            stringBuilder.append("\n");
        });
    }

    /**
     * Logs the response entity, if there is one.
     *
     * @param response      HTTP response.
     * @param stringBuilder {@link StringBuilder} instance to logger to.
     */
    private void logResponseEntity(HttpResponse response, StringBuilder stringBuilder) {
        try {
            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getInputStream();

            if (!inputStream.markSupported()) {
                inputStream = new BufferedInputStream(inputStream, MAX_ENTITY_LENGTH + 1);
            }

            inputStream.mark(MAX_ENTITY_LENGTH + 1);
            byte[] buffer = new byte[MAX_ENTITY_LENGTH + 1];

            int entitySize = inputStream.read(buffer);

            if (entitySize > -1) {
                stringBuilder.append("\n");
                stringBuilder.append(new String(buffer, 0, Math.min(entitySize, MAX_ENTITY_LENGTH)));

                if (entitySize > MAX_ENTITY_LENGTH) {
                    stringBuilder.append(" ...more...");
                }

                stringBuilder.append("\n");

                inputStream.reset();
            }

            response.setEntity(new HttpEntity(inputStream, entity.getContentType(), entity.getCharSet()));
        }
        catch (IOException | EntityException e) {
            throw new HttpClientException(e);
        }
    }

    /**
     * Writes the contents of the conversation.
     *
     * @param content Contents of the conversation.
     */
    protected abstract void write(String content);

    /**
     * An {@link OutputStream} that wraps an actual stream and writes to both that stream
     * and another local stream so that the request can be captured.
     */
    public static class LoggingOutputStream extends OutputStream {
        /**
         * The output stream that will contain the info to logger.
         */
        private final ByteArrayOutputStream loggingStream;

        /**
         * The output stream being wrapped by logging output stream.
         */
        private final OutputStream outputStream;

        /**
         * Constructor.
         *
         * @param outputStream Output stream to wrap and logger.
         */
        LoggingOutputStream(OutputStream outputStream) {
            loggingStream = new ByteArrayOutputStream();
            this.outputStream = outputStream;
        }

        /**
         * Returns the output stream containing logs.
         *
         * @return The output stream containing logs.
         */
        ByteArrayOutputStream getLoggingStream() {
            return loggingStream;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void write(int b) throws IOException {
            outputStream.write(b);

            if (loggingStream.size() <= MAX_ENTITY_LENGTH) {
                loggingStream.write(b);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        context.remove();
    }
}
