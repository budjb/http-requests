/*
 * Copyright 2016 Bud Byrd
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

class FormDataSpec extends Specification {
    def 'Verify functionality to add fields'() {
        setup:
        def data = new FormData()

        when:
        data.addField('foo', 'bar')
        data.addField('hi', 'there')

        then:
        data.getFields() == [foo: ['bar'], hi: ['there']]
        data.getFlattenedFields() == [foo: 'bar', hi: 'there']

        when:
        data.addField('foo', ['baz', 'bat'])

        then:
        data.getFields() == [foo: ['bar', 'baz', 'bat'], hi: ['there']]
        data.getFlattenedFields() == [foo: ['bar', 'baz', 'bat'], hi: 'there']

        when:
        data.addFields([meh: 'moo', blah: ['bleh'], foo: 'bas'])

        then:
        data.getFields() == [foo: ['bar', 'baz', 'bat', 'bas'], hi: ['there'], meh: ['moo'], blah: ['bleh']]
        data.getFlattenedFields() == [foo: ['bar', 'baz', 'bat', 'bas'], hi: 'there', meh: 'moo', blah: 'bleh']
    }
}
