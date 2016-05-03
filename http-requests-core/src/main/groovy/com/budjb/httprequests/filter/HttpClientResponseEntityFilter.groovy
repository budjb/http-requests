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

import com.budjb.httprequests.HttpContext

/**
 * An HTTP client filter that allows modification of the response entity before it is
 * returned to the application.
 */
interface HttpClientResponseEntityFilter extends HttpClientFilter {
    /**
     * Filters a response entity's {@link InputStream}.
     *
     * @param context HTTP context.
     * @param inputStream The {@link InputStream} of the response.
     * @return Filtered response {@link InputStream}.
     */
    InputStream readResponseEntity(HttpContext context, InputStream inputStream)
}
