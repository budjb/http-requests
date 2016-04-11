package com.budjb.httprequests

import com.budjb.httprequests.exception.HttpFoundException
import com.budjb.httprequests.exception.HttpInternalServerErrorException
import com.budjb.httprequests.exception.HttpNotAcceptableException
import com.budjb.httprequests.exception.HttpUnauthorizedException
import com.budjb.httprequests.listener.BasicAuthListener
import com.budjb.httprequests.listener.HttpClientListener
import com.budjb.httprequests.listener.HttpClientRetryListener
import spock.lang.Ignore

@Ignore
abstract class HttpIntegrationTestSuiteSpec extends AbstractIntegrationSpec {
    def 'When a GET request is made to /testBasicGet, the proper response is received'() {
        when:
        def response = httpClientFactory.createHttpClient().get(new HttpRequest(uri: "${baseUrl}/testBasicGet"))

        then:
        response.getEntityAsString() == 'The quick brown fox jumps over the lazy dog.'
    }

    def 'When a DELETE request is made to /testBasicDelete, the proper response is received'() {
        when:
        def response = httpClientFactory.createHttpClient().delete(new HttpRequest(uri: "${baseUrl}/testBasicDelete"))

        then:
        response.entityAsString == "Please don't hurt me!"
    }

    def 'When a POST request is made to /testBasicPost, the proper response is received'() {
        when:
        def response = httpClientFactory.createHttpClient().post(
            new HttpRequest(uri: "${baseUrl}/testBasicPost"),
            "Please don't play the repeating game!"
        )

        then:
        response.entityAsString == "Please don't play the repeating game!"
    }

    def 'When a PUT request is made to /testBasicPut, the proper response is received'() {
        when:
        def response = httpClientFactory.createHttpClient().put(
            new HttpRequest(uri: "${baseUrl}/testBasicPut"),
            "Please don't play the repeating game!"
        )

        then:
        response.entityAsString == "Please don't play the repeating game!"
    }

    def 'When an Accept header is assigned, the server receives and processes it correctly'() {
        when:
        def response = httpClientFactory.createHttpClient().get(new HttpRequest(
            uri: "${baseUrl}/testAccept",
            accept: 'text/plain'
        ))

        then:
        response.entityAsString == 'I am plain text.'
    }

    def 'When an unknown Accept header is assigned, the server receives it and returns an error'() {
        when:
        httpClientFactory.createHttpClient().get(new HttpRequest(
            uri: "${baseUrl}/testAccept",
            accept: 'foo/bar'
        ))

        then:
        thrown HttpNotAcceptableException
    }

    def 'When a read timeout is reached, a SocketTimeoutException occurs'() {
        when:
        httpClientFactory.createHttpClient().get(new HttpRequest(
            uri: "${baseUrl}/testReadTimeout",
            readTimeout: 1000
        ))

        then:
        thrown SocketTimeoutException
    }

    def 'When a call to /testBasicGet is made, the proper byte stream is received'() {
        when:
        def response = httpClientFactory.createHttpClient().get(new HttpRequest(uri: "${baseUrl}/testBasicGet"))

        then:
        response.entity == [84, 104, 101, 32, 113, 117, 105, 99, 107, 32, 98, 114, 111, 119, 110, 32, 102, 111, 120, 32, 106, 117, 109, 112, 115, 32, 111, 118, 101, 114, 32, 116, 104, 101, 32, 108, 97, 122, 121, 32, 100, 111, 103, 46] as byte[]
    }

    def 'When a redirect is received and the client is configured to follow it, the proper response is received'() {
        when:
        def response = httpClientFactory.createHttpClient().get(new HttpRequest(
            uri: "${baseUrl}/testRedirect",
        ))

        then:

        response.entityAsString == 'The quick brown fox jumps over the lazy dog.'
    }

