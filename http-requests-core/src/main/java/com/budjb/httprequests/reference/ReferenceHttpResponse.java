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
package com.budjb.httprequests.reference;

import com.budjb.httprequests.HttpEntity;
import com.budjb.httprequests.HttpRequest;
import com.budjb.httprequests.HttpResponse;
import com.budjb.httprequests.MultiValuedMap;
import com.budjb.httprequests.converter.EntityConverterManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An {@link HttpResponse} implementation that wraps an {@link HttpURLConnection} object.
 */
class ReferenceHttpResponse extends HttpResponse {
    /**
     * Pattern to parse content type.
     */
    private final static Pattern CONTENT_TYPE_PATTERN = Pattern.compile("^([^;]+).*$");

    /**
     * Pattern to parse character set.
     */
    private final static Pattern CHARACTER_SET_PATTERN = Pattern.compile("charset\\s*=\\s*([^;]+)");

    /**
     * Constructor.
     *
     * @param request          Request properties used to make the request.
     * @param converterManager Converter manager.
     * @throws IOException When an IO exception occurs.
     */
    ReferenceHttpResponse(HttpRequest request, EntityConverterManager converterManager, int status, MultiValuedMap headers, InputStream inputStream, String contentType) throws IOException {
        super(converterManager, request, status, headers, loadEntity(inputStream, contentType));
    }

    /**
     * Parses the response entity and content type, if available.
     *
     * @param inputStream Input stream.
     * @return A parsed HTTP entity, or {@code null}.
     * @throws IOException When an IO exception occurs.
     */
    private static HttpEntity loadEntity(InputStream inputStream, String contentType) throws IOException {
        if (inputStream == null) {
            return null;
        }

        int read = inputStream.read();

        if (read == -1) {
            return null;
        }

        PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream);
        pushbackInputStream.unread(read);

        if (contentType != null) {
            Matcher contentMatcher = CONTENT_TYPE_PATTERN.matcher(contentType);

            if (contentMatcher.find()) {
                Matcher charsetMatcher = CHARACTER_SET_PATTERN.matcher(contentType);

                if (charsetMatcher.find()) {
                    return new HttpEntity(pushbackInputStream, contentMatcher.group(1), charsetMatcher.group(1));
                }
                else {
                    return new HttpEntity(pushbackInputStream, contentMatcher.group(1));
                }
            }
        }

        return new HttpEntity(pushbackInputStream);
    }
}
