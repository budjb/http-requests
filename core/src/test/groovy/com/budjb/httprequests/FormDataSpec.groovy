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
