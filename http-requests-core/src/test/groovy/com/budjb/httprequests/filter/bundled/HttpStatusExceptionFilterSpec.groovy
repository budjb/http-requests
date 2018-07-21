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

package com.budjb.httprequests.filter.bundled

import com.budjb.httprequests.HttpContext
import com.budjb.httprequests.HttpResponse
import com.budjb.httprequests.exception.HttpStatusException
import spock.lang.Specification

class HttpStatusExceptionFilterSpec extends Specification {
    def 'A non-2xx HTTP status code will throw an HttpStatusException'() {
        setup:
        HttpStatusExceptionFilter filter = new HttpStatusExceptionFilter()

        HttpResponse response = Mock(HttpResponse)
        response.getStatus() >> 300

        HttpContext context = new HttpContext()
        context.response = response

        when:
        filter.onComplete(context)

        then:
        thrown HttpStatusException
    }

    def 'A 2xx HTTP status code will not throw an HttpStatusException'() {
        setup:
        HttpStatusExceptionFilter filter = new HttpStatusExceptionFilter()

        HttpResponse response = Mock(HttpResponse)
        response.getStatus() >> 201

        HttpContext context = new HttpContext()
        context.response = response

        when:
        filter.onComplete(context)

        then:
        notThrown HttpStatusException
    }
}
