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
package com.budjb.httprequests.jersey2

import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.HttpResponse
import com.budjb.httprequests.converter.EntityConverterManager

import javax.ws.rs.core.Response

/**
 * An {@link HttpResponse} implementation that wraps a {@link Response}.
 */
class JerseyHttpResponse extends HttpResponse {
    /**
     * Jersey Client response.
     */
    Response response

    /**
     * Constructor.
     *
     * @param request Request properties used to make the request.
     * @param converterManager Converter manager.
     * @param response Jersey Client response.
     */
    JerseyHttpResponse(HttpRequest request, EntityConverterManager converterManager, Response response) {
        super(request, converterManager)

        this.response = response

        setStatus(response.getStatus())
        setHeaders(response.getHeaders())

        if (response.getMediaType()) {
            setContentType(response.getMediaType().toString())
        }

        if (response.hasEntity()) {
            setEntity(response.getEntity() as InputStream)
            if (request.isBufferResponseEntity()) {
                close()
            }
        }
        else {
            close()
        }
    }

    /**
     * Closes the entity {@link InputStream} and the Jersey {@link Response}.
     *
     * @throws IOException
     */
    @Override
    void close() throws IOException {
        super.close()
        response.close()
    }
}
