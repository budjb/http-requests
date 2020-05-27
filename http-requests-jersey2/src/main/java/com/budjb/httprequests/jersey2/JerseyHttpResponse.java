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
package com.budjb.httprequests.jersey2;

import com.budjb.httprequests.*;
import com.budjb.httprequests.converter.EntityConverterManager;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

/**
 * An {@link HttpResponse} implementation that wraps a {@link Response}.
 */
class JerseyHttpResponse extends AbstractHttpResponse {
    /**
     * Jersey Client response.
     */
    private final Response response;

    /**
     * Constructor.
     *
     * @param request          Request properties used to make the request.
     * @param converterManager Converter manager.
     * @param response         Jersey Client response.
     */
    JerseyHttpResponse(HttpRequest request, EntityConverterManager converterManager, Response response) throws IOException {
        super(converterManager, request, response.getStatus(), new MultiValuedMap(response.getStringHeaders()), parseEntity(response));

        this.response = response;

        if (!hasEntity()) {
            close();
        }
    }

    /**
     * Parses the entity from the response.
     *
     * @param response Jersey response.
     * @return The parsed HTTP entity, or {@code null} if none is available.
     * @throws IOException When an IO exception occurs.
     */
    private static HttpEntity parseEntity(Response response) throws IOException {
        if (!response.hasEntity()) {
            return null;
        }

        String contentType = null;
        String charSet = null;

        if (response.getMediaType() != null) {
            MediaType mediaType = response.getMediaType();
            contentType = mediaType.getType() + "/" + mediaType.getSubtype();
            if (mediaType.getParameters().containsKey("charset")) {
                charSet = mediaType.getParameters().get("charset");
            }
        }

        return new HttpEntity((InputStream) response.getEntity(), contentType, charSet);
    }

    /**
     * Returns the Jersey response object.
     *
     * @return The Jersey response object.
     */
    public Response getResponse() {
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        super.close();
        response.close();
    }
}
