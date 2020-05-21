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
package com.budjb.httprequests

import com.budjb.httprequests.application.TestApp
import com.budjb.httprequests.exception.HttpInternalServerErrorException
import com.budjb.httprequests.filter.HttpClientFilter
import com.budjb.httprequests.filter.RetryFilter
import com.budjb.httprequests.filter.bundled.BasicAuthFilter
import com.budjb.httprequests.filter.bundled.GZIPFilter
import com.budjb.httprequests.filter.bundled.HttpStatusExceptionFilter
import com.budjb.httprequests.filter.bundled.Slf4jLoggingFilter
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Ignore
import spock.lang.Unroll

import java.util.zip.GZIPInputStream

@Ignore
@SpringBootTest(classes = TestApp, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = 'spring.mvc.dispatch-trace-request=true')
abstract class HttpIntegrationTestSuiteSpec extends AbstractIntegrationSpec {
    def 'When a GET request is made to /testBasicGet, the proper response is received'() {
        when:
        def response = httpClientFactory.createHttpClient().get "${baseUrl}/testBasicGet"

        then:
        response.getEntity(String) == 'The quick brown fox jumps over the lazy dog.'
    }

    def 'When a DELETE request is made to /testBasicDelete, the proper response is received'() {
        when:
        def response = httpClientFactory.createHttpClient().delete "${baseUrl}/testBasicDelete"

        then:
        response.getEntity(String) == "Please don't hurt me!"
    }

    def 'When a POST request is made to /testBasicPost, the proper response is received'() {
        when:
        def response = httpClientFactory.createHttpClient().post("${baseUrl}/testBasicPost", "Please don't play the repeating game!")

        then:
        response.getEntity(String) == "Please don't play the repeating game!"
    }

    def 'When a PUT request is made to /testBasicPut, the proper response is received'() {
        when:
        def response = httpClientFactory.createHttpClient().put("${baseUrl}/testBasicPut", "Please don't play the repeating game!")

        then:
        response.getEntity(String) == "Please don't play the repeating game!"
    }

    def 'When an Accept header is assigned, the server receives and processes it correctly'() {
        when:
        def response = httpClientFactory.createHttpClient().get(new HttpRequest()
            .setUri("${baseUrl}/testAccept")
            .setHeader('Accept', 'text/plain')
        )

        then:
        response.getEntity(String) == 'I am plain text.'
    }

    def 'When an unknown Accept header is assigned, the server receives it and returns an error'() {
        when:
        def response = httpClientFactory.createHttpClient().get(new HttpRequest()
            .setUri("${baseUrl}/testAccept")
            .setHeader('Accept', 'foo/bar')
        )

        then:
        response.status == 406
    }

    def 'When a read timeout is reached, a SocketTimeoutException occurs'() {
        when:
        httpClientFactory.createHttpClient().get(new HttpRequest()
            .setUri("${baseUrl}/testReadTimeout")
            .setReadTimeout(1000)
        )

        then:
        thrown SocketTimeoutException
    }

    def 'When a call to /testBasicGet is made, the proper byte stream is received'() {
        when:
        def response = httpClientFactory.createHttpClient().get("${baseUrl}/testBasicGet")

        then:
        response.getEntity(byte[]) == [84, 104, 101, 32, 113, 117, 105, 99, 107, 32, 98, 114, 111, 119, 110, 32, 102, 111, 120, 32, 106, 117, 109, 112, 115, 32, 111, 118, 101, 114, 32, 116, 104, 101, 32, 108, 97, 122, 121, 32, 100, 111, 103, 46] as byte[]
    }

    def 'When a redirect is received and the client is configured to follow it, the proper response is received'() {
        when:
        def response = httpClientFactory.createHttpClient().get("${baseUrl}/testRedirect")

        then:

        response.getEntity(String) == 'The quick brown fox jumps over the lazy dog.'
    }

