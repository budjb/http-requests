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

package com.budjb.httprequests.filter.bundled

import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.exception.EntityConverterException
import spock.lang.Specification

class GZIPFilterSpec extends Specification {
    def 'When the GZIP filter is used, the input is compressed and the proper header is set'() {
        setup:
        HttpRequest request = new HttpRequest()

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
        GZIPFilter filter = new GZIPFilter()
        OutputStream filteredStream = filter.filter(byteArrayOutputStream)

        when:
        filteredStream.write('hi there'.bytes)

        then:
        byteArrayOutputStream.toByteArray() == [31, -117, 8, 0, 0, 0, 0, 0, 0, 0] as byte[]

        when:
        filter.filter(request)

        then:
        request.getHeaders().get('Content-Encoding') == ['gzip']
    }

    def 'When an IOException occurs, it is wrapped in an EntityConverterException'() {
        setup:
        GZIPFilter filter = new GZIPFilter()

        OutputStream outputStream = new OutputStream() {
            @Override
            void write(int b) throws IOException {
                throw new IOException("foo")
            }
        }

        when:
        filter.filter(outputStream)

        then:
        EntityConverterException exception = thrown EntityConverterException
        exception.cause.message == 'foo'
    }
}
