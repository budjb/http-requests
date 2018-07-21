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

import com.budjb.httprequests.FormData
import com.budjb.httprequests.StreamUtils
import spock.lang.Specification

class FormDataEntityWriterSpec extends Specification {
    def 'FormDataEntityWriter only supports FormData'() {
        setup:
        FormDataEntityWriter writer = new FormDataEntityWriter()

        expect:
        writer.supports(FormData)
        !writer.supports(String[])
        !writer.supports(Object)
        !writer.supports(String)
    }

    def 'FormDataEntityWriter creates an input stream from a FormData object'() {
        setup:
        FormDataEntityWriter writer = new FormDataEntityWriter()
        FormData formData = new FormData().addField('foo', 'bar').addField('foo', 'baz')

        when:
        InputStream inputStream = writer.write(formData, null)

        then:
        new String(StreamUtils.readBytes(inputStream)) == 'foo=bar&foo=baz'
    }

    def 'FormDataEntityWriter has a default content type of application/x-www-form-urlencoded'() {
        expect:
        new FormDataEntityWriter().contentType == 'application/x-www-form-urlencoded'
    }
}
