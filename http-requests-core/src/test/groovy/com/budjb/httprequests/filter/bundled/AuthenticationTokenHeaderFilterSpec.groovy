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

import com.budjb.httprequests.HttpClientFactory
import com.budjb.httprequests.HttpContext
import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.converter.EntityConverterManager
import com.budjb.httprequests.converter.bundled.ByteArrayEntityWriter
import com.budjb.httprequests.converter.bundled.StringEntityWriter
import com.budjb.httprequests.mock.MockHttpClient
import com.budjb.httprequests.mock.MockHttpClientFactory
import spock.lang.Specification

class AuthenticationTokenHeaderFilterSpec extends Specification {
    MockHttpClient client

    def setup() {
        EntityConverterManager converterManager = new EntityConverterManager()
        converterManager.add(new StringEntityWriter())
        converterManager.add(new ByteArrayEntityWriter())
        HttpClientFactory httpClientFactory = new MockHttpClientFactory(converterManager)
        client = (MockHttpClient) httpClientFactory.createHttpClient()
    }

    def 'When authentication is valid, the request succeeds'() {
        setup:
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
        client.get request

        then:
        client.httpContext.retries == 0
        client.httpContext.request.getHeaders().get('X-Auth-Token') == ['foo']
    }

    def 'When authentication fails, the request is retried'() {
        setup:
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
                client.status = 200
                super.isRetryRequired(httpContext)
            }
        }
        client.status = 401

        HttpRequest request = new HttpRequest('http://foo.bar.com').addFilter(filter)

        when:
        def response = client.get request

        then:
        client.httpContext.retries == 1
        client.httpContext.request.getHeaders().get('X-Auth-Token') == ['bar']
        response.status == 200
    }

    def 'When the authentication token has expired, the client is re-authenticated'() {
        setup:
        AuthenticationTokenHeaderFilter filter = new AuthenticationTokenHeaderFilter() {
            {
                setAuthenticationToken('foo')
                setTimeout(new Date() - 1)
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
        client.get request

        then:
        client.httpContext.retries == 0
        client.httpContext.request.getHeaders().get('X-Auth-Token') == ['bar']
    }
}
