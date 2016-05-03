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
import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.filter.HttpClientRequestEntityFilter
import com.budjb.httprequests.filter.HttpClientRequestFilter

import java.util.zip.DeflaterOutputStream

/**
 * A filter that compresses the entity with the deflate algorithm.
 */
class DeflateFilter implements HttpClientRequestEntityFilter, HttpClientRequestFilter {
    /**
     * Filters a request entity in {@link OutputStream} form.
     *
     * @param context HTTP context.
     * @param outputStream Output stream of the request.
     * @return Filtered request input stream.
     */
    @Override
    OutputStream filterRequestEntity(HttpContext context, OutputStream outputStream) {
        return new DeflaterOutputStream(outputStream)
    }

    /**
     * Provides an opportunity to modify the {@link HttpRequest} before it is transmitted.
     *
     * @param context HTTP request context.
     */
    @Override
    void filterHttpRequest(HttpContext context) {
        context.getRequest().setHeader('Content-Encoding', 'deflate')
    }
}
