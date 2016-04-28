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

import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.filter.HttpClientEntityFilter
import com.budjb.httprequests.filter.HttpClientRequestFilter

import java.util.zip.GZIPOutputStream

/**
 * A filter that compresses the entity with the GZIP algorithm.
 */
class GZIPFilter implements HttpClientEntityFilter, HttpClientRequestFilter {
    /**
     * Filters a request entity in {@link OutputStream} form.
     *
     * @param request HTTP request properties.
     * @param outputStream Output stream of the request.
     * @return Filtered request input stream.
     */
    @Override
    OutputStream filterEntity(OutputStream outputStream) {
        return new GZIPOutputStream(outputStream)
    }

    /**
     * Provides an opportunity to modify the {@link HttpRequest} before it is transmitted.
     *
     * @param request Request object that will be used to make the HTTP request.
     */
    @Override
    void filterRequest(HttpRequest request) {
        request.setHeader('Content-Encoding', 'gzip')
    }
}
