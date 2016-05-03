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
package com.budjb.httprequests.filter

import com.budjb.httprequests.HttpClient
import com.budjb.httprequests.HttpContext
import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.HttpResponse

/**
 * An {@link HttpClientFilter} that allows modification of the {@link HttpResponse} instance before
 * it is returned from the {@link HttpClient}.
 */
interface HttpClientResponseFilter extends HttpClientFilter {
    /**
     * Provides an opportunity to modify the {@link HttpResponse} before it is returned.
     *
     * @param context HTTP request context.
     */
    void filterHttpResponse(HttpContext context)
}
