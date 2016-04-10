package com.budjb.httprequests.jersey1

import com.budjb.httprequests.BaseIntegrationSpec
import com.budjb.httprequests.HttpClientFactory

class Jersey1Spec extends BaseIntegrationSpec {
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
