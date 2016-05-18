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

import com.budjb.httprequests.converter.bundled.StringEntityReader
import com.budjb.httprequests.filter.bundled.Slf4jLoggingFilter
import org.slf4j.Logger
import spock.lang.Specification

class Slf4jLoggingFilterSpec extends Specification {
    Slf4jLoggingFilter filter
    Logger log
    MockHttpClient client

    def setup() {
        log = Mock(Logger)
        log.isInfoEnabled() >> true
        log.isTraceEnabled() >> true

        filter = new Slf4jLoggingFilter()
        filter.setLogger(log)

        client = new MockHttpClient()
        client.addFilter(filter)
        client.addEntityConverter(new StringEntityReader())
    }

    def 'When a response has no entity, the entity is not logged'() {
        setup:
        client.responseInputStream = null
        client.status = 200

        when:
        def response = client.get { uri = "https://example.com" }

        then:
        response.status == 200
        1 * log.trace('Sending HTTP client request with the following data:\n> GET https://example.com\n\n' +
            'Received HTTP server response with the following data:\n< 200\n')
    }

    def 'When a response has an empty entity, the entity is not logged'() {
        setup:
        client.responseInputStream = new ByteArrayInputStream([] as byte[])
        client.status = 200

        when:
        def response = client.get { uri = "https://example.com" }

        then:
        response.status == 200
        1 * log.trace('Sending HTTP client request with the following data:\n> GET https://example.com\n\n' +
            'Received HTTP server response with the following data:\n< 200\n')
    }

    def 'When a response has a non-empty entity, the entity is logged'() {
        setup:
        InputStream responseInputStream = new EntityInputStream(new ByteArrayInputStream('response stuff'.bytes))
        client.responseInputStream = responseInputStream
        client.status = 200

        when:
        def response = client.get { uri = "https://example.com" }

        then:
        response.status == 200
        responseInputStream.isClosed()
        1 * log.trace('Sending HTTP client request with the following data:\n> GET https://example.com\n\n' +
            'Received HTTP server response with the following data:\n< 200\nresponse stuff\n')
    }

    def 'When the logger is configured for level INFO, the content is logged appropriately'() {
        setup:
        client.responseInputStream = new ByteArrayInputStream([] as byte[])
        client.status = 200
        filter.setLoggerLevel(Slf4jLoggingFilter.LoggerLevel.INFO)

        when:
        def response = client.get { uri = "https://example.com" }

        then:
        response.status == 200
        1 * log.info('Sending HTTP client request with the following data:\n> GET https://example.com\n\n' +
            'Received HTTP server response with the following data:\n< 200\n')
    }

    def 'When a response has a non-empty entity that is not buffered, the entity is logged but the response input stream is not closed'() {
        setup:
        String content = '0123456789' * 1100
        InputStream responseInputStream = new EntityInputStream(new ByteArrayInputStream(content.bytes))
        client.responseInputStream = responseInputStream
        client.status = 200

        when:
        def response = client.get {
            uri = "https://example.com"
            bufferResponseEntity = false
        }

        then:
        response.status == 200
        !responseInputStream.isClosed()
        response.getEntity().getClass() == EntityInputStream
        response.getEntity().source.getClass() == PushbackInputStream
        response.getEntity().source.in.getClass() == BufferedInputStream
        response.getEntity().source.in.count == 10001
        response.getEntity(String) == content
        !response.isEntityBuffered()
    }
}
