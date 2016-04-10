package com.budjb.httprequests

import com.budjb.httprequests.application.TestApp
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest
import spock.lang.Ignore
import spock.lang.Specification

@WebIntegrationTest('server.port:0')
@SpringApplicationConfiguration(TestApp)
@Ignore
abstract class BaseIntegrationSpec extends Specification {
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

    def 'make sure it works!'() {
        expect:
        httpClientFactory.createHttpClient().get(new HttpRequest(uri: "${baseUrl}/test")).entityAsString == '{"foo":"bar"}'
    }
}
