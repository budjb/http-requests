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
import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.converter.EntityConverterManager
import com.budjb.httprequests.converter.bundled.ByteArrayEntityWriter
import com.budjb.httprequests.converter.bundled.StringEntityWriter
import com.budjb.httprequests.test.MockHttpClientFactory
import com.budjb.httprequests.test.MockHttpResponse
import com.budjb.httprequests.test.RequestMock
import spock.lang.Specification

import java.time.LocalDate

class AuthenticationTokenHeaderFilterSpec extends Specification {
    MockHttpClientFactory httpClientFactory

    def setup() {
        EntityConverterManager converterManager = new EntityConverterManager([
            new StringEntityWriter(),
            new ByteArrayEntityWriter()
        ])
        httpClientFactory = new MockHttpClientFactory(converterManager)
    }

    def 'When authentication is valid, the request succeeds'() {
        setup:
        httpClientFactory.createMock().setRequestUri('http://foo.bar.com').setResponseStatusCode(200)

        AuthenticationTokenHeaderFilter filter = new AuthenticationTokenHeaderFilter() {
            {
                setAuthenticationToken('foo')
            }

            @Override
            protected void authenticate() {
                setAuthenticationToken('bar')
            }

            @Override
            protected String getAuthenticationTokenHeader() {
                return 'X-Auth-Token'
            }
        }

        HttpRequest request = new HttpRequest('http://foo.bar.com').addFilter(filter)

        when:
        MockHttpResponse response = (MockHttpResponse) httpClientFactory.createHttpClient().get(request)

        then:
        response.context.retries == 0
        response.context.request.getHeaders().get('X-Auth-Token') == ['foo']
    }

    def 'When authentication fails, the request is retried'() {
        setup:
        RequestMock mock = httpClientFactory.createMock().setRequestUri('http://foo.bar.com').setResponseStatusCode(401)

        AuthenticationTokenHeaderFilter filter = new AuthenticationTokenHeaderFilter() {
            {
                setAuthenticationToken('foo')
            }

            @Override
            protected void authenticate() {
                setAuthenticationToken('bar')
            }

            @Override
            protected String getAuthenticationTokenHeader() {
                return 'X-Auth-Token'
            }

            @Override
            boolean isRetryRequired(HttpContext httpContext) {
                mock.setResponseStatusCode(200)
                super.isRetryRequired(httpContext)
            }
        }

        HttpRequest request = new HttpRequest('http://foo.bar.com').addFilter(filter)

        when:
        MockHttpResponse response = (MockHttpResponse) httpClientFactory.createHttpClient().get(request)

        then:
        response.context.retries == 1
        response.context.request.getHeaders().get('X-Auth-Token') == ['bar']
        response.status == 200
    }

    def 'When the authentication token has expired, the client is re-authenticated'() {
        setup:
        httpClientFactory.createMock().setRequestUri('http://foo.bar.com').setResponseStatusCode(200)

        AuthenticationTokenHeaderFilter filter = new AuthenticationTokenHeaderFilter() {
            {
                setAuthenticationToken('foo')
                setTimeout(java.sql.Date.valueOf(LocalDate.now().minusDays(1)))
            }

            @Override
            protected void authenticate() {
                setAuthenticationToken('bar')
            }

            @Override
            protected String getAuthenticationTokenHeader() {
                return 'X-Auth-Token'
            }
        }

        HttpRequest request = new HttpRequest('http://foo.bar.com').addFilter(filter)

        when:
        MockHttpResponse response = (MockHttpResponse) httpClientFactory.createHttpClient().get(request)

        then:
        response.context.retries == 0
        response.context.request.getHeaders().get('X-Auth-Token') == ['bar']
    }
}
