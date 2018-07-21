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
package com.budjb.httprequests.httpcomponents.client

import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.HttpResponse
import com.budjb.httprequests.converter.EntityConverterManager
import org.apache.http.HttpEntity
import org.apache.http.client.methods.CloseableHttpResponse

/**
 * An {@link HttpResponse} implementation that wraps an Apache {@link CloseableHttpResponse}.
 */
class HttpComponentsResponse extends HttpResponse {
    /**
     * The response returned from the Apache HTTP client.
     */
    CloseableHttpResponse response

    /**
     * Constructor.
     *
     * @param request
     * @param response
     */
    HttpComponentsResponse(HttpRequest request, EntityConverterManager converterManager, CloseableHttpResponse response) {
        super(request, converterManager)

        this.response = response

        setStatus(response.getStatusLine().getStatusCode())
        response.getAllHeaders().each {
            addHeader(it.getName(), it.getValue())
        }

        HttpEntity entity = response.getEntity()
        if (entity) {
            if (entity.getContentType()) {
                setContentType(entity.getContentType().getValue())
            }
            setEntity(entity.getContent())
            if (request.isBufferResponseEntity()) {
                close()
            }
        }
        else {
            close()
        }
    }

    /**
     * Closes the {@link InputStream} of the response and the underlying client.
     *
     * @throws IOException
     */
    @Override
    void close() throws IOException {
        super.close()
        response.close()
    }
}
