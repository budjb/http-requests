import com.budjb.httprequests.HttpRequest
import spock.lang.Specification

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

class HttpClientStaticGroovyExtensionsSpec extends Specification {
    def 'The groovy HttpRequest DSL parses properties correctly'() {
        when:
        HttpRequest request = HttpRequest.build {
            uri 'https://foo.bar.com'
            header 'foo', 'bar'
            queryParameter 'foo', 'bar'
        }

        then:
        request.uri == 'https://foo.bar.com'
        request.headers == ['foo': ['bar']]
        request.queryParameters == ['foo': ['bar']]
    }
}
