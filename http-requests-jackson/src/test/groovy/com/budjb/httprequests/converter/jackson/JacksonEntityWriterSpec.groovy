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

class JacksonEntityWriterSpec extends Specification {
    ObjectMapper objectMapper
    JacksonEntityWriter writer

    def setup() {
        objectMapper = new ObjectMapper()
        writer = new JacksonEntityWriter(objectMapper)
    }

    String readInputStream(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
        byte[] buffer = new byte[8192]
        int read

        while ((read = inputStream.read(buffer, 0, buffer.size())) != -1) {
            byteArrayOutputStream.write(buffer, 0, read)
        }

        return byteArrayOutputStream.toString('utf-8')
    }

    def 'The Jackson entity writer supports serializing from a POGO'() {
        setup:
        Foo foo = new Foo()
        foo.foo = 'hi'
        foo.bar = true
        foo.baz = 5

        expect:
        writer.supports(Foo)

        when:
        def result = readInputStream(writer.write(foo, null))

        then:
        result == '{"foo":"hi","bar":true,"baz":5}'
    }

    def 'The Jackson entity writer supports serializing from a Map'() {
        setup:
        Map foo = [
            foo: 'hi',
            bar: true,
            baz: 5
        ]

        expect:
        writer.supports(Map)

        when:
        def result = readInputStream(writer.write(foo, null))

        then:
        result == '{"foo":"hi","bar":true,"baz":5}'
    }

    def 'The Jackson entity writer supports serializing from a List'() {
        setup:
        List foo = [
            [
                foo: 'hi',
                bar: true,
                baz: 5
            ]
        ]

        expect:
        writer.supports(List)

        when:
        def result = readInputStream(writer.write(foo, null))

        then:
        result == '[{"foo":"hi","bar":true,"baz":5}]'
    }
}
