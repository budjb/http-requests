package com.budjb.httprequests

import com.budjb.httprequests.exception.HttpFoundException
import com.budjb.httprequests.exception.HttpInternalServerErrorException
import com.budjb.httprequests.exception.HttpNotAcceptableException
import spock.lang.Ignore

@Ignore
abstract class HttpIntegrationTestSuiteSpec extends AbstractIntegrationSpec {
    def 'Test simple usage of the GET method'() {
        when:
        def response = httpClientFactory.createHttpClient().get(new HttpRequest(uri: "${baseUrl}/testBasicGet"))

        then:
        response.getEntityAsString() == 'The quick brown fox jumps over the lazy dog.'
    }

    def 'Test simple usage of the DELETE method'() {
        when:
        def response = httpClientFactory.createHttpClient().delete(new HttpRequest(uri: "${baseUrl}/testBasicDelete"))

        then:
        response.entityAsString == "Please don't hurt me!"
    }

    def 'Test simple usage of the POST method'() {
        when:
        def response = httpClientFactory.createHttpClient().post(
            new HttpRequest(uri: "${baseUrl}/testBasicPost"),
            "Please don't play the repeating game!"
        )

        then:
        response.entityAsString == "Please don't play the repeating game!"
    }

    def 'Test simple usage of the PUT method'() {
        when:
        def response = httpClientFactory.createHttpClient().put(
            new HttpRequest(uri: "${baseUrl}/testBasicPut"),
            "Please don't play the repeating game!"
        )

        then:
        response.entityAsString == "Please don't play the repeating game!"
    }

    def 'Tests that the accept header is received and respected by the server'() {
        when:
        def response = httpClientFactory.createHttpClient().get(new HttpRequest(
            uri: "${baseUrl}/testAccept",
            accept: 'text/plain'
        ))

        then:
        response.entityAsString == 'I am plain text.'
    }

    def 'Tests that an unacceptable accept type is handled and the correct exception thrown'() {
        when:
        httpClientFactory.createHttpClient().get(new HttpRequest(
            uri: "${baseUrl}/testAccept",
            accept: 'foo/bar'
        ))

        then:
        thrown HttpNotAcceptableException
    }

    def 'Tests the read timeout handling'() {
        when:
        httpClientFactory.createHttpClient().get(new HttpRequest(
            uri: "${baseUrl}/testReadTimeout",
            readTimeout: 1000
        ))

        then:
        thrown SocketTimeoutException
    }

    def 'Tests the binary entity of the response'() {
        when:
        def response = httpClientFactory.createHttpClient().get(new HttpRequest(uri: "${baseUrl}/testBasicGet"))

        then:
        response.entity == [84, 104, 101, 32, 113, 117, 105, 99, 107, 32, 98, 114, 111, 119, 110, 32, 102, 111, 120, 32, 106, 117, 109, 112, 115, 32, 111, 118, 101, 114, 32, 116, 104, 101, 32, 108, 97, 122, 121, 32, 100, 111, 103, 46] as byte[]
    }

    def 'Tests the output of a redirect'() {
        when:
        def response = httpClientFactory.createHttpClient().get(new HttpRequest(
            uri: "${baseUrl}/testRedirect",
        ))

        then:

        response.entityAsString == 'The quick brown fox jumps over the lazy dog.'
    }

    def 'Tests that the appropriate exception is thrown on a redirect when redirects are disabled'() {
        when:
        httpClientFactory.createHttpClient().get(new HttpRequest(
            uri: "${baseUrl}/testRedirect",
            followRedirects: false
        ))

        then:
        thrown HttpFoundException
    }

    def 'Test the headers parameter'() {
        when:
        def response = httpClientFactory.createHttpClient().get(new HttpRequest(
            uri: "${baseUrl}/testHeaders",
            headers: [foo: ['bar'], key: ['value']]
        ))

        then:
        def json = response.entityAsJson
        json.foo == ['bar']
        json.key == ['value']
    }

    def 'Test the headers parameter when multivalued entries are present'() {
        setup:
        def request = new HttpRequest()
            .setUri("${baseUrl}/testHeaders")
            .addHeader('foo', 'bar')
            .addHeader('foo', 'baz')
            .addHeader('hi', 'there')

        when:
        def response = httpClientFactory.createHttpClient().get(request)

        then:
        def json = response.entityAsJson
        json.foo == ['bar,baz']
        json.hi == ['there']
    }

    def 'Test the query parameter'() {
        setup:
        def request = new HttpRequest()
            .setUri("${baseUrl}/testParams")
            .addQueryParameter('foo', 'bar')
            .addQueryParameter('key', 'value')

        when:
        def response = httpClientFactory.createHttpClient().get(request)

        then:
        response.entityAsJson == [foo: ['bar'], key: ['value']]
    }

    def 'Test the query parameters when multivalued entries are present'() {
        setup:
        def request = new HttpRequest()
            .setUri("${baseUrl}/testParams")
            .addQueryParameter('foo', 'bar')
            .addQueryParameter('foo', 'baz')
            .addQueryParameter('hi', 'there')

        when:
        def response = httpClientFactory.createHttpClient().get(request)

        then:
        response.entityAsJson == ['foo': ['bar', 'baz'], 'hi': ['there']]
    }

    def 'Test that a proper exception is thrown when throwStatusExceptions = true'() {
        when:
        httpClientFactory.createHttpClient().get(new HttpRequest(uri: "${baseUrl}/test500"))

        then:
        thrown HttpInternalServerErrorException
    }

    def 'Test that no exception is thrown when throwStatusExceptions = false'() {
        when:
        def response = httpClientFactory.createHttpClient().get(new HttpRequest(
            uri: "${baseUrl}/test500",
            throwStatusExceptions: false
        ))

        then:
        notThrown HttpInternalServerErrorException
        response.status == 500
    }

    def 'Tests the form parameter of the request builder'() {
        setup:
        FormData formData = new FormData()
        formData.addField('foo', 'bar')
        formData.addField('key', 'value')


        when:
        def response = httpClientFactory.createHttpClient().post(new HttpRequest(uri: "${baseUrl}/testForm"), formData)

        then:
        response.entityAsJson == ['foo': ['bar'], 'key': ['value']]
    }

    def 'Tests the form parameter when it has multivalued entries'() {
        setup:
        FormData formData = new FormData()
        formData.addField('foo', 'bar')
        formData.addField('foo', 'baz')
        formData.addField('key', 'value')


        when:
        def response = httpClientFactory.createHttpClient().post(new HttpRequest(uri: "${baseUrl}/testForm"), formData)

        then:
        response.entityAsJson == ['foo': ['bar', 'baz'], 'key': ['value']]
    }

    /*
    def 'Test basic auth functionality'() {
        when:
        httpClientFactory.createHttpClient().get(new HttpRequest(uri: "${baseUrl}/testAuth"))

        then:
        thrown HttpUnauthorizedException

        when:
        def response = httpClientFactory.createHttpClient().get(new HttpRequest(
            uri: "${baseUrl}/testAuth",
            useBasicAuth: true,
            basicAuthUserName: 'foo',
            basicAuthPassword: 'bar'
        ))

        then:
        response.entityAsString == 'welcome'
    }
    */
}
