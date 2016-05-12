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

import spock.lang.Specification
import spock.lang.Unroll

class HttpRequestSpec extends Specification {
    @Unroll
    def 'When an HttpRequest is build with URI #raw, the properties of the URI are parsed correctly'() {
        setup:
        URI uri = new URI(raw)

        when:
        HttpRequest request = new HttpRequest(uri)

        then:
        request.getUri() == parsed
        request.getQueryParameters() == query

        where:
        raw                                         | parsed                              | query
        'https://budjb.com/path/to/nothing?foo=bar' | 'https://budjb.com/path/to/nothing' | [foo: ['bar']]
        'https://host/path?f'                       | 'https://host/path'                 | [f: ['']]
        'https://host?f=1&f=2&b=3&b=4'              | 'https://host'                      | [f: ['1', '2'], b: ['3', '4']]
        'http://host'                               | 'http://host'                       | [:]
        'http://foo.bar.com:8080'                   | 'http://foo.bar.com:8080'           | [:]
        'https://foo.bar.com:993'                   | 'https://foo.bar.com:993'           | [:]
        'https://host?f=b=a=r'                      | 'https://host'                      | [f: ['b=a=r']]
    }

    def 'When the builder syntax is used, all properties are set correctly'() {
        setup:
        HttpRequest request = new HttpRequest()

        when:
        request.setUri('http://localhost')
            .setCharset('ISO-8859-8')
            .setAccept('text/plain')
            .setContentType('application/json')
            .addHeader('foo', 'bar')
            .addHeader('foo', ['1', '2'])
            .addHeaders([hi: ['there']])
            .addQueryParameter('foo', 'bar')
            .addQueryParameter('foo', ['1', '2'])
            .addQueryParameters([hi: ['there']])
            .setSslValidated(false)
            .setReadTimeout(5000)
            .setConnectionTimeout(10000)
            .setBufferResponseEntity(false)

        then:
        request.getCharset() == 'ISO-8859-8'
        request.getAccept() == 'text/plain'
        request.getContentType() == 'application/json'
        request.getHeaders() == [foo: ['bar', '1', '2'], hi: ['there']]
        request.getQueryParameters() == [foo: ['bar', '1', '2'], hi: ['there']]
        !request.isSslValidated()
        !request.getBufferResponseEntity()
        request.connectionTimeout == 10000
        request.readTimeout == 5000
        request.uri == 'http://localhost'

        when:
        request
            .setHeader('foo', 'meh')
            .setHeader('hi', 'meh')
            .setQueryParameter('foo', 'meh')
            .setQueryParameter('hi', 'meh')

        then:
        request.headers == [foo: ['meh'], hi: ['meh']]
        request.queryParameters == [foo: ['meh'], hi: ['meh']]

        when:
        request
            .setHeader('foo', ['bar', 'baz'])
            .setHeader('hi', ['there', 'man'])
            .setQueryParameter('foo', ['bar', 'baz'])
            .setQueryParameter('hi', ['there', 'man'])

        then:
        request.headers == [foo: ['bar', 'baz'], hi: ['there', 'man']]
        request.queryParameters == [foo: ['bar', 'baz'], hi: ['there', 'man']]

        when:
        request
            .setHeaders([var: 'val'])
            .setQueryParameters([var: 'val1'])

        then:
        request.headers == [var: ['val']]
        request.queryParameters == [var: ['val1']]
    }

    def 'When a URI is passed to setUri(), the request properties are set as expected'() {
        setup:
        def uri = new URI('https://localhost:12345?f=&foo=bar&foo=baz')
        def request = new HttpRequest().setUri('http://foo.bar.com?var=val')

        when:
        request.setUri(uri)

        then:
        request.uri == 'https://localhost:12345'
        request.queryParameters == [f: [''], foo: ['bar', 'baz']]
    }

    def 'When a request is built with the closure builder, the properties are set correctly'() {
        when:
        def request = HttpRequest.build {
            uri = 'https://localhost:8080?going=away'
            accept = 'application/json'
            contentType = 'text/plain'
            connectionTimeout = 10000
            readTimeout = 5000
            followRedirects = false
            sslValidated = false
            bufferResponseEntity = false
            charset = 'ISO-1234'
            headers = [foo: 'bar']
        }

        then:
        request.uri == 'https://localhost:8080'
        request.accept == 'application/json'
        request.contentType == 'text/plain'
        request.connectionTimeout == 10000
        request.readTimeout == 5000
        !request.followRedirects
        !request.sslValidated
        !request.bufferResponseEntity
        request.charset == 'ISO-1234'
        request.headers == [foo: ['bar']]
        request.queryParameters == [going: ['away']]
    }

    def 'When the response contains no entity, hasEntity() returns false'() {
        expect:
        !new HttpResponse().hasEntity()
    }

    def 'When the response contains an open input stream, hasEntity() returns true'() {
        setup:
        def response = new HttpResponse()
        response.setRequest(new HttpRequest())
        response.setEntity(new ByteArrayInputStream([1, 2, 3] as byte[]))

        expect:
        response.hasEntity()
    }

    def 'When the response contains a byte array entity, hasEntity() returns true'() {
        setup:
        def response = new HttpResponse()
        response.setEntity(new ByteArrayInputStream([1, 2, 3] as byte[]))

        expect:
        response.hasEntity()
    }
}
