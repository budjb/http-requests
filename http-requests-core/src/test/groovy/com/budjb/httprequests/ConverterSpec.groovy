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

import com.budjb.httprequests.converter.EntityConverterManager
import com.budjb.httprequests.converter.EntityConverter
import com.budjb.httprequests.converter.bundled.StringEntityReader
import com.budjb.httprequests.converter.bundled.StringEntityWriter
import com.budjb.httprequests.exception.UnsupportedConversionException
import com.budjb.httprequests.support.NullHttpClient
import spock.lang.Specification

class ConverterSpec extends Specification {
    def 'When a converter is removed from an HttpClient, it is still present in the factory'() {
        setup:
        EntityConverter converter = new StringEntityReader()

        EntityConverterManager converterManager = new EntityConverterManager()
        converterManager.add(converter)
        converterManager.add(new StringEntityWriter())

        HttpClient httpClient = new NullHttpClient() {}
        httpClient.converterManager = new EntityConverterManager(converterManager)

        when:
        httpClient.removeEntityConverter(converter)

        then:
        !httpClient.getEntityConverters().contains(converter)
        converterManager.getAll().contains(converter)
    }

    def 'When no reader is available to perform conversion, an UnsupportedConversionException is thrown'() {
        setup:
        EntityConverterManager converterManager = new EntityConverterManager()

        when:
        converterManager.read(String, new ByteArrayInputStream([1, 2, 3] as byte[]), null, null)

        then:
        thrown UnsupportedConversionException
    }

    def 'When no writer is available to perform conversion, an UnsupportedConversionException is thrown'() {
        setup:
        EntityConverterManager converterManager = new EntityConverterManager()

        when:
        converterManager.write(null, 'Hello!')

        then:
        thrown UnsupportedConversionException
    }
}
