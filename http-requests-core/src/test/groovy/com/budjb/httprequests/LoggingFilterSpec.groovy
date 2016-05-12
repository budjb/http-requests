package com.budjb.httprequests

import com.budjb.httprequests.converter.bundled.StringEntityReader
import com.budjb.httprequests.filter.bundled.LoggingFilter
import org.slf4j.Logger
import spock.lang.Specification

class LoggingFilterSpec extends Specification {
    LoggingFilter filter
    Logger log
    HttpResponse httpResponse
    HttpContext httpContext
    MockHttpClient client

    def setup() {
        httpResponse = new HttpResponse()
        httpContext = new HttpContext()
        httpContext.response = httpResponse

        log = Mock(Logger)
        log.isDebugEnabled() >> true

        filter = new LoggingFilter()
        filter.setLogger(log)

        client = new MockHttpClient()
        client.addFilter(filter)
        client.addEntityConverter(new StringEntityReader())
    }

    def 'When a response has no entity, the entity is not logged'() {
        setup:
        client.responseInputStream = null
        client.status = 200

        when:
        def response = client.get { uri = "https://example.com" }

        then:
        response.status == 200
        1 * log.debug('Sending HTTP client request with the following data:\n> GET https://example.com\n')
        1 * log.debug('Received HTTP server response with the following data:\n< 200\n')
    }

    def 'When a response has an empty entity, the entity is not logged'() {
        setup:
        client.responseInputStream = new ByteArrayInputStream([] as byte[])
        client.status = 200

        when:
        def response = client.get { uri = "https://example.com" }

        then:
        response.status == 200
        1 * log.debug('Sending HTTP client request with the following data:\n> GET https://example.com\n')
        1 * log.debug('Received HTTP server response with the following data:\n< 200\n')
    }

    def 'When a response has a non-empty entity, the entity is logged'() {
        setup:
        InputStream responseInputStream = new EntityInputStream(new ByteArrayInputStream('response stuff'.bytes))
        client.responseInputStream = responseInputStream
        client.status = 200

        when:
        def response = client.get { uri = "https://example.com" }

        then:
        response.status == 200
        responseInputStream.isClosed()
        1 * log.debug('Sending HTTP client request with the following data:\n> GET https://example.com\n')
        1 * log.debug('Received HTTP server response with the following data:\n< 200\nresponse stuff\n')
    }

    def 'When a response has a non-empty entity that is not buffered, the entity is logged but the response input stream is not closed'() {
        setup:
        String content = '0123456789' * 1100
        InputStream responseInputStream = new EntityInputStream(new ByteArrayInputStream(content.bytes))
        client.responseInputStream = responseInputStream
        client.status = 200

        when:
        def response = client.get {
            uri = "https://example.com"
            bufferResponseEntity = false
        }

        then:
        response.status == 200
        !responseInputStream.isClosed()
        response.getEntity().getClass() == EntityInputStream
        response.getEntity().source.getClass() == PushbackInputStream
        response.getEntity().source.in.getClass() == BufferedInputStream
        response.getEntity().source.in.count == 10001
        response.getEntity(String) == content
        response.entityBuffer == null
    }
}
