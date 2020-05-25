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

package com.budjb.httprequests.converter.bundled

import com.budjb.httprequests.HttpEntity
import com.budjb.httprequests.StreamUtils
import spock.lang.Specification

class StringEntityWriterSpec extends Specification {
    def 'StringEntityWriter only supports byte arrays'() {
        setup:
        StringEntityWriter writer = new StringEntityWriter()

        expect:
        writer.supports(String, null, null)
        !writer.supports(String[], null, null)
        !writer.supports(Object, null, null)
    }

    def 'StringEntityWriter creates an input stream from a String'() {
        setup:
        StringEntityWriter writer = new StringEntityWriter()

        when:
        HttpEntity entity = writer.write("foobar", null, null)

        then:
        StreamUtils.readBytes(entity.getInputStream()) == [102, 111, 111, 98, 97, 114] as byte[]
    }

    def 'StringEntityWriter has a default content type of text/plain'() {
        setup:
        StringEntityWriter writer = new StringEntityWriter()

        when:
        HttpEntity entity = writer.write("foobar", null, null)

        then:
        entity.getContentType() == 'text/plain'
    }
}