    def 'When a redirect is received and the client is configured to not follow it, the response status code is 302'() {
        when:
        def response = httpClientFactory.createHttpClient().get(new HttpRequest()
            .setUri("${baseUrl}/testRedirect")
            .setFollowRedirects(false)
        )

        then:
        response.status == 302
    }

    def 'When a request includes headers, the server receives them correctly'() {
        when:
        def response = httpClientFactory.createHttpClient().get(new HttpRequest()
            .setUri("${baseUrl}/testHeaders")
            .addHeaders(new MultiValuedMap([foo: ['bar'], key: ['value']]))
        )

        then:
        def json = response.getEntity(Map)
        json.foo == ['bar']
        json.key == ['value']
    }

    def 'When a request includes headers with multiple values, the server receives them correctly'() {
        setup:
        def request = new HttpRequest()
            .setUri("${baseUrl}/testHeaders")
            .addHeader('foo', 'bar')
            .addHeader('foo', 'baz')
            .addHeader('hi', 'there')

        when:
        def response = httpClientFactory.createHttpClient().get(request)

        then:
        def json = response.getEntity(Map)
        json.foo == ['bar,baz'] || json.foo == ['bar', 'baz']
        json.hi == ['there']
    }

    def 'When a request includes query parameters, the server receives them correctly'() {
        setup:
        def request = new HttpRequest()
            .setUri("${baseUrl}/testParams")
            .addQueryParameter('foo', 'bar')
            .addQueryParameter('key', 'value')

        when:
        def response = httpClientFactory.createHttpClient().get(request)

        then:
        response.getEntity(Map) == [foo: ['bar'], key: ['value']]
    }

    def 'When a request includes query parameters with multiple values, the server receives them correctly'() {
        setup:
        def request = new HttpRequest()
            .setUri("${baseUrl}/testParams")
            .addQueryParameter('foo', 'bar')
            .addQueryParameter('foo', 'baz')
            .addQueryParameter('hi', 'there')

        when:
        def response = httpClientFactory.createHttpClient().get(request)

        then:
        response.getEntity(Map) == ['foo': ['bar', 'baz'], 'hi': ['there']]
    }

    def 'When a response has a status of 500, an HttpInternalServerErrorException is thrown'() {
        when:
        httpClientFactory.createHttpClient().get(new HttpRequest().setUri("${baseUrl}/test500").addFilter(new HttpStatusExceptionFilter()))

        then:
        thrown HttpInternalServerErrorException
    }

    def 'When a response has a status of 500 but the client is configured to not throw exceptions, no exception is thrown'() {
        when:
        def response = httpClientFactory.createHttpClient().get "${baseUrl}/test500"

        then:
        notThrown HttpInternalServerErrorException
        response.status == 500
    }

    def 'When the client sends form data as the request entity, the server receives them correctly'() {
        setup:
        FormData formData = new FormData()
        formData.addField('foo', 'bar')
        formData.addField('key', 'value')


        when:
        def response = httpClientFactory.createHttpClient().post("${baseUrl}/testForm", formData)

        then:
        response.getEntity(Map) == ['foo': ['bar'], 'key': ['value']]
    }

    def 'When the client sends form data with multiple values as the request entity, the server receives them correctly'() {
        setup:
        FormData formData = new FormData()
        formData.addField('foo', 'bar')
        formData.addField('foo', 'baz')
        formData.addField('key', 'value')


        when:
        def response = httpClientFactory.createHttpClient().post("${baseUrl}/testForm", formData)

        then:
        response.getEntity(Map) == ['foo': ['bar', 'baz'], 'key': ['value']]
    }

    def 'When a server requires basic authentication but none is provided, an HttpUnauthorizedException is thrown'() {
        when:
        def response = httpClientFactory.createHttpClient().get("${baseUrl}/testAuth")

        then:
        response.status == 401
    }

    def 'When a server requires basic authentication and the client provides it, the proper response is received'() {
        when:
        def response = httpClientFactory.createHttpClient().get(new HttpRequest("${baseUrl}/testAuth").addFilter(new BasicAuthFilter('foo', 'bar')))

        then:
        response.getEntity(String) == 'welcome'
    }

