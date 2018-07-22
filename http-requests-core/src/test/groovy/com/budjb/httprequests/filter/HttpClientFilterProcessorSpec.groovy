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

package com.budjb.httprequests.filter

import com.budjb.httprequests.HttpContext
import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.HttpResponse
import spock.lang.Specification

class HttpClientFilterProcessorSpec extends Specification {
    def 'When filtering a request, only RequestFilter instances are used'() {
        setup:
        RequestFilter f1 = Mock(RequestFilter)
        HttpClientFilter f2 = Mock(HttpClientFilter)

        HttpClientFilterProcessor processor = new HttpClientFilterProcessor([f1, f2])

        HttpRequest request = Mock(HttpRequest)

        when:
        processor.filterHttpRequest(request)

        then:
        1 * f1.filter(request)
    }

    def 'When calling onStart, only LifecycleFilter instances are used'() {
        setup:
        LifecycleFilter f1 = Mock(LifecycleFilter)
        HttpClientFilter f2 = Mock(HttpClientFilter)

        HttpClientFilterProcessor processor = new HttpClientFilterProcessor([f1, f2])

        HttpContext context = Mock(HttpContext)

        when:
        processor.onStart(context)

        then:
        1 * f1.onStart(context)
    }

    def 'When calling onRequest, only LifecycleFilter instances are used'() {
        setup:
        LifecycleFilter f1 = Mock(LifecycleFilter)
        HttpClientFilter f2 = Mock(HttpClientFilter)

        HttpClientFilterProcessor processor = new HttpClientFilterProcessor([f1, f2])

        HttpContext context = Mock(HttpContext)

        when:
        processor.onRequest(context)

        then:
        1 * f1.onRequest(context)
    }

    def 'When calling onResponse, only LifecycleFilter instances are used'() {
        setup:
        LifecycleFilter f1 = Mock(LifecycleFilter)
        HttpClientFilter f2 = Mock(HttpClientFilter)

        HttpClientFilterProcessor processor = new HttpClientFilterProcessor([f1, f2])

        HttpContext context = Mock(HttpContext)

        when:
        processor.onResponse(context)

        then:
        1 * f1.onResponse(context)
    }

    def 'When calling onComplete, only LifecycleFilter instances are used'() {
        setup:
        LifecycleFilter f1 = Mock(LifecycleFilter)
        HttpClientFilter f2 = Mock(HttpClientFilter)

        HttpClientFilterProcessor processor = new HttpClientFilterProcessor([f1, f2])

        HttpContext context = Mock(HttpContext)

        when:
        processor.onComplete(context)

        then:
        1 * f1.onComplete(context)
    }

    def 'When filtering a response, only ResponseFilter instances are used'() {
        setup:
        ResponseFilter f1 = Mock(ResponseFilter)
        HttpClientFilter f2 = Mock(HttpClientFilter)

        HttpClientFilterProcessor processor = new HttpClientFilterProcessor([f1, f2])

        HttpResponse response = Mock(HttpResponse)

        when:
        processor.filterHttpResponse(response)

        then:
        1 * f1.filter(response)
    }

    def 'When filtering an output stream filter, only OutputStreamFilter instances are used'() {
        setup:
        OutputStreamFilter f1 = Mock(OutputStreamFilter)
        HttpClientFilter f2 = Mock(HttpClientFilter)

        HttpClientFilterProcessor processor = new HttpClientFilterProcessor([f1, f2])

        OutputStream outputStream = Mock(OutputStream)

        when:
        processor.filterOutputStream(outputStream)

        then:
        1 * f1.filter(outputStream)
    }

    def 'hasRetryFilters returns whether the processor contains retry filters'() {
        when:
        HttpClientFilterProcessor processor = new HttpClientFilterProcessor([])

        then:
        !processor.hasRetryFilters()

        when:
        processor = new HttpClientFilterProcessor([Mock(RetryFilter)])

        then:
        processor.hasRetryFilters()
    }

    def 'When calling onRetry and a retry filter returns true, the method returns true'() {
        setup:
        RetryFilter f1 = Mock(RetryFilter)

        HttpClientFilterProcessor processor = new HttpClientFilterProcessor([f1])

        HttpContext context = Mock(HttpContext)

        when:
        boolean result = processor.isRetryRequired(context)

        then:
        1 * f1.isRetryRequired(context) >> true
        result
    }

    def 'When calling onRetry and no retry filters returns true, the method returns false'() {
        setup:
        RetryFilter f1 = Mock(RetryFilter)

        HttpClientFilterProcessor processor = new HttpClientFilterProcessor([f1])

        HttpContext context = Mock(HttpContext)

        when:
        boolean result = processor.isRetryRequired(context)

        then:
        1 * f1.isRetryRequired(context) >> false
        !result
    }
}
