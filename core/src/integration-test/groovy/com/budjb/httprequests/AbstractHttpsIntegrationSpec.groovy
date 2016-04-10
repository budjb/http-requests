package com.budjb.httprequests

import org.springframework.boot.test.WebIntegrationTest
import spock.lang.Ignore

@Ignore
@WebIntegrationTest([
    'server.port=0',
    'server.ssl.key-store = classpath:keystore.jks',
    'server.ssl.key-password = password'
])
abstract class AbstractHttpsIntegrationSpec extends AbstractIntegrationSpec {
    /**
     * Return the base URL of the running server.
     *
     * @return
     */
    @Override
    String getBaseUrl() {
        return "https://localhost:${webPort}"
    }
}
