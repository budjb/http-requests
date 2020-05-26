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

class StringEntityReaderSpec extends Specification {
    def 'StringEntityReader only supports Strings'() {
        setup:
        StringEntityReader reader = new StringEntityReader()

        expect:
        reader.supports(String, null, null)
        !reader.supports(String[], null, null)
        !reader.supports(Object, null, null)
    }

    def 'StringEntityReader returns a String'() {
        setup:
        StringEntityReader reader = new StringEntityReader()
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream([102, 111, 111, 98, 97, 114] as byte[])

        expect:
        reader.read(String, new HttpEntity(byteArrayInputStream, null, null)) == 'foobar'
    }
}
