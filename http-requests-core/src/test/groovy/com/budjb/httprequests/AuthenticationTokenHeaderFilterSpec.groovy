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
package com.budjb.httprequests

import com.budjb.httprequests.converter.bundled.ByteArrayEntityWriter
import com.budjb.httprequests.converter.bundled.StringEntityWriter
import com.budjb.httprequests.filter.bundled.AuthenticationTokenHeaderFilter
import spock.lang.Specification

class AuthenticationTokenHeaderFilterSpec extends Specification {
    MockHttpClient client

    def setup() {
        client = new MockHttpClient()
        client.addEntityConverter(new StringEntityWriter())
        client.addEntityConverter(new ByteArrayEntityWriter())
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
        client.addFilter(filter)

        when:
        client.get { uri = "http://foo.bar.com" }

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
            boolean onRetry(HttpContext httpContext) {
                client.status = 200
                super.onRetry(httpContext)
            }
        }
        client.addFilter(filter)
        client.status = 401

        when:
        def response = client.get { uri = "http://foo.bar.com" }

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
        client.addFilter(filter)

        when:
        client.get { uri = "http://foo.bar.com" }

        then:
        client.httpContext.retries == 0
        client.httpContext.request.getHeaders().get('X-Auth-Token') == ['bar']
    }
}
