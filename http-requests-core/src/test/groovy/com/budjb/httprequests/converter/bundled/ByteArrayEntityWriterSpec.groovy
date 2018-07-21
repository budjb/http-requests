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

import com.budjb.httprequests.StreamUtils
import spock.lang.Specification

class ByteArrayEntityWriterSpec extends Specification {
    def 'ByteArrayEntityWriter only supports byte arrays'() {
        setup:
        ByteArrayEntityWriter writer = new ByteArrayEntityWriter()

        expect:
        writer.supports(byte[])
        !writer.supports(String[])
        !writer.supports(Object)
        !writer.supports(String)
    }

    def 'ByteArrayEntityWriter creates an input stream from a byte array'() {
        setup:
        ByteArrayEntityWriter writer = new ByteArrayEntityWriter()

        when:
        InputStream inputStream = writer.write([1, 2, 3] as byte[], null)

        then:
        StreamUtils.readBytes(inputStream) == [1, 2, 3] as byte[]
    }

    def 'ByteArrayEntityWriter has a default content type of application/octet-stream'() {
        expect:
        new ByteArrayEntityWriter().contentType == 'application/octet-stream'
    }
}
