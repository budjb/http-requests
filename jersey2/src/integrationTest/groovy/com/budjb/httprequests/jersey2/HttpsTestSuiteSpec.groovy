package com.budjb.httprequests.jersey2

import com.budjb.httprequests.HttpClientFactory
import com.budjb.httprequests.HttpsIntegrationTestSuiteSpec

class HttpsTestSuiteSpec extends HttpsIntegrationTestSuiteSpec {
    /**
     * Create an HTTP client factory to use with tests.
     *
     * @return
     */
    @Override
    HttpClientFactory createHttpClientFactory() {
        return new JerseyHttpClientFactory()
    }
}
