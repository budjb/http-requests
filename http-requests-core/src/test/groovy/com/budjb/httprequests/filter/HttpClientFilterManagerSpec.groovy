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

class HttpClientFilterManagerSpec extends Specification {
    def 'When creating an HttpClientFilterManager from another one, all filters are copied into the new one'() {
        setup:
        HttpClientFilter f1 = Mock(HttpClientFilter)
        HttpClientFilter f2 = Mock(HttpClientFilter)

        HttpClientFilterManager m1 = new HttpClientFilterManager()
        m1.add(f1)
        m1.add(f2)

        when:
        HttpClientFilterManager m2 = new HttpClientFilterManager(m1)

        then:
        m2.getAll().sort() == m1.getAll().sort()
    }

    def 'When an HttpClientFilter is added, the manager contains it'() {
        setup:
        HttpClientFilter filter = Mock(HttpClientFilter)
        HttpClientFilterManager manager = new HttpClientFilterManager()

        when:
        manager.add(filter)

        then:
        manager.getAll().size() == 1
        manager.getAll().get(0).is filter
    }

    def 'After removing a filter, it is no longer contained in the manager'() {
        setup:
        HttpClientFilter filter = Mock(HttpClientFilter)
        HttpClientFilterManager manager = new HttpClientFilterManager()
        manager.add(filter)

        expect:
        manager.getAll().contains(filter)

        when:
        manager.remove(filter)

        then:
        !manager.getAll().contains(filter)
    }

    def 'After clearing the manager, no filters are contained in the manager'() {
        setup:
        HttpClientFilter f1 = Mock(HttpClientFilter)
        HttpClientFilter f2 = Mock(HttpClientFilter)

        HttpClientFilterManager manager = new HttpClientFilterManager()
        manager.add(f1)
        manager.add(f2)

        when:
        manager.clear()

        then:
        manager.getAll().size() == 0
    }

    def 'When getting lifecycle filters, only filters that are lifecycle filters are returned'() {
        setup:
        LifecycleFilter f1 = Mock(LifecycleFilter)
        HttpClientFilter f2 = Mock(HttpClientFilter)

        HttpClientFilterManager manager = new HttpClientFilterManager()
        manager.add(f1)
        manager.add(f2)

        when:
        List<HttpClientFilter> readers = manager.getLifecycleFilters()

        then:
        readers.size() == 1
        readers.get(0).is f1
    }

    def 'When getting output stream filters, only filters that are output stream filters are returned'() {
        setup:
        OutputStreamFilter f1 = Mock(OutputStreamFilter)
        HttpClientFilter f2 = Mock(HttpClientFilter)

        HttpClientFilterManager manager = new HttpClientFilterManager()
        manager.add(f1)
        manager.add(f2)

        when:
        List<HttpClientFilter> readers = manager.getRequestEntityFilters()

        then:
        readers.size() == 1
        readers.get(0).is f1
    }

    def 'When getting request filters, only filters that are request filters are returned'() {
        setup:
        RequestFilter f1 = Mock(RequestFilter)
        HttpClientFilter f2 = Mock(HttpClientFilter)

        HttpClientFilterManager manager = new HttpClientFilterManager()
        manager.add(f1)
        manager.add(f2)

        when:
        List<HttpClientFilter> readers = manager.getRequestFilters()

        then:
        readers.size() == 1
        readers.get(0).is f1
    }

    def 'When getting response filters, only filters that are response filters are returned'() {
        setup:
        ResponseFilter f1 = Mock(ResponseFilter)
        HttpClientFilter f2 = Mock(HttpClientFilter)

        HttpClientFilterManager manager = new HttpClientFilterManager()
        manager.add(f1)
        manager.add(f2)

        when:
        List<HttpClientFilter> readers = manager.getResponseFilters()

        then:
        readers.size() == 1
        readers.get(0).is f1
    }

    def 'When getting retry filters, only filters that are retry filters are returned'() {
        setup:
        RetryFilter f1 = Mock(RetryFilter)
        HttpClientFilter f2 = Mock(HttpClientFilter)

        HttpClientFilterManager manager = new HttpClientFilterManager()
        manager.add(f1)
        manager.add(f2)

        when:
        List<HttpClientFilter> readers = manager.getRetryFilters()

        then:
        readers.size() == 1
        readers.get(0).is f1
    }

