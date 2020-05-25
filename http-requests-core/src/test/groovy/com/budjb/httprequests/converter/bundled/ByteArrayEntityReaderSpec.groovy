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
import spock.lang.Specification

class ByteArrayEntityReaderSpec extends Specification {
    def 'ByteArrayEntityReader only supports byte arrays'() {
        setup:
        ByteArrayEntityReader reader = new ByteArrayEntityReader()

        expect:
        reader.supports(byte[], null, null)
        !reader.supports(String[], null, null)
        !reader.supports(Object, null, null)
        !reader.supports(String, null, null)
    }

    def 'ByteArrayEntityReader returns a byte array'() {
        setup:
        ByteArrayEntityReader reader = new ByteArrayEntityReader()
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream([1, 2, 3] as byte[])

        expect:
        reader.read(byte[].class, new HttpEntity(byteArrayInputStream, null, null)) == [1, 2, 3] as byte[]
    }
}
