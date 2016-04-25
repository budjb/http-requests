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
        def response = httpClientFactory.createHttpClient().get(new HttpRequest().setUri("${baseUrl}/testBasicGet").setLogConversation(true))

        then:
        response.getEntity(String) == 'The quick brown fox jumps over the lazy dog.'
    }

    def 'When a DELETE request is made to /testBasicDelete, the proper response is received'() {
        when:
        def response = httpClientFactory.createHttpClient().delete(new HttpRequest().setUri("${baseUrl}/testBasicDelete"))

        then:
        response.getEntity(String) == "Please don't hurt me!"
    }

    def 'When a POST request is made to /testBasicPost, the proper response is received'() {
        when:
        def response = httpClientFactory.createHttpClient().post(
            new HttpRequest().setUri("${baseUrl}/testBasicPost").setContentType('text/plain'),
            "Please don't play the repeating game!"
        )

        then:
        response.getEntity(String) == "Please don't play the repeating game!"
    }

    def 'When a PUT request is made to /testBasicPut, the proper response is received'() {
        when:
        def response = httpClientFactory.createHttpClient().put(
            new HttpRequest().setUri("${baseUrl}/testBasicPut").setContentType('text/plain'),
            "Please don't play the repeating game!"
        )

        then:
        response.getEntity(String) == "Please don't play the repeating game!"
    }

    def 'When an Accept header is assigned, the server receives and processes it correctly'() {
        when:
        def response = httpClientFactory.createHttpClient().get(new HttpRequest()
            .setUri("${baseUrl}/testAccept")
            .setAccept('text/plain')
        )

        then:
        response.getEntity(String) == 'I am plain text.'
    }

    def 'When an unknown Accept header is assigned, the server receives it and returns an error'() {
        when:
        httpClientFactory.createHttpClient().get(new HttpRequest()
            .setUri("${baseUrl}/testAccept")
            .setAccept('foo/bar')
        )

        then:
        thrown HttpNotAcceptableException
    }

    def 'When a read timeout is reached, a SocketTimeoutException occurs'() {
        when:
        httpClientFactory.createHttpClient().get(new HttpRequest()
            .setUri("${baseUrl}/testReadTimeout")
            .setReadTimeout(1000)
        )

        then:
        thrown SocketTimeoutException
    }

    def 'When a call to /testBasicGet is made, the proper byte stream is received'() {
        when:
        def response = httpClientFactory.createHttpClient().get(new HttpRequest().setUri("${baseUrl}/testBasicGet"))

        then:
        response.getEntity() == [84, 104, 101, 32, 113, 117, 105, 99, 107, 32, 98, 114, 111, 119, 110, 32, 102, 111, 120, 32, 106, 117, 109, 112, 115, 32, 111, 118, 101, 114, 32, 116, 104, 101, 32, 108, 97, 122, 121, 32, 100, 111, 103, 46] as byte[]
    }

    def 'When a redirect is received and the client is configured to follow it, the proper response is received'() {
        when:
        def response = httpClientFactory.createHttpClient().get(new HttpRequest().setUri("${baseUrl}/testRedirect"))

        then:

        response.getEntity(String) == 'The quick brown fox jumps over the lazy dog.'
    }

    def 'When a redirect is received and the client is configured to not follow it, an HttpFoundException is thrown'() {
        when:
        httpClientFactory.createHttpClient().get(new HttpRequest()
            .setUri("${baseUrl}/testRedirect")
            .setFollowRedirects(false)
        )

        then:
        thrown HttpFoundException
    }

    def 'When a request includes headers, the server receives them correctly'() {
        when:
        def response = httpClientFactory.createHttpClient().get(new HttpRequest()
            .setUri("${baseUrl}/testHeaders")
            .addHeaders([foo: ['bar'], key: ['value']])
        )

        then:
        def json = response.getEntity(Map)
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
        def json = response.getEntity(Map)
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
        response.getEntity(Map) == [foo: ['bar'], key: ['value']]
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
        response.getEntity(Map) == ['foo': ['bar', 'baz'], 'hi': ['there']]
    }

    def 'When a response has a status of 500, an HttpInternalServerErrorException is thrown'() {
        when:
        httpClientFactory.createHttpClient().get(new HttpRequest().setUri("${baseUrl}/test500"))

        then:
        thrown HttpInternalServerErrorException
    }

    def 'When a response has a status of 500 but the client is configured to not throw exceptions, no exception is thrown'() {
        when:
        def response = httpClientFactory.createHttpClient().get(new HttpRequest()
            .setUri("${baseUrl}/test500")
            .setThrowStatusExceptions(false)
        )

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
        def response = httpClientFactory.createHttpClient().post(new HttpRequest().setUri("${baseUrl}/testForm").setLogConversation(true), formData)

        then:
        response.getEntity(Map) == ['foo': ['bar'], 'key': ['value']]
    }

    def 'When the client sends form data with multiple values as the request entity, the server receives them correctly'() {
        setup:
        FormData formData = new FormData()
        formData.addField('foo', 'bar')
        formData.addField('foo', 'baz')
        formData.addField('key', 'value')


        when:
        def response = httpClientFactory.createHttpClient().post(new HttpRequest().setUri("${baseUrl}/testForm").setLogConversation(true), formData)

        then:
        response.getEntity(Map) == ['foo': ['bar', 'baz'], 'key': ['value']]
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
        response.getEntity(String) == 'welcome'
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
        def response = httpClientFactory.createHttpClient().addListener(listener).get(
            new HttpRequest().setUri("${baseUrl}/testHeaders")
        )

        then:
        response.getEntity(Map).foo == ['bar']
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
            .get(new HttpRequest().setUri("${baseUrl}/testHeaders"))

        then:
        response.getEntity(Map).foo == ['bar']
        !response.getEntity(Map).hi
    }

    def 'Validate builder form of GET works'() {
        when:
        def response = httpClientFactory.createHttpClient().get {
            uri = "${baseUrl}/testBasicGet"
        }

        then:
        response.getEntity(String) == 'The quick brown fox jumps over the lazy dog.'
    }

    def 'Validate builder form of DELETE works'() {
        when:
        def response = httpClientFactory.createHttpClient().delete {
            uri = "${baseUrl}/testBasicDelete"
        }

        then:
        response.getEntity(String) == 'Please don\'t hurt me!'
    }

    /*
    def 'Validate builder form of TRACE works'() {
        when:
        def response = httpClientFactory.createHttpClient().trace {
            uri = "${baseUrl}/testBasicTrace"
            headers = [foo: 'bar']
            logConversation = true
        }

        then:
        response.entityAsJson == [foo: ['bar']]
    }
    */

    def 'Validate builder form of HEAD works'() {
        when:
        def response = httpClientFactory.createHttpClient().head { uri = "${baseUrl}/testBasicHead" }

        then:
        response.headers.foo == ['baz', 'bar']
        response.headers.hi == ['there']
        !response.hasEntity()
    }

    def 'Validate builder form of POST with no entity works'() {
        when:
        def response = httpClientFactory.createHttpClient().post { uri = "${baseUrl}/testBasicPost" }

        then:
        !response.hasEntity()
    }

    def 'Validate builder form of POST with a byte array entity works'() {
        when:
        def response = httpClientFactory.createHttpClient().post('Hello'.getBytes()) {
            uri = "${baseUrl}/testBasicPost"
        }

        then:
        response.getEntity(String) == 'Hello'
    }

    def 'Validate builder form of POST with a string entity works'() {
        when:
        def response = httpClientFactory.createHttpClient().post('Hello') { uri = "${baseUrl}/testBasicPost" }

        then:
        response.getEntity(String) == 'Hello'
    }

    def 'Validate builder form of POST with an input stream works'() {
        setup:
        def stream = new ByteArrayInputStream('Hello'.getBytes())

        when:
        def response = httpClientFactory.createHttpClient().post(stream) { uri = "${baseUrl}/testBasicPost" }

        then:
        response.getEntity(String) == 'Hello'
    }

    def 'Validate that a request can be made with a Map'() {
        when:
        def response = httpClientFactory.createHttpClient().post([foo: ['bar', 'baz']]) {
            uri = "${baseUrl}/testBasicPost"
        }

        then:
        response.getEntity(Map) == [foo: ['bar', 'baz']]
        response.getEntity(String) == '{"foo":["bar","baz"]}'
    }

    def 'Validate that a request can be made with a GString'() {
        setup:
        String val = "friend"

        when:
        def response = httpClientFactory.createHttpClient().post("Hello, ${val}!") {
            uri = "${baseUrl}/testBasicPost"
        }

        then:
        response.getEntity(String) == 'Hello, friend!'
    }

    def 'Validate builder form of POST with FormData works'() {
        setup:
        def form = new FormData()
        form.addField('foo', 'bar')
        form.addField('foo', 'baz')
        form.addField('hi', 'there')

        when:
        def response = httpClientFactory.createHttpClient().post(form) { uri = "${baseUrl}/testBasicPost" }

        then:
        response.getEntity(String).contains('foo=bar')
        response.getEntity(String).contains('foo=baz')
        response.getEntity(String).contains('hi=there')
    }

    def 'Validate builder form of PUT with no entity works'() {
        when:
        def response = httpClientFactory.createHttpClient().put { uri = "${baseUrl}/testBasicPut" }

        then:
        !response.hasEntity()
    }

    def 'Validate builder form of PUT with a byte array entity works'() {
        when:
        def response = httpClientFactory.createHttpClient().put('Hello'.getBytes()) { uri = "${baseUrl}/testBasicPut" }

        then:
        response.getEntity(String) == 'Hello'
    }

    def 'Validate builder form of PUT with a string entity works'() {
        when:
        def response = httpClientFactory.createHttpClient().put('Hello') { uri = "${baseUrl}/testBasicPut" }

        then:
        response.getEntity(String) == 'Hello'
    }

    def 'Validate builder form of PUT with an input stream works'() {
        setup:
        def stream = new ByteArrayInputStream('Hello'.getBytes())

        when:
        def response = httpClientFactory.createHttpClient().put(stream) { uri = "${baseUrl}/testBasicPut" }

        then:
        response.getEntity(String) == 'Hello'
    }

    /*
    def 'Validate builder form of PUT with FormData works'() {
        setup:
        def form = new FormData()
        form.addField('foo', 'bar')
        form.addField('foo', 'baz')
        form.addField('hi', 'there')

        when:
        def response = httpClientFactory.createHttpClient().put(form) { uri = "${baseUrl}/testBasicPut" }

        then:
        response.entityAsString == 'foo=bar&foo=baz&hi=there'
    }
    */

    def 'Validate request form of POST with no entity works'() {
        setup:
        def request = new HttpRequest("${baseUrl}/testBasicPost")

        when:
        def response = httpClientFactory.createHttpClient().post(request)

        then:
        !response.hasEntity()
    }

    def 'Validate request form of POST with a byte array entity works'() {
        setup:
        def request = new HttpRequest("${baseUrl}/testBasicPost")

        when:
        def response = httpClientFactory.createHttpClient().post(request, 'Hello'.getBytes())

        then:
        response.getEntity(String) == 'Hello'
    }

    def 'Validate request form of POST with a string entity works'() {
        setup:
        def request = new HttpRequest("${baseUrl}/testBasicPost")

        when:
        def response = httpClientFactory.createHttpClient().post(request, 'Hello')

        then:
        response.getEntity(String) == 'Hello'
    }

    def 'Validate request form of POST with an input stream works'() {
        setup:
        def request = new HttpRequest("${baseUrl}/testBasicPost")
        def stream = new ByteArrayInputStream('Hello'.getBytes())

        when:
        def response = httpClientFactory.createHttpClient().post(request, stream)

        then:
        response.getEntity(String) == 'Hello'
    }

    def 'Validate request form of POST with FormData works'() {
        setup:
        def form = new FormData()
        form.addField('foo', 'bar')
        form.addField('foo', 'baz')
        form.addField('hi', 'there')

        def request = new HttpRequest("${baseUrl}/testBasicPost")

        when:
        def response = httpClientFactory.createHttpClient().post(request, form)

        then:
        response.getEntity(String).contains('foo=bar')
        response.getEntity(String).contains('foo=baz')
        response.getEntity(String).contains('hi=there')
    }

    def 'Validate request form of PUT with no entity works'() {
        setup:
        def request = new HttpRequest("${baseUrl}/testBasicPut")

        when:
        def response = httpClientFactory.createHttpClient().put(request)

        then:
        !response.hasEntity()
    }

    def 'Validate request form of PUT with a byte array entity works'() {
        setup:
        def request = new HttpRequest("${baseUrl}/testBasicPut")

        when:
        def response = httpClientFactory.createHttpClient().put(request, 'Hello'.getBytes())

        then:
        response.getEntity(String) == 'Hello'
    }

    def 'Validate request form of PUT with a string entity works'() {
        setup:
        def request = new HttpRequest("${baseUrl}/testBasicPut")

        when:
        def response = httpClientFactory.createHttpClient().put(request, 'Hello')

        then:
        response.getEntity(String) == 'Hello'
    }

    def 'Validate request form of PUT with an input stream works'() {
        setup:
        def stream = new ByteArrayInputStream('Hello'.getBytes())
        def request = new HttpRequest("${baseUrl}/testBasicPut")

        when:
        def response = httpClientFactory.createHttpClient().put(request, stream)

        then:
        response.getEntity(String) == 'Hello'
    }

    /*
    def 'Validate request form of PUT with FormData works'() {
        setup:
        def form = new FormData()
        form.addField('foo', 'bar')
        form.addField('foo', 'baz')
        form.addField('hi', 'there')

        def request = new HttpRequest("${baseUrl}/testBasicPut")

        when:
        def response = httpClientFactory.createHttpClient().put(request, form)

        then:
        response.entityAsString == 'foo=bar&foo=baz&hi=there'
    }
    */

    def 'When a content type is already set, it will not be overwritten by a converter'() {
        when:
        def response = httpClientFactory.createHttpClient().post('hi!') {
            uri = "${baseUrl}/printContentType"
            contentType = 'foo/bar'
        }

        then:
        response.getEntity(String).startsWith('foo/bar')
    }

    def 'When no content type is set, it will be set by the converter'() {
        when:
        def response = httpClientFactory.createHttpClient().post('hi!') {
            uri = "${baseUrl}/printContentType"
        }

        then:
        response.getEntity(String).startsWith('text/plain')
    }
}
