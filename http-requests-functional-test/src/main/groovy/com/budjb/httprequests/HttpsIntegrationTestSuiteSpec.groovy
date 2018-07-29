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
package com.budjb.httprequests

import com.budjb.httprequests.application.TestApp
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Ignore

import javax.net.ssl.SSLException

@Ignore
@SpringBootTest(classes = TestApp, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = [
    'server.ssl.key-store=classpath:keystore.jks',
    'server.ssl.key-password=password',
    'spring.mvc.dispatch-trace-request=true'
])
abstract class HttpsIntegrationTestSuiteSpec extends AbstractHttpsIntegrationSpec {
    def 'Given that the keystore does not contain a localhost cert, when a request is made over SSL with cert validation enabled, an SSL exception is thrown'() {
        setup:
        HttpClient client = httpClientFactory.createHttpClient()

        when:
        client.get(new HttpRequest().setUri("${baseUrl}/test"))

        then:
        thrown SSLException
    }

    def 'Given that the keystore does not contain a localhost cert, when a request is made over SSL with cert validation disabled, no SSL exception is thrown'() {
        setup:
        HttpClient client = httpClientFactory.createHttpClient()

        when:
        client.get(new HttpRequest().setUri("${baseUrl}/testBasicGet").setSslValidated(false))

        then:
        notThrown SSLException
    }
}
