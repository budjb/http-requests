/*
 * Copyright 2016-2020 the original author or authors.
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

package com.budjb.httprequests.test

import com.budjb.httprequests.HttpMethod
import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.converter.EntityConverterManager
import com.budjb.httprequests.groovy.JsonEntityReader
import com.budjb.httprequests.groovy.JsonEntityWriter
import spock.lang.Specification

class FunctionalSpec extends Specification {
    MockHttpClientFactory httpClientFactory
    EntityConverterManager entityConverterManager

    def setup() {
        entityConverterManager = new EntityConverterManager([
            new JsonEntityWriter(),
            new JsonEntityReader()
        ])
        httpClientFactory = new MockHttpClientFactory(entityConverterManager)
    }

    def 'A simple mocked GET request with no response entity returns no entity'() {
        setup:
        httpClientFactory.createMock().setRequestUri('http://localhost/foo/bar')

        when:
        httpClientFactory.createHttpClient().get('http://localhost/foo/bar')

        then:
        httpClientFactory.allMocksCalled()
    }

    def 'A simple mocked GET request returns the expected response entity'() {
        setup:
        RequestMock mock = httpClientFactory
            .createMock()
            .setRequestUri('http://localhost/foo/bar')
            .setResponseEntity([
                foo: 'bar',
                baz: 'boz'
            ])

        when:
        def response = httpClientFactory.createHttpClient().get('http://localhost/foo/bar').getEntity(Map)

        then:
        mock.called()
        httpClientFactory.allMocksCalled()
        response == [
            foo: 'bar',
            baz: 'boz'
        ]
    }

    def 'An HTTP request without a mock throws an UnmatchedRequestMockException'() {
        when:
        httpClientFactory.createHttpClient().get('http://localhost/foo/bar')

        then:
        thrown UnmatchedRequestMockException
    }

    def 'A mock does not match when the URI matches but the HTTP method does not'() {
        setup:
        httpClientFactory.createMock().setRequestUri('http://localhost/foo/bar').setRequestMethod(HttpMethod.POST)

        when:
        httpClientFactory.createHttpClient().get('http://localhost/foo/bar')

        then:
        thrown UnmatchedRequestMockException
    }

    def 'A mock matches when the URI and HTTP method match'() {
        setup:
        def mock = httpClientFactory.createMock().setRequestUri('http://localhost/foo/bar').setRequestMethod(HttpMethod.POST)

        when:
        httpClientFactory.createHttpClient().post('http://localhost/foo/bar')

        then:
        notThrown UnmatchedRequestMockException
        mock.called()
    }

    def 'A mock does not match when request headers do not match'() {
        setup:
        httpClientFactory
            .createMock()
            .setRequestUri('http://localhost/foo/bar')
            .addRequestHeader('foo', 'bar')

        when:
        HttpRequest request = new HttpRequest()
            .setUri('http://localhost/foo/bar')
            .addHeader('foo', 'bar')
            .addHeader('baz', 'boz')
        httpClientFactory.createHttpClient().get(request)

        then:
        thrown UnmatchedRequestMockException
    }

    def 'A mock matches when request headers match'() {
        setup:
        def mock = httpClientFactory
            .createMock()
            .setRequestUri('http://localhost/foo/bar')
            .addRequestHeader('foo', 'bar')

        when:
        HttpRequest request = new HttpRequest()
            .setUri('http://localhost/foo/bar')
            .addHeader('foo', 'bar')
        httpClientFactory.createHttpClient().get(request)

        then:
        notThrown UnmatchedRequestMockException
        mock.called()
    }
}
