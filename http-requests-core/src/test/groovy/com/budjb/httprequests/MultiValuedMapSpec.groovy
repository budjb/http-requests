/*
 * Copyright 2016-2019 the original author or authors.
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

import spock.lang.Specification

class MultiValuedMapSpec extends Specification {
    def 'Setting a null value to a key that does not exist in the map results in an entry with no value'() {
        setup:
        MultiValuedMap map = new MultiValuedMap();

        when:
        map.set('foo', null)

        then:
        (Map) map == [
            foo: []
        ]
    }

    def 'Adding null values to a key that does not exist in the map results in an entry with no value'() {
        setup:
        MultiValuedMap map = new MultiValuedMap();

        when:
        map.add('foo', null)

        then:
        (Map) map == [
            foo: []
        ]
    }

    def 'Adding null values to a key with an existing value in the map has no effect'() {
        setup:
        MultiValuedMap map = new MultiValuedMap();
        map.add('foo', 'bar')

        when:
        map.add('foo', null)

        then:
        (Map) map == [
            foo: [
                'bar'
            ]
        ]
    }

    def 'Setting a null value to a key with an existing value in the map results in an entry with no value'() {
        setup:
        MultiValuedMap map = new MultiValuedMap();
        map.add('foo', 'bar')

        when:
        map.set('foo', null)

        then:
        (Map) map == [
            foo: []
        ]
    }
}
