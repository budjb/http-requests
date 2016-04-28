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

import com.budjb.httprequests.application.TestApp
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest
import spock.lang.Ignore
import spock.lang.Specification

@Ignore
@WebIntegrationTest(['spring.mvc.dispatch-trace-request=true'])
@SpringApplicationConfiguration(TestApp)
abstract class AbstractIntegrationSpec extends Specification {
    /**
     * HTTP client factory to use with tests.
     */
    HttpClientFactory httpClientFactory

    /**
     * The port the spring boot application is running on.
     */
    @Value('${local.server.port}')
    int webPort

    /**
     * Create an HTTP client factory to use with tests.
     *
     * @return
     */
    abstract HttpClientFactory createHttpClientFactory()

    /**
     * Return the base URL of the running server.
     *
     * @return
     */
    String getBaseUrl() {
        return "http://localhost:${webPort}"
    }

    /**
     * Set up the environment for each test.
     *
     * @return
     */
    def setup() {
        httpClientFactory = createHttpClientFactory()
    }
}