    def 'If a retry filter requests a retry, ensure its proper operations'() {
        setup:
        boolean ran = false

        RetryFilter filter = new RetryFilter() {
            @Override
            boolean isRetryRequired(HttpContext context) {
                if (context.getRetries() == 0) {
                    ran = true
                    return true
                }
                return false
            }
        }

        when:
        httpClientFactory.createHttpClient().get(new HttpRequest("${baseUrl}/testBasicGet").addFilter(filter))

        then:
        ran
    }

    def 'Validate that a request can be made with a Map'() {
        when:
        def response = httpClientFactory.createHttpClient().post("${baseUrl}/testBasicPost", [foo: ['bar', 'baz']])

        then:
        response.getEntity(Map) == [foo: ['bar', 'baz']]
        response.getEntity(String) == '{"foo":["bar","baz"]}'
    }

    def 'Validate request form of POST with no entity works'() {
        setup:
        def request = new HttpRequest("${baseUrl}/testBasicPost")

        when:
        def response = httpClientFactory.createHttpClient().post(request)

        then:
        !response.hasEntity()
    }

    def 'Validate request form of POST with a byte array entity works'() {
        when:
        def response = httpClientFactory.createHttpClient().post("${baseUrl}/testBasicPost", 'Hello'.getBytes())

        then:
        response.getEntity(String) == 'Hello'
    }

    def 'Validate request form of POST with a string entity works'() {
        when:
        def response = httpClientFactory.createHttpClient().post("${baseUrl}/testBasicPost", 'Hello')

        then:
        response.getEntity(String) == 'Hello'
    }

    def 'Validate request form of POST with an input stream works'() {
        setup:
        def stream = new ByteArrayInputStream('Hello'.getBytes())

        when:
        def response = httpClientFactory.createHttpClient().post("${baseUrl}/testBasicPost", stream)

        then:
        response.getEntity(String) == 'Hello'
    }

    def 'Validate request form of POST with FormData works'() {
        setup:
        def form = new FormData()
        form.addField('foo', 'bar')
        form.addField('foo', 'baz')
        form.addField('hi', 'there')

        when:
        def response = httpClientFactory.createHttpClient().post("${baseUrl}/testBasicPost", form)

        then:
        response.getEntity(String).contains('foo=bar')
        response.getEntity(String).contains('foo=baz')
        response.getEntity(String).contains('hi=there')
    }

    def 'Validate request form of PUT with no entity works'() {
        when:
        def response = httpClientFactory.createHttpClient().put("${baseUrl}/testBasicPut")

        then:
        !response.hasEntity()
    }

    def 'Validate request form of PUT with a byte array entity works'() {
        when:
        def response = httpClientFactory.createHttpClient().put("${baseUrl}/testBasicPut", 'Hello'.getBytes())

        then:
        response.getEntity(String) == 'Hello'
    }

    def 'Validate request form of PUT with a string entity works'() {
        when:
        def response = httpClientFactory.createHttpClient().put("${baseUrl}/testBasicPut", 'Hello')

        then:
        response.getEntity(String) == 'Hello'
    }

    def 'Validate request form of PUT with an input stream works'() {
        setup:
        def stream = new ByteArrayInputStream('Hello'.getBytes())

        when:
        def response = httpClientFactory.createHttpClient().put("${baseUrl}/testBasicPut", stream)

        then:
        response.getEntity(String) == 'Hello'
    }

    /*
    def 'Validate request form of PUT with FormData works'() {
        setup:
        def form = new FormData()
        form.addField('foo', 'bar')
        form.addField('foo', 'baz')
        form.addField('hi', 'there')

        def request = new HttpRequest("${baseUrl}/testBasicPut")

        when:
        def response = httpClientFactory.createHttpClient().put(request, form)

        then:
        response.entityAsString == 'foo=bar&foo=baz&hi=there'
    }
    */

