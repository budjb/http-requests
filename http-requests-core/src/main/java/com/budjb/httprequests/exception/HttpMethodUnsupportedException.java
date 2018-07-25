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

package com.budjb.httprequests.exception;

import com.budjb.httprequests.HttpClient;
import com.budjb.httprequests.HttpMethod;

public class HttpMethodUnsupportedException extends HttpClientException {
    public HttpMethodUnsupportedException(HttpClient client, HttpMethod method) {
        super("HttpClient implementation " + client.getClass().getName() + " does not support the HTTP method " + method.name());
    }
}
