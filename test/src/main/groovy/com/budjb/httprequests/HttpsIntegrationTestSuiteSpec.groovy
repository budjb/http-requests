package com.budjb.httprequests

import spock.lang.Ignore

import javax.net.ssl.SSLException

@Ignore
abstract class HttpsIntegrationTestSuiteSpec extends AbstractHttpsIntegrationSpec {
    def 'Given that the keystore does not contain a localhost cert, when a request is made over SSL with cert validation enabled, an SSL exception is thrown'() {
        setup:
        HttpClient client = httpClientFactory.createHttpClient()

        when:
        client.get(new HttpRequest(uri: "${baseUrl}/test"))

        then:
        thrown SSLException
    }

    def 'Given that the keystore does not contain a localhost cert, when a request is made over SSL with cert validation disabled, no SSL exception is thrown'() {
        setup:
        HttpClient client = httpClientFactory.createHttpClient()

        when:
        client.get(new HttpRequest(uri: "${baseUrl}/test", sslValidated: false))

        then:
        notThrown SSLException
    }
}
