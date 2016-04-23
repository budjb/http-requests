package com.budjb.httprequests

import com.budjb.httprequests.converter.ConverterManager
import com.budjb.httprequests.converter.StringEntityReader
import spock.lang.Specification
import spock.lang.Unroll

class HttpResponseSpec extends Specification {
    def 'When a charset is provided, the resulting string is built using it'() {
        setup:
        ConverterManager converterManager = new ConverterManager()
        converterManager.add(new StringEntityReader())

        HttpResponse response = new HttpResponse()
        response.converterManager = converterManager
        response.entity = 'åäö'.getBytes()
        response.charset = 'euc-jp'

        when:
        String entity = response.getEntity(String)

        then:
        entity == '奪辰旦'
    }

    def 'When no charset is provided, UTF-8 is used'() {
        setup:
        ConverterManager converterManager = new ConverterManager()
        converterManager.add(new StringEntityReader())

        HttpResponse response = new HttpResponse()
        response.converterManager = converterManager
        response.entity = 'åäö'.getBytes()

        when:
        String entity = response.getEntity(String)

        then:
        response.charset == 'UTF-8'
        entity == 'åäö'
    }

    @Unroll
    def 'When a #type charset is assigned, charset is not actually assigned'() {
        setup:
        HttpResponse response = new HttpResponse()

        when:
        response.charset = charset

        then:
        response.charset == 'UTF-8'

        where:
        type    | charset
        'null'  | null
        'blank' | ''
    }

    def 'When a null character set is assigned, the existing value is not overwritten'() {
        setup:
        def response = new HttpResponse()
        response.setCharset('ISO-8859-1')

        when:
        response.setCharset(null)

        then:
        response.charset == 'ISO-8859-1'
    }

    def 'Verify header parsing and retrieval'() {
        setup:
        def response = new HttpResponse()

        when:
        response.headers = [
            foo: ['bar', 'baz'],
            hi: ['there'],
            peek: 'boo'
        ]

        then:
        response.getHeaders() == [
            foo: ['bar', 'baz'],
            hi: ['there'],
            peek: ['boo']
        ]
        response.getFlattenedHeaders() == [
            foo: ['bar', 'baz'],
            hi: 'there',
            peek: 'boo'
        ]
        response.getHeaders('foo') == ['bar', 'baz']
        response.getHeaders('hi') == ['there']
        response.getHeaders('peek') == ['boo']
        response.getHeader('foo') == 'bar'
        response.getHeader('hi') == 'there'
        response.getHeader('peek') == 'boo'
    }

    def 'When the entity is retrieved from a streaming response, the input stream is closed'() {
        setup:
        ConverterManager converterManager = new ConverterManager()
        converterManager.add(new StringEntityReader())

        def entity = 'Hello, world!'
        def inputStream = new ByteArrayInputStream(entity.getBytes())
        def response = new HttpResponse()
        response.converterManager = converterManager
        response.request = HttpRequest.build { autoBufferEntity = false }
        response.inputStream = inputStream

        when:
        def body = response.getEntity(String)

        then:
        body == entity

        expect:
        response.inputStream.read() == -1
    }
}
