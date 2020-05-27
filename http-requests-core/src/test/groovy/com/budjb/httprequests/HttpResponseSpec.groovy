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

import com.budjb.httprequests.converter.EntityConverterManager
import com.budjb.httprequests.converter.bundled.StringEntityReader
import com.budjb.httprequests.mock.MockHttpResponse
import spock.lang.Specification

class HttpResponseSpec extends Specification {
    def 'When a charset is provided, the resulting string is built using it'() {
        setup:
        EntityConverterManager converterManager = new EntityConverterManager([new StringEntityReader()])

        HttpEntity httpEntity = new HttpEntity(new ByteArrayInputStream('åäö'.getBytes()), 'text/plain', 'euc-jp')
        HttpResponse response = new MockHttpResponse(converterManager)
        response.entity = httpEntity


        when:
        String entity = response.getEntity(String)

        then:
        entity == '奪辰旦'
    }

    def 'Verify header parsing and retrieval'() {
        setup:
        MultiValuedMap headers = new MultiValuedMap()
        headers.add("foo", ["bar", "baz"])
        headers.add("hi", "there")
        headers.add("peek", "boo")

        HttpResponse response = new MockHttpResponse(EntityConverterManager.empty)
        response.headers = headers

        expect:
        response.getHeaders() == [
            foo : ['bar', 'baz'],
            hi  : ['there'],
            peek: ['boo']
        ]
        response.getHeaders('foo') == ['bar', 'baz']
        response.getHeaders('hi') == ['there']
        response.getHeaders('peek') == ['boo']
        response.getHeader('foo') == 'bar'
        response.getHeader('hi') == 'there'
        response.getHeader('peek') == 'boo'
    }

    def 'When the response contains no entity, hasEntity() returns false'() {
        setup:
        HttpResponse response = new MockHttpResponse(EntityConverterManager.empty)

        expect:
        !response.hasEntity()
    }

    def 'When the response contains an entity, hasEntity() returns true'() {
        setup:
        HttpEntity entity = new HttpEntity(new ByteArrayInputStream([1, 2, 3] as byte[]))
        HttpResponse response = new MockHttpResponse(EntityConverterManager.empty)
        response.entity = entity

        expect:
        response.hasEntity()
    }
}
