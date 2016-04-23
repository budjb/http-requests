package com.budjb.httprequests

import com.budjb.httprequests.converter.ConverterManager
import com.budjb.httprequests.converter.EntityConverter
import com.budjb.httprequests.converter.StringEntityReader
import com.budjb.httprequests.converter.StringEntityWriter
import com.budjb.httprequests.exception.UnsupportedConversionException
import com.budjb.httprequests.support.NullHttpClient
import spock.lang.Specification

class ConverterSpec extends Specification {
    def 'When a converter is removed from an HttpClient, it is still present in the factory'() {
        setup:
        EntityConverter converter = new StringEntityReader()

        ConverterManager converterManager = new ConverterManager()
        converterManager.add(converter)
        converterManager.add(new StringEntityWriter())

        HttpClient httpClient = new NullHttpClient() {}
        httpClient.converterManager = new ConverterManager(converterManager)

        when:
        httpClient.removeEntityConverter(converter)

        then:
        !httpClient.getEntityConverters().contains(converter)
        converterManager.getAll().contains(converter)
    }

    def 'When no reader is available to perform conversion, an UnsupportedConversionException is thrown'() {
        setup:
        ConverterManager converterManager = new ConverterManager()

        when:
        converterManager.read(String, [1, 2, 3] as byte[], null, null)

        then:
        thrown UnsupportedConversionException
    }

    def 'When no writer is available to perform conversion, an UnsupportedConversionException is thrown'() {
        setup:
        ConverterManager converterManager = new ConverterManager()

        when:
        converterManager.write(null, 'Hello!')

        then:
        thrown UnsupportedConversionException
    }
}
