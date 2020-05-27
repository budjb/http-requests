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
package com.budjb.httprequests.httpcomponents.client;

import com.budjb.httprequests.AbstractHttpResponse;
import com.budjb.httprequests.HttpRequest;
import com.budjb.httprequests.HttpResponse;
import com.budjb.httprequests.MultiValuedMap;
import com.budjb.httprequests.converter.EntityConverterManager;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.Arrays;

/**
 * An {@link HttpResponse} implementation that wraps an Apache {@link CloseableHttpResponse}.
 */
class HttpComponentsResponse extends AbstractHttpResponse {
    /**
     * The response returned from the Apache HTTP client.
     */
    private final CloseableHttpResponse response;

    /**
     * The underlying Apache client. This is only tracked here so that it can be properly closed.
     */
    private final CloseableHttpClient httpClient;

    /**
     * Constructor.
     */
    HttpComponentsResponse(HttpRequest request, EntityConverterManager converterManager, CloseableHttpResponse response, CloseableHttpClient httpClient) throws IOException {
        super(
            converterManager,
            request,
            response.getStatusLine().getStatusCode(),
            parseHeaders(response.getAllHeaders()),
            parseEntity(response.getEntity())
        );

        this.response = response;
        this.httpClient = httpClient;

        if (!hasEntity()) {
            close();
        }
    }

    /**
     * Parses headers into a {@link MultiValuedMap}.
     *
     * @param headers Apache response headers.
     * @return The resulting {@link MultiValuedMap}.
     */
    private static MultiValuedMap parseHeaders(Header[] headers) {
        MultiValuedMap result = new MultiValuedMap();
        Arrays.stream(headers).forEach(h -> result.add(h.getName(), h.getValue()));
        return result;
    }

    /**
     * Parses the response's entity into an {@link com.budjb.httprequests.HttpEntity}.
     *
     * @param entity Response entity.
     * @return The parsed {@link com.budjb.httprequests.HttpEntity}, or {@code null} if there is not one.
     * @throws IOException When an IO exception occurs.
     */
    private static com.budjb.httprequests.HttpEntity parseEntity(HttpEntity entity) throws IOException {
        if (entity == null) {
            return null;
        }

        if (entity.getContentLength() == 0) {
            return null;
        }

        String contentType = null;
        String charSet = null;

        if (entity.getContentType() != null) {
            ContentType c = ContentType.parse(entity.getContentType().getValue());
            contentType = c.getMimeType();

            if (c.getCharset() != null) {
                charSet = c.getCharset().toString();
            }
        }

        return new com.budjb.httprequests.HttpEntity(entity.getContent(), contentType, charSet);
    }

    /**
     * Returns the response returned from the Apache HTTP client.
     *
     * @return The response returned from the Apache HTTP client.
     */
    public CloseableHttpResponse getResponse() {
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        super.close();
        response.close();
        httpClient.close();
    }
}