    def 'When a content type is already set, it will not be overwritten by a converter'() {
        when:
        def response = httpClientFactory.createHttpClient().post("${baseUrl}/printContentType", new ConvertingHttpEntity('hi!', 'foo/bar'))

        then:
        response.getEntity(String).startsWith('foo/bar')
    }

    def 'When no content type is set, it will be set by the converter'() {
        when:
        def response = httpClientFactory.createHttpClient().post("${baseUrl}/printContentType", 'hi!')

        then:
        response.getEntity(String).startsWith('text/plain')
    }

    def 'When the response is not buffered, the entity can only be retrieved once'() {
        setup:
        def response = httpClientFactory.createHttpClient().post(
            new HttpRequest("${baseUrl}/testBasicPost").setBufferResponseEntity(false),
            [foo: ['bar', 'baz']]
        )

        expect:
        response.getEntity(Map) == [foo: ['bar', 'baz']]

        when:
        response.getEntity(String)

        then:
        thrown IOException
    }

    def 'When the response is buffered, the entity can be retrieved multiple times'() {
        setup:
        def response = httpClientFactory.createHttpClient().post(
            new HttpRequest("${baseUrl}/testBasicPost").setBufferResponseEntity(true),
            [foo: ['bar', 'baz']]
        )

        expect:
        response.getEntity(Map) == [foo: ['bar', 'baz']]
        response.getEntity(String) == '{"foo":["bar","baz"]}'
    }

    def 'When a GZIPFilter is applied to the request, the request is compressed'() {
        when:
        def response = httpClientFactory.createHttpClient().post(
            new HttpRequest("${baseUrl}/echo").addFilter(new GZIPFilter()),
            'Hello, world!'
        )

        then:
        StreamUtils.readString(new GZIPInputStream(response.getEntity().getInputStream()), 'UTF-8') == 'Hello, world!'
    }

    def 'Ensure the LoggingFilter does not cause interruptions to HTTP requests.'() {
        setup:
        HttpRequest request = new HttpRequest("${baseUrl}/testBasicPost")

        when:
        def response = httpClientFactory.createHttpClient().post(request, "Hello, world")

        then:
        notThrown IOException
        response.getEntity(String) == 'Hello, world'

        when:
        request = new HttpRequest("${baseUrl}/testBasicGet").setHeader('Accept', 'text/plain')
        response = httpClientFactory.createHttpClient().get request

        then:
        notThrown IOException
        response.getEntity(String) == 'The quick brown fox jumps over the lazy dog.'
    }

    @Unroll
    def 'When an accepted character set #charset is requested, the proper output is received and parsed'() {
        setup:
        String input = 'åäö'

        when:
        def response = httpClientFactory.createHttpClient().post(
            new HttpRequest("${baseUrl}/acceptContentType").setHeader('Accept', "text/plain;charset=${charset}"),
            new ConvertingHttpEntity(input, 'text/plain', charset)
        )

        then:
        response.entity.contentType == 'text/plain'
        response.entity.charSet.equalsIgnoreCase(charset)
        response.getEntity(String) == output

        where:
        charset  | output
        'euc-jp' | '奪辰旦'
        'UTF-8'  | 'åäö'
    }

    @Unroll
    def 'When #writeType is sent, the entity was properly written and received'() {
        when:
        def response = httpClientFactory.createHttpClient().post "${baseUrl}/echo", inp

        then:
        response.getEntity(String) == output
        response.getEntity().contentType == contentType

        when:
        def read = response.getEntity(readType)

        then:
        read != null
        readType.isInstance(read)
        notThrown(Exception)

        where:
        writeType  | inp                                    | output            | readType | contentType
        'Map'      | [foo: ['bar']]                         | '{"foo":["bar"]}' | Map      | 'application/json'
        'List'     | ['foo', 'bar']                         | '["foo","bar"]'   | List     | 'application/json'
        'String'   | "Hello, world!"                        | "Hello, world!"   | String   | 'text/plain'
        'byte[]'   | [102, 111, 111, 98, 97, 114] as byte[] | 'foobar'          | byte[]   | 'application/octet-stream'
        'FormData' | {
            def form = new FormData(); form.addField('foo', 'bar'); return form
        }.call()                                            | 'foo=bar'         | String   | 'application/x-www-form-urlencoded'
    }

