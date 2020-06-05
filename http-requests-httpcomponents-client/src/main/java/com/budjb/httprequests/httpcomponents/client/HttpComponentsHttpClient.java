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

import com.budjb.httprequests.*;
import com.budjb.httprequests.converter.EntityConverterManager;
import com.budjb.httprequests.filter.HttpClientFilterProcessor;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

public class HttpComponentsHttpClient extends AbstractHttpClient {
    /**
     * Constructor.
     *
     * @param converterManager Entity converter manager.
     */
    HttpComponentsHttpClient(EntityConverterManager converterManager) {
        super(converterManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected HttpResponse execute(HttpContext context, HttpEntity httpEntity, HttpClientFilterProcessor filterProcessor) throws URISyntaxException, IOException, GeneralSecurityException {
        HttpRequest request = context.getRequest();
        HttpMethod method = context.getMethod();

        CloseableHttpClient client = createClient(request);

        URIBuilder uriBuilder = new URIBuilder(request.getUri());

        request.getQueryParameters().forEach((k, v) -> {
            if (v.size() == 0) {
                uriBuilder.addParameter(k, null);
            }
            else {
                v.forEach(value -> uriBuilder.addParameter(k, value));
            }
        });

        HttpUriRequest httpRequest = createHttpRequest(method, uriBuilder.build());

        request.getHeaders().forEach((k, v) -> {
            if (v.size() == 0) {
                httpRequest.addHeader(k, null);
            }
            else {
                v.forEach(value -> httpRequest.addHeader(k, value));
            }
        });

        if (httpEntity != null && httpRequest instanceof HttpEntityEnclosingRequest) {
            ContentType contentType = null;
            if (httpEntity.getFullContentType() != null) {
                contentType = ContentType.parse(httpEntity.getFullContentType());
            }

            org.apache.http.HttpEntity entity = new InputStreamEntity(httpEntity.getInputStream(), contentType) {
                @Override
                public void writeTo(final OutputStream outputStream) throws IOException {
                    OutputStream filtered = filterProcessor.filterOutputStream(outputStream);
                    super.writeTo(filtered);

                    // This is a bit of a hack since HTTP components client does not give
                    // applications the ability to change the OutputStream being written to
                    // inside the library. For compression to work correctly, close() must
                    // be called. Ultimately, the next thing the client does after the call
                    // to this method is to close the original stream anyway.
                    filtered.close();
                }
            };
            ((HttpEntityEnclosingRequest) httpRequest).setEntity(entity);
        }

        return new HttpComponentsResponse(request, getConverterManager(), client.execute(httpRequest), client);
    }

    /**
     * Creates a client.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A new http client.
     */
    private CloseableHttpClient createClient(HttpRequest request) throws GeneralSecurityException {
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(request.getConnectionTimeout())
            .setSocketTimeout(request.getReadTimeout())
            .setRedirectsEnabled(request.isFollowRedirects())
            .build();

        HttpClientBuilder builder = HttpClients.custom();

        builder.setDefaultRequestConfig(requestConfig);

        builder.setSSLContext(this.createSSLContext(request));
        if (!request.isSslValidated()) {
            builder.setSSLHostnameVerifier(createTrustingHostnameVerifier());
        }

        return builder.build();
    }

    /**
     * Creates the appropriate HTTP request type for the given {@link HttpMethod}.
     *
     * @param method HTTP method of the request.
     * @param uri    URI of the request.
     * @return The appropriate HTTP request type.
     */
    private HttpUriRequest createHttpRequest(HttpMethod method, URI uri) {
        switch (method) {
            case GET:
                return new HttpGet(uri);

            case POST:
                return new HttpPost(uri);

            case PUT:
                return new HttpPut(uri);

            case DELETE:
                return new HttpDelete(uri);

            case HEAD:
                return new HttpHead(uri);

            case OPTIONS:
                return new HttpOptions(uri);

            case TRACE:
                return new HttpTrace(uri);

            case PATCH:
                return new HttpPatch(uri);

            default:
                throw new IllegalArgumentException("HTTP method ${method.toString()} is unsupported");
        }
    }
}