    def 'When a redirect is received and the client is configured to not follow it, an HttpFoundException is thrown'() {
        when:
        httpClientFactory.createHttpClient().get(new HttpRequest(
            uri: "${baseUrl}/testRedirect",
            followRedirects: false
        ))

        then:
        thrown HttpFoundException
    }

    def 'When a request includes headers, the server receives them correctly'() {
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

    def 'When a request includes headers with multiple values, the server receives them correctly'() {
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

    def 'When a request includes query parameters, the server receives them correctly'() {
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

    def 'When a request includes query parameters with multiple values, the server receives them correctly'() {
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

    def 'When a response has a status of 500, an HttpInternalServerErrorException is thrown'() {
        when:
        httpClientFactory.createHttpClient().get(new HttpRequest(uri: "${baseUrl}/test500"))

        then:
        thrown HttpInternalServerErrorException
    }

    def 'When a response has a status of 500 but the client is configured to not throw exceptions, no exception is thrown'() {
        when:
        def response = httpClientFactory.createHttpClient().get(new HttpRequest(
            uri: "${baseUrl}/test500",
            throwStatusExceptions: false
        ))

        then:
        notThrown HttpInternalServerErrorException
        response.status == 500
    }

    def 'When the client sends form data as the request entity, the server receives them correctly'() {
        setup:
        FormData formData = new FormData()
        formData.addField('foo', 'bar')
        formData.addField('key', 'value')


        when:
        def response = httpClientFactory.createHttpClient().post(new HttpRequest(uri: "${baseUrl}/testForm"), formData)

        then:
        response.entityAsJson == ['foo': ['bar'], 'key': ['value']]
    }

    def 'When the client sends form data with multiple values as the request entity, the server receives them correctly'() {
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

    def 'When a server requires basic authentication but none is provided, an HttpUnauthorizedException is thrown'() {
        when:
        httpClientFactory.createHttpClient().get(new HttpRequest().setUri("${baseUrl}/testAuth"))

        then:
        thrown HttpUnauthorizedException
    }

    def 'When a server requires basic authentication and the client provides it, the proper response is received'() {
        when:
        def response = httpClientFactory
            .createHttpClient()
            .addListener(new BasicAuthListener('foo', 'bar'))
            .get(new HttpRequest().setUri("${baseUrl}/testAuth"))

        then:
        response.entityAsString == 'welcome'
    }

    def 'If a retry listener requests a retry, ensure its proper operations'() {
        setup:
        HttpClientRetryListener listener = new HttpClientRetryListener() {
            @Override
            boolean shouldRetry(HttpRequest request, HttpResponse response, int retries) {
                return retries == 0
            }

            @Override
            void onRetry(HttpRequest request, HttpResponse response) {
                request.setHeader('foo', 'bar')
            }
        }

        when:
        def response = httpClientFactory.createHttpClient().addListener(listener).get(new HttpRequest(uri: "${baseUrl}/testHeaders"))

        then:
        response.entityAsJson.foo == ['bar']
    }

    def 'If a retry listener requests a retry and another does, not, ensure the non-requester is not called'() {
        setup:
        HttpClientRetryListener listener1 = new HttpClientRetryListener() {
            @Override
            boolean shouldRetry(HttpRequest request, HttpResponse response, int retries) {
                return retries == 0
            }

            @Override
            void onRetry(HttpRequest request, HttpResponse response) {
                request.setHeader('foo', 'bar')
            }
        }

        HttpClientListener listener2 = new HttpClientRetryListener() {
            @Override
            boolean shouldRetry(HttpRequest request, HttpResponse response, int retries) {
                return false
            }

            @Override
            void onRetry(HttpRequest request, HttpResponse response) {
                request.setHeader('hi', 'there')
            }
        }

        when:
        def response = httpClientFactory
            .createHttpClient()
            .addListener(listener1)
            .addListener(listener2)
            .get(new HttpRequest(uri: "${baseUrl}/testHeaders"))

        then:
        response.entityAsJson.foo == ['bar']
        !response.entityAsJson.hi
    }
}
