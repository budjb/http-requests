package com.budjb.httprequests

import spock.lang.Ignore

@Ignore
abstract class HttpIntegrationTestSuiteSpec extends AbstractIntegrationSpec {
    def 'make sure it works!'() {
        expect:
        httpClientFactory.createHttpClient().get(new HttpRequest(uri: "${baseUrl}/test")).entityAsString == '{"foo":"bar"}'
    }
}