    def 'Closing the response multiple times has no effect and does not cause an error'() {
        setup:
        def response = httpClientFactory.createHttpClient().get "${baseUrl}/testBasicGet"

        when:
        response.close()
        response.close()
        response.close()

        then:
        notThrown Exception
    }

    def 'When a request is retried, the entity is resent correctly'() {
        setup:
        InputStream inputStream = new ByteArrayInputStream('test payload'.bytes)
        RetryFilter retryFilter = new RetryFilter() {
            @Override
            boolean isRetryRequired(HttpContext context) {
                return context.retries == 0
            }
        }

        when:
        def response = httpClientFactory.createHttpClient().post new HttpRequest("${baseUrl}/testBasicPost").addFilter(retryFilter), inputStream

        then:
        response.status == 200
        response.hasEntity()
        response.getEntity(String) == 'test payload'
    }

    def 'When logging a request with a GET HTTP method, a NullPointerException is not thrown'() {
        setup:
        HttpRequest request = new HttpRequest()
        request.uri = "${baseUrl}/testBasicGet"
        request.addFilter(new Slf4jLoggingFilter())

        when:
        httpClientFactory.createHttpClient().get(request)

        then:
        notThrown NullPointerException
    }

    def 'When a request is made with a filter that is closeable, it is closed'() {
        setup:
        CloseableFilter filter = new CloseableFilter()

        HttpRequest request = new HttpRequest()
        request.uri = "${baseUrl}/testBasicGet"
        request.addFilter(filter)

        when:
        httpClientFactory.createHttpClient().get(request)

        then:
        filter.closed
    }

    def 'When a URL does not exist and the response has no entity, a 404 status code should be captured with no exception'() {
        when:
        HttpResponse response = httpClientFactory.createHttpClient().get("${baseUrl}/test404")

        then:
        response.status == 404
        response.getEntity(String) == null
    }

    def 'Query parameters that have been set but contain no values are still sent with the request URI'() {
        setup:
        HttpRequest request = new HttpRequest("${baseUrl}/testParams")
        request.setQueryParameter('foo', null)

        when:
        Map response = httpClientFactory.createHttpClient().get(request).getEntity(Map)

        then:
        response.containsKey('foo')
        !((List) response.get('foo'))[0]
    }

    def 'Headers that have been set but contain no value are still sent with the request'() {
        setup:
        HttpRequest request = new HttpRequest("${baseUrl}/testHeaders")
        request.setHeader('x-foo', null)

        when:
        Map response = httpClientFactory.createHttpClient().get(request).getEntity(Map)

        then:
        response.containsKey('x-foo')
        !((List) response.get('x-foo'))[0]
    }

    def 'A Content-Type contained in the request is preferred over the one in the entity'() {
        setup:
        HttpRequest request = new HttpRequest("${baseUrl}/echo")
        request.setHeader('content-type', 'foo/bar')

        when:
        def response = httpClientFactory.createHttpClient().post(request, 'foo')

        then:
        response.getHeader('content-type') == 'foo/bar'
    }

    def 'Sending a null request entity does not send any request entity'() {
        setup:
        HttpRequest request = new HttpRequest("${baseUrl}/testBasicPost")

        when:
        def response = httpClientFactory.createHttpClient().post(request, null)

        then:
        !response.hasEntity()
    }

    def 'Retrieving a response entity when the response does not contain and entity does not result in a NullPointerException'() {
        setup:
        HttpRequest request = new HttpRequest("${baseUrl}/testBasicPost")

        when:
        def response = httpClientFactory.createHttpClient().post(request, null)

        then:
        !response.hasEntity()
        response.getEntity(String) == null
    }

    static class CloseableFilter implements HttpClientFilter, Closeable {
        boolean closed = false

        @Override
        void close() throws IOException {
            closed = true
        }
    }
}
