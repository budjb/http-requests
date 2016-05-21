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
package com.budjb.httprequests.reference

import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.HttpResponse
import com.budjb.httprequests.converter.EntityConverterManager

class ReferenceHttpResponse extends HttpResponse {
    /**
     * Connection object of the request.
     */
    HttpURLConnection httpURLConnection

    /**
     * Constructor.
     *
     * @param request Request properties used to make the request.
     * @param converterManager Converter manager.
     */
    ReferenceHttpResponse(HttpRequest request, EntityConverterManager converterManager, HttpURLConnection connection) {
        super(request, converterManager)

        this.httpURLConnection = connection

        setStatus(connection.getResponseCode())
        setContentType(connection.getContentType())
        setHeaders(connection.getHeaderFields())

        if (connection.getDoInput()) {
            InputStream inputStream = connection.getErrorStream()
            if (inputStream == null) {
                inputStream = connection.getInputStream()
            }

            if (inputStream != null) {
                byte read = inputStream.read()

                if (read != -1) {
                    PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream)
                    pushbackInputStream.unread(read)
                    setEntity(pushbackInputStream)
                }
            }
        }
    }
}
