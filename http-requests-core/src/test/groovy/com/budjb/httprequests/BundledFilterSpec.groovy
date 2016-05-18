/*
 * Copyright 2016 Bud Byrd
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

import com.budjb.httprequests.converter.bundled.ByteArrayEntityWriter
import com.budjb.httprequests.converter.bundled.StringEntityWriter
import com.budjb.httprequests.filter.bundled.BasicAuthFilter
import com.budjb.httprequests.filter.bundled.DeflateFilter
import com.budjb.httprequests.filter.bundled.GZIPFilter
import org.slf4j.Logger
import spock.lang.Specification

class BundledFilterSpec extends Specification {
    MockHttpClient client

    def setup() {
        client = new MockHttpClient()
        client.addEntityConverter(new StringEntityWriter())
        client.addEntityConverter(new ByteArrayEntityWriter())
    }

    def 'When the GZIP filter is used, the input is compressed and the proper header is set'() {
        setup:
        client.addFilter(new GZIPFilter())

        when:
        def response = client.post 'hi there', {
            uri = 'http://foo.bar.com'
        }

        then:
        response.request.getHeaders().get('Content-Encoding') == ['gzip']
        client.requestBuffer == [31, -117, 8, 0, 0, 0, 0, 0, 0, 0] as byte[]
    }

    def 'When the deflate filter is used, the input is compressed and the proper header is set'() {
        setup:
        client.addFilter(new DeflateFilter())

        when:
        def response = client.post 'hi there', {
            uri = 'http://foo.bar.com'
        }

        then:
        response.request.getHeaders().get('Content-Encoding') == ['deflate']
        client.requestBuffer == [120, -100] as byte[]
    }

    def 'When the basic auth filter is used, the correct header is set'() {
        setup:
        String username = 'foo'
        String password = 'bar'
        client.addFilter(new BasicAuthFilter(username, password))

        when:
        def response = client.get {
            uri = 'http://foo.bar.com'
        }

        then:
        response.request.getHeaders().get('Authorization') == ["Basic " + "$username:$password".getBytes().encodeBase64()]
    }
}
