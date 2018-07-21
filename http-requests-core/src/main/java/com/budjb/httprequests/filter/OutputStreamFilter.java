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
package com.budjb.httprequests.filter;

import java.io.OutputStream;

/**
 * An HTTP client filter that allows modification of the request entity before it is
 * transmitted with the request.
 */
public interface OutputStreamFilter extends HttpClientFilter {
    /**
     * Filters a request entity's {@link OutputStream}
     *
     * @param outputStream The {@link OutputStream} of the request.
     * @return Filtered request {@link OutputStream}.
     */
    OutputStream filter(OutputStream outputStream);
}
