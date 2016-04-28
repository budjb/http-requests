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

        HttpResponse response = new HttpResponse()
        response.converterManager = converterManager
        response.entity = new ByteArrayInputStream('åäö'.getBytes())
        response.charset = 'euc-jp'

        when:
        String entity = response.getEntity(String)

        then:
        entity == '奪辰旦'
    }

    def 'When no charset is provided, UTF-8 is used'() {
        setup:
        EntityConverterManager converterManager = new EntityConverterManager()
        converterManager.add(new StringEntityReader())

        HttpResponse response = new HttpResponse()
        response.converterManager = converterManager
        response.entity = new ByteArrayInputStream('åäö'.getBytes())

        when:
        String entity = response.getEntity(String)

        then:
        response.charset == 'UTF-8'
        entity == 'åäö'
    }

    @Unroll
    def 'When a #type charset is assigned, charset is not actually assigned'() {
        setup:
        HttpResponse response = new HttpResponse()

        when:
        response.charset = charset

        then:
        response.charset == 'UTF-8'

        where:
        type    | charset
        'null'  | null
        'blank' | ''
    }

    def 'When a null character set is assigned, the existing value is not overwritten'() {
        setup:
        def response = new HttpResponse()
        response.setCharset('ISO-8859-1')

        when:
        response.setCharset(null)

        then:
        response.charset == 'ISO-8859-1'
    }

    def 'Verify header parsing and retrieval'() {
        setup:
        def response = new HttpResponse()

        when:
        response.headers = [
            foo: ['bar', 'baz'],
            hi: ['there'],
            peek: 'boo'
        ]

        then:
        response.getHeaders() == [
            foo: ['bar', 'baz'],
            hi: ['there'],
            peek: ['boo']
        ]
        response.getFlattenedHeaders() == [
            foo: ['bar', 'baz'],
            hi: 'there',
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
        HttpResponse response = new HttpResponse()
        response.setHeaders(['Allow': 'GET,POST,PUT'])

        expect:
        response.getAllow() == [HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT]
    }
}