    def 'When filtering a request, only RequestFilter instances are used'() {
        setup:
        RequestFilter f1 = Mock(RequestFilter)
        HttpClientFilter f2 = Mock(HttpClientFilter)

        HttpClientFilterManager manager = new HttpClientFilterManager()
        manager.add(f1)
        manager.add(f2)

        HttpRequest request = Mock(HttpRequest)

        when:
        manager.filterHttpRequest(request)

        then:
        1 * f1.filter(request)
    }

    def 'When calling onStart, only LifecycleFilter instances are used'() {
        setup:
        LifecycleFilter f1 = Mock(LifecycleFilter)
        HttpClientFilter f2 = Mock(HttpClientFilter)

        HttpClientFilterManager manager = new HttpClientFilterManager()
        manager.add(f1)
        manager.add(f2)

        HttpContext context = Mock(HttpContext)

        when:
        manager.onStart(context)

        then:
        1 * f1.onStart(context)
    }

    def 'When calling onRequest, only LifecycleFilter instances are used'() {
        setup:
        LifecycleFilter f1 = Mock(LifecycleFilter)
        HttpClientFilter f2 = Mock(HttpClientFilter)

        HttpClientFilterManager manager = new HttpClientFilterManager()
        manager.add(f1)
        manager.add(f2)

        HttpContext context = Mock(HttpContext)

        when:
        manager.onRequest(context)

        then:
        1 * f1.onRequest(context)
    }

    def 'When calling onResponse, only LifecycleFilter instances are used'() {
        setup:
        LifecycleFilter f1 = Mock(LifecycleFilter)
        HttpClientFilter f2 = Mock(HttpClientFilter)

        HttpClientFilterManager manager = new HttpClientFilterManager()
        manager.add(f1)
        manager.add(f2)

        HttpContext context = Mock(HttpContext)

        when:
        manager.onResponse(context)

        then:
        1 * f1.onResponse(context)
    }

    def 'When calling onComplete, only LifecycleFilter instances are used'() {
        setup:
        LifecycleFilter f1 = Mock(LifecycleFilter)
        HttpClientFilter f2 = Mock(HttpClientFilter)

        HttpClientFilterManager manager = new HttpClientFilterManager()
        manager.add(f1)
        manager.add(f2)

        HttpContext context = Mock(HttpContext)

        when:
        manager.onComplete(context)

        then:
        1 * f1.onComplete(context)
    }

    def 'When filtering a response, only ResponseFilter instances are used'() {
        setup:
        ResponseFilter f1 = Mock(ResponseFilter)
        HttpClientFilter f2 = Mock(HttpClientFilter)

        HttpClientFilterManager manager = new HttpClientFilterManager()
        manager.add(f1)
        manager.add(f2)

        HttpResponse response = Mock(HttpResponse)

        when:
        manager.filterHttpResponse(response)

        then:
        1 * f1.filter(response)
    }

    def 'When filtering an output stream filter, only OutputStreamFilter instances are used'() {
        setup:
        OutputStreamFilter f1 = Mock(OutputStreamFilter)
        HttpClientFilter f2 = Mock(HttpClientFilter)

        HttpClientFilterManager manager = new HttpClientFilterManager()
        manager.add(f1)
        manager.add(f2)

        OutputStream outputStream = Mock(OutputStream)

        when:
        manager.filterOutputStream(outputStream)

        then:
        1 * f1.filter(outputStream)
    }

    def 'hasRetryFilters returns whether the manager contains retry filters'() {
        setup:
        HttpClientFilterManager manager = new HttpClientFilterManager()

        expect:
        !manager.hasRetryFilters()

        when:
        manager.add(Mock(RetryFilter))

        then:
        manager.hasRetryFilters()
    }

    def 'When calling onRetry and a retry filter returns true, the method returns true'() {
        setup:
        RetryFilter f1 = Mock(RetryFilter)

        HttpClientFilterManager manager = new HttpClientFilterManager()
        manager.add(f1)

        HttpContext context = Mock(HttpContext)

        when:
        boolean result = manager.isRetryRequired(context)

        then:
        1 * f1.isRetryRequired(context) >> true
        result
    }

    def 'When calling onRetry and no retry filters returns true, the method returns false'() {
        setup:
        RetryFilter f1 = Mock(RetryFilter)

        HttpClientFilterManager manager = new HttpClientFilterManager()
        manager.add(f1)

        HttpContext context = Mock(HttpContext)

        when:
        boolean result = manager.isRetryRequired(context)

        then:
        1 * f1.isRetryRequired(context) >> false
        !result
    }
}
