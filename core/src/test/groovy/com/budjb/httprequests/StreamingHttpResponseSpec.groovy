package com.budjb.httprequests

import spock.lang.Specification

class StreamingHttpResponseSpec extends Specification {
    def 'When the entity is retrieved from a streaming response, the input stream is closed'() {
        setup:
        def entity = 'Hello, world!'
        def inputStream = new ByteArrayInputStream(entity.getBytes())
        def response = new StreamingHttpResponse()
        response.inputStream = inputStream

        when:
        def body = response.entityAsString

        then:
        body == entity

        expect:
        response.inputStream.read() == -1
    }
}
