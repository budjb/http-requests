package com.budjb.httprequests

import spock.lang.Specification
import spock.lang.Unroll

class HttpRequestSpec extends Specification {
    @Unroll
    def 'When an HttpRequest is build with URI #raw, the properties of the URI are parsed correctly'() {
        setup:
        URI uri = new URI(raw)

        when:
        HttpRequest request = new HttpRequest(uri)

        then:
        request.getUri() == parsed
        request.getQueryParameters() == query

        where:
        raw                                         | parsed                              | query
        'https://budjb.com/path/to/nothing?foo=bar' | 'https://budjb.com/path/to/nothing' | [foo: ['bar']]
        'https://host/path?f'                       | 'https://host/path'                 | [f: ['']]
        'https://host?f=1&f=2&b=3&b=4'              | 'https://host'                      | [f: ['1', '2'], b: ['3', '4']]
        'http://host'                               | 'http://host'                       | [:]
        'http://foo.bar.com:8080'                   | 'http://foo.bar.com:8080'           | [:]
        'https://foo.bar.com:993'                   | 'https://foo.bar.com:993'           | [:]
        'https://host?f=b=a=r'                      | 'https://host'                      | [f: ['b=a=r']]
    }

    def 'When the builder syntax is used, all properties are set correctly'() {
        setup:
        HttpRequest request = new HttpRequest()

        when:
        request.setCharset('ISO-8859-8')
            .setAccept('text/plain')
            .setContentType('application/json')
            .addHeader('foo', 'bar')
            .addHeaders('foo', ['1', '2'])
            .addHeaders([hi: ['there']])
            .addQueryParameter('foo', 'bar')
            .addQueryParameter('foo', ['1', '2'])
            .addQueryParameters([hi: ['there']])
            .setSslValidated(false)
            .setThrowStatusExceptions(false)

        then:
        request.getCharset() == 'ISO-8859-8'
        request.getAccept() == 'text/plain'
        request.getContentType() == 'application/json'
        request.getHeaders() == [foo: ['bar', '1', '2'], hi: ['there']]
        request.getQueryParameters() == [foo: ['bar', '1', '2'], hi: ['there']]
        !request.isSslValidated()
        !request.isThrowStatusExceptions()
    }
}
