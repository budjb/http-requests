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

import com.budjb.httprequests.converter.EntityConverterManager
import com.budjb.httprequests.converter.bundled.StringEntityReader
import spock.lang.Specification
import spock.lang.Unroll

class HttpResponseSpec extends Specification {
    def 'When a charset is provided, the resulting string is built using it'() {
        setup:
        EntityConverterManager converterManager = new EntityConverterManager()
        converterManager.add(new StringEntityReader())

        HttpResponse response = new MockHttpResponse(
            new HttpRequest(),
            converterManager,
            200,
            [:],
            'text/plain;charset=euc-jp',
            new ByteArrayInputStream('åäö'.getBytes())
        )

        when:
        String entity = response.getEntity(String)

        then:
        entity == '奪辰旦'
    }

    def 'When no charset is provided, UTF-8 is used'() {
        setup:
        EntityConverterManager converterManager = new EntityConverterManager()
        converterManager.add(new StringEntityReader())

        HttpResponse response = new MockHttpResponse(
            new HttpRequest(),
            converterManager,
            200,
            [:],
            'text/plain',
            new ByteArrayInputStream('åäö'.getBytes())
        )

        when:
        String entity = response.getEntity(String)

        then:
        response.charset == 'UTF-8'
        entity == 'åäö'
    }

    def 'Verify header parsing and retrieval'() {
        setup:
        def response = new MockHttpResponse(
            new HttpRequest(),
            new EntityConverterManager(),
            200,
            [
                foo : ['bar', 'baz'],
                hi  : ['there'],
                peek: 'boo'
            ],
            null,
            null
        )

        expect:
        response.getHeaders() == [
            foo : ['bar', 'baz'],
            hi  : ['there'],
            peek: ['boo']
        ]
        response.getFlattenedHeaders() == [
            foo : ['bar', 'baz'],
            hi  : 'there',
            peek: 'boo'
        ]
        response.getHeaders('foo') == ['bar', 'baz']
        response.getHeaders('hi') == ['there']
        response.getHeaders('peek') == ['boo']
        response.getHeader('foo') == 'bar'
        response.getHeader('hi') == 'there'
        response.getHeader('peek') == 'boo'
    }

    def 'Ensure the Allow header is parsed properly'() {
        setup:
        HttpResponse response = new MockHttpResponse(
            new HttpRequest(),
            new EntityConverterManager(),
            200,
            ['Allow': 'GET,POST,PUT'],
            null,
            null
        )

        expect:
        response.getAllow() == [HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT]
    }

    @Unroll
    def 'When the content-type is #fullContentType, the content type and character set are parsed properly'() {
        setup:
        HttpResponse response = new MockHttpResponse(
            new HttpRequest(),
            new EntityConverterManager(),
            200,
            [:],
            fullContentType,
            null
        )

        expect:
        response.getContentType() == contentType
        response.getCharset() == charset

        where:
        fullContentType                   | contentType  | charset
        null                              | null         | null
        ''                                | null         | null
        'text/plain'                      | 'text/plain' | 'UTF-8'
        'text/plain;'                     | 'text/plain' | 'UTF-8'
        'text/plain;charset=foobar'       | 'text/plain' | 'foobar'
        'text/plain;q=0.9;charset=foobar' | 'text/plain' | 'foobar'
        'text/plain;q=0.9'                | 'text/plain' | 'UTF-8'
        'text/plain;charset'              | 'text/plain' | 'UTF-8'
        'text/plain;charset='             | 'text/plain' | 'UTF-8'
    }

    def 'When the response contains no entity, hasEntity() returns false'() {
        setup:
        HttpResponse response = new MockHttpResponse(
            new HttpRequest(),
            new EntityConverterManager(),
            200,
            [:],
            null,
            null
        )

        expect:
        !response.hasEntity()
    }

    def 'When the response contains an un-buffered input stream, hasEntity() returns true'() {
        setup:
        HttpResponse response = new MockHttpResponse(
            HttpRequest.build { bufferResponseEntity = false },
            new EntityConverterManager(),
            200,
            [:],
            null,
            new ByteArrayInputStream([1, 2, 3] as byte[])
        )

        expect:
        response.hasEntity()
    }

    def 'When the response contains a byte array entity, hasEntity() returns true'() {
        setup:
        HttpResponse response = new MockHttpResponse(
            HttpRequest.build { bufferResponseEntity = true },
            new EntityConverterManager(),
            200,
            [:],
            null,
            new ByteArrayInputStream([1, 2, 3] as byte[])
        )

        expect:
        response.hasEntity()
    }
}
