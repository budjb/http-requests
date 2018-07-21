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

import com.budjb.httprequests.*
import spock.lang.Specification

import static com.budjb.httprequests.filter.bundled.LoggingFilter.LoggingOutputStream

class LoggingFilterSpec extends Specification {
    String buffer = ''
    LoggingFilter loggingFilter

    def setup() {
        loggingFilter = new LoggingFilter() {
            @Override
            protected void write(String content) {
                buffer += content
            }
        }
    }

    def 'After onStart OutputStream filter are called, a StringBuild is present in the HTTP context and the OutputStream is wrapped'() {
        setup:
        HttpContext context = Mock(HttpContext)
        OutputStream outputStream = Mock(OutputStream)

        when:
        loggingFilter.onStart(context)

        and:
        OutputStream result = loggingFilter.filter(outputStream)

        then:
        1 * context.set('com.budjb.httprequests.filter.logging.LoggingOutputStream', {
            it instanceof LoggingOutputStream
        })
        result instanceof LoggingOutputStream
        ((LoggingOutputStream) result).outputStream.is outputStream
    }

    def 'onRequest creates and stores a StringBuilder, and renders the request'() {
        setup:
        MultiValuedMap map = new MultiValuedMap()
        map.set('foo', 'bar')
        map.set('hi', 'there')

        HttpRequest request = Mock(HttpRequest)
        request.getUri() >> 'http://foo.bar.com'
        request.getHeaders() >> map

        HttpEntity entity = Mock(HttpEntity)
        entity.getFullContentType() >> 'text/plain'

        HttpContext context = new HttpContext()
        context.method = HttpMethod.GET
        context.request = request
        context.requestEntity = entity

        String expected = 'Sending HTTP client request with the following data:\n' +
            '> GET http://foo.bar.com\n' +
            '> Content-Type: text/plain\n' +
            '> Accept: text/plain\n' +
            '> foo: bar\n' +
            '> hi: there\n'

        when:
        loggingFilter.onRequest(context)

        then:
        context.get('com.budjb.httprequests.filter.logging.StringBuilder').toString() == expected
    }

    def 'An invalid URI causes an exception'() {
        setup:
        MultiValuedMap map = new MultiValuedMap()
        map.set('foo', 'bar')
        map.set('hi', 'there')

        HttpRequest request = Mock(HttpRequest)
        request.getUri() >> 'this is bad'
        request.getHeaders() >> map

        HttpEntity entity = Mock(HttpEntity)
        entity.getFullContentType() >> 'text/plain'

        HttpContext context = new HttpContext()
        context.method = HttpMethod.GET
        context.request = request
        context.requestEntity = entity

        when:
        loggingFilter.onRequest(context)

        then:
        RuntimeException exception = thrown RuntimeException
        exception.cause instanceof URISyntaxException
    }

    def 'onResponse renders the request entity, response, and response entity'() {
        setup:
        ByteArrayOutputStream requestOutputStream = new ByteArrayOutputStream()
        LoggingOutputStream outputStream = new LoggingOutputStream(requestOutputStream)
        outputStream.write(('0123456789' * 1001).getBytes())

        ByteArrayInputStream responseInputStream = new ByteArrayInputStream(('0123456789' * 1002).getBytes())

        StringBuilder stringBuilder = new StringBuilder()

        MultiValuedMap map = new MultiValuedMap()
        map.set('foo', 'bar')
        map.set('hi', 'there')

        HttpEntity entity = Mock(HttpEntity)
        entity.getFullContentType() >> 'text/plain'
        entity.getInputStream() >> responseInputStream

        HttpResponse response = Mock(HttpResponse)
        response.getHeaders() >> map
        response.getStatus() >> 201
        response.getEntity() >> entity
        response.hasEntity() >> true

        HttpContext context = new HttpContext()
        context.set('com.budjb.httprequests.filter.logging.StringBuilder', stringBuilder)
        context.set('com.budjb.httprequests.filter.logging.LoggingOutputStream', outputStream)
        context.response = response

        String expected = '\n' + ('0123456789' * 1000) + ' ...more...\n\nReceived HTTP server response with the following data:\n' +
            '< 201\n' +
            '< foo: bar\n' +
            '< hi: there\n' +
            '\n' +
            ('0123456789' * 1000) + ' ...more...\n'

        when:
        loggingFilter.onResponse(context)

        then:
        context.get('com.budjb.httprequests.filter.logging.StringBuilder').toString() == expected
        requestOutputStream.size() == 10010
    }
}
