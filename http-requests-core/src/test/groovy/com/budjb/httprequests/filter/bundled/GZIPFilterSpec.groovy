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
import com.budjb.httprequests.converter.EntityConverterManager
import com.budjb.httprequests.converter.bundled.StringEntityWriter
import com.budjb.httprequests.exception.EntityConverterException
import com.budjb.httprequests.mock.MockHttpClient
import com.budjb.httprequests.mock.MockHttpClientFactory
import spock.lang.Specification

class GZIPFilterSpec extends Specification {
    def 'When the GZIP filter is used, the input is compressed and the proper header is set'() {
        setup:
        EntityConverterManager converterManager = new EntityConverterManager()
        converterManager.add(new StringEntityWriter())
        MockHttpClient client = (MockHttpClient) new MockHttpClientFactory(converterManager).createHttpClient()
        HttpRequest request = new HttpRequest('http://foo.bar.com').addFilter(new GZIPFilter())

        when:
        def response = client.post request, 'hi there'

        then:
        response.request.getHeaders().get('Content-Encoding') == ['gzip']
        client.requestBuffer == [31, -117, 8, 0, 0, 0, 0, 0, 0, 0] as byte[]
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
