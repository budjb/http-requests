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

import com.budjb.httprequests.*;
import com.budjb.httprequests.converter.EntityConverterManager;
import com.budjb.httprequests.exception.HttpMethodUnsupportedException;
import com.budjb.httprequests.filter.HttpClientFilterProcessor;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.budjb.httprequests.HttpRequestsUtil.isNullOrEmpty;
import static com.budjb.httprequests.HttpRequestsUtil.tokenize;

/**
 * A built-in, basic implementation of an {@link HttpClient}. This implementation is useful
 * when minimal external dependencies are desired. While it provides basic HTTP functionality,
 * it should not be considered production ready, and other underlying client implementations
 * should be used instead.
 */
class ReferenceHttpClient extends AbstractHttpClient {
    /**
     * Constructor.
     *
     * @param converterManager Entity converter manager.
     */
    ReferenceHttpClient(EntityConverterManager converterManager) {
        super(converterManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected HttpResponse execute(HttpContext context, HttpEntity httpEntity, HttpClientFilterProcessor filterProcessor) throws IOException, URISyntaxException, GeneralSecurityException {
        if (context.getMethod() == HttpMethod.PATCH) {
            throw new HttpMethodUnsupportedException(this, HttpMethod.PATCH);
        }

        HttpRequest request = context.getRequest();

        URI uri = createURI(request);

        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();

        if (connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection) connection).setSSLSocketFactory(this.createSSLContext(request).getSocketFactory());
            if (!request.isSslValidated()) {
                ((HttpsURLConnection) connection).setHostnameVerifier(createTrustingHostnameVerifier());
            }

        }

        connection.setRequestMethod(context.getMethod().name());
        connection.setConnectTimeout(request.getConnectionTimeout());
        connection.setReadTimeout(request.getReadTimeout());
        connection.setInstanceFollowRedirects(request.isFollowRedirects());

        request.getHeaders().forEach((k, v) -> connection.setRequestProperty(k, String.join(",", v)));

        if (httpEntity != null) {
            if (!context.getMethod().isSupportsResponseEntity()) {
                throw new IllegalArgumentException("HTTP request entity is not supported for HTTP  method " + context.getMethod().name());
            }

            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", request.getHeaders().containsKey("Content-Type") ? request.getHeaders().getFlat("Content-Type") : httpEntity.getFullContentType());

            OutputStream outputStream = filterProcessor.filterOutputStream(connection.getOutputStream());
            StreamUtils.shovel(httpEntity.getInputStream(), outputStream);
            httpEntity.getInputStream().close();
            outputStream.close();
        }

        connection.connect();

        int status = connection.getResponseCode();
        MultiValuedMap headers = new MultiValuedMap(connection.getHeaderFields().entrySet().stream().filter(e -> e.getKey() != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        InputStream inputStream = getEntityStream(status, connection);
        String contentType = headers.getFlat("Content-Type");
        return new ReferenceHttpResponse(request, getConverterManager(), status, headers, inputStream, contentType);
    }

    /**
     * Creates the URI of the request.
     *
     * @param request Request properties.
     * @return A new URI with all query parameters applied.
     * @throws URISyntaxException When a problem occurs while parsing a URI.
     */
    private URI createURI(HttpRequest request) throws URISyntaxException {
        URI uri = new URI(request.getUri());

        Map<String, List<String>> queryParameters = new HashMap<>();

        String existingQuery = uri.getQuery();
        if (!isNullOrEmpty(existingQuery)) {
            tokenize(existingQuery, "&").forEach(pair -> {
                List<String> parts = tokenize(pair, "=");

                if (parts.size() > 2) {
                    throw new IllegalArgumentException("malformed query string");
                }

                if (!queryParameters.containsKey(parts.get(0))) {
                    queryParameters.put(parts.get(0), new ArrayList<>());
                }

                if (parts.size() > 1) {
                    queryParameters.get(parts.get(0)).add(parts.get(1));
                }
            });
        }

        request.getQueryParameters().forEach((k, v) -> {
            if (!queryParameters.containsKey(k)) {
                queryParameters.put(k, new ArrayList<>());
            }
            queryParameters.get(k).addAll(v);
        });

        List<String> parts = new ArrayList<>();
        queryParameters.forEach((k, v) -> {
            if (v.size() == 0) {
                parts.add(k + "=");
            }
            else {
                v.forEach(it -> parts.add(k + "=" + it));
            }
        });
        String newQuery = String.join("&", parts);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(uri.getScheme()).append("://").append(uri.getHost());
        if (uri.getPort() != -1 && !(uri.getScheme().equalsIgnoreCase("https") && uri.getPort() == 443) && !(uri.getScheme().equalsIgnoreCase("http") && uri.getPort() == 80)) {
            stringBuilder.append(':').append(uri.getPort());
        }
        stringBuilder.append(uri.getPath());
        if (newQuery.length() > 0) {
            stringBuilder.append('?').append(newQuery);
        }

        return new URI(stringBuilder.toString());
    }

    /**
     * Returns the appropriate entity {@link InputStream} based on the HTTP status of the response.
     *
     * @param status     HTTP status of the response.
     * @param connection HTTP connection
     * @return The entity stream.
     * @throws IOException when an IO exception occurs.
     */
    private InputStream getEntityStream(int status, HttpURLConnection connection) throws IOException {
        if (status < 400) {
            return connection.getInputStream();
        }
        else {
            return connection.getErrorStream();
        }
    }
}
