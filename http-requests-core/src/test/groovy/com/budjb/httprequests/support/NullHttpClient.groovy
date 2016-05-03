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
package com.budjb.httprequests.support

import com.budjb.httprequests.AbstractHttpClient
import com.budjb.httprequests.HttpContext
import com.budjb.httprequests.HttpMethod
import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.HttpResponse

class NullHttpClient extends AbstractHttpClient {
    /**
     * Response object that will be returned from any HTTP request.
     *
     * Use this field to effectively mock a request and response.
     */
    HttpResponse response

    /**
     * Implements the logic to make an actual request with an HTTP client library.
     *
     * @param context HTTP request context.
     * @param inputStream An {@link InputStream} containing the response body. May be <code>null</code>.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    @Override
    protected HttpResponse doExecute(HttpContext context, InputStream inputStream) throws IOException {
        return response
    }
}
