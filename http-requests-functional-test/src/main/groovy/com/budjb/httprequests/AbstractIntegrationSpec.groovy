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

import com.budjb.httprequests.converter.EntityConverterManager
import com.budjb.httprequests.converter.bundled.*
import com.budjb.httprequests.filter.jackson.JacksonListReader
import com.budjb.httprequests.filter.jackson.JacksonListWriter
import com.budjb.httprequests.filter.jackson.JacksonMapReader
import com.budjb.httprequests.filter.jackson.JacksonMapWriter
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import spock.lang.Ignore
import spock.lang.Specification

@Ignore
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
    abstract HttpClientFactory createHttpClientFactory(EntityConverterManager converterManager)

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
        ObjectMapper objectMapper = new ObjectMapper()

        EntityConverterManager converterManager = new EntityConverterManager([
            new StringEntityReader(),
            new StringEntityWriter(),
            new ByteArrayEntityWriter(),
            new ByteArrayEntityReader(),
            new JacksonMapReader(objectMapper),
            new JacksonListReader(objectMapper),
            new JacksonMapWriter(objectMapper),
            new JacksonListWriter(objectMapper),
            new FormDataEntityWriter()
        ])

        httpClientFactory = createHttpClientFactory(converterManager)
    }
}
