/*
 * Copyright 2016-2020 the original author or authors.
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

package com.budjb.httprequests.converter.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

class JacksonEntityReaderSpec extends Specification {
    ObjectMapper objectMapper
    JacksonEntityReader reader

    def setup() {
        objectMapper = new ObjectMapper()
        reader = new JacksonEntityReader(objectMapper)
    }

    def 'The Jackson entity reader supports de-serializing to a POGO'() {
        setup:
        InputStream entity = new ByteArrayInputStream('{"foo": "hi", "bar": true, "baz": 5}'.getBytes('utf-8'))

        expect:
        reader.supports(Foo, null, null)

        when:
        def result = reader.read(Foo, entity, null, null)

        then:
        result instanceof Foo
        result.foo == 'hi'
        result.bar
        result.baz == 5
    }

    def 'The Jackson entity reader supports de-serializing to a Map'() {
        setup:
        InputStream entity = new ByteArrayInputStream('{"foo": "hi", "bar": true, "baz": 5}'.getBytes('utf-8'))

        expect:
        reader.supports(Map, null, null)

        when:
        def result = reader.read(Map, entity, null, null)

        then:
        result instanceof Map
        result.foo == 'hi'
        result.bar
        result.baz == 5
    }

    def 'The Jackson entity read supports de-serializing to a List'() {
        setup:
        InputStream entity = new ByteArrayInputStream('[{"foo": "hi", "bar": true, "baz": 5}]'.getBytes('utf-8'))

        expect:
        reader.supports(List, null, null)

        when:
        def result = reader.read(List, entity, null, null)

        then:
        result instanceof List
        result.size() == 1
        result[0] instanceof Map
        result[0].foo == 'hi'
        result[0].bar
        result[0].baz == 5
    }
}
