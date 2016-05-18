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
