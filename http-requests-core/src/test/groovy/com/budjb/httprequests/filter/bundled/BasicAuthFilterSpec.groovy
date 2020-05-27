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

package com.budjb.httprequests.filter.bundled


import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.converter.EntityConverterManager
import com.budjb.httprequests.test.MockHttpClientFactory
import com.budjb.httprequests.test.MockHttpResponse
import spock.lang.Specification

class BasicAuthFilterSpec extends Specification {
    def 'When the basic auth filter is used, the correct header is set'() {
        setup:
        MockHttpClientFactory httpClientFactory = new MockHttpClientFactory(EntityConverterManager.empty)
        httpClientFactory.createMock().setRequestUri('http://foo.bar.com')

        String username = 'foo'
        String password = 'bar'

        HttpRequest request = new HttpRequest('http://foo.bar.com').addFilter(new BasicAuthFilter(username, password))

        when:
        MockHttpResponse response = (MockHttpResponse) httpClientFactory.createHttpClient().get(request)

        then:
        response.request.getHeaders().get('Authorization') == ["Basic " + "$username:$password".getBytes().encodeBase64()]
    }
}
