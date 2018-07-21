/*
 * Copyright 2016-2018 the original author or authors.
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

package com.budjb.httprequests.converter

import com.budjb.httprequests.HttpEntity
import com.budjb.httprequests.exception.UnsupportedConversionException
import spock.lang.Specification

class EntityConverterManagerSpec extends Specification {
    def 'When creating an EntityConverterManager from another one, all converters are copied into the new one'() {
        setup:
        EntityConverter ec1 = Mock(EntityConverter)
        EntityConverter ec2 = Mock(EntityConverter)

        EntityConverterManager m1 = new EntityConverterManager()
        m1.add(ec1)
        m1.add(ec2)

        when:
        EntityConverterManager m2 = new EntityConverterManager(m1)

        then:
        m2.getAll().sort() == m1.getAll().sort()
    }

    def 'When an EntityConverter is added, the manager contains it'() {
        setup:
        EntityConverter converter = Mock(EntityConverter)
        EntityConverterManager manager = new EntityConverterManager()

        when:
        manager.add(converter)

        then:
        manager.getAll().size() == 1
        manager.getAll().get(0).is converter
    }

    def 'After removing a converter, it is no longer contained in the manager'() {
        setup:
        EntityConverter converter = Mock(EntityConverter)
        EntityConverterManager manager = new EntityConverterManager()
        manager.add(converter)

        expect:
        manager.getAll().contains(converter)

        when:
        manager.remove(converter)

        then:
        !manager.getAll().contains(converter)
    }

    def 'After clearing the manager, no converters are contained in the manager'() {
        setup:
        EntityConverter ec1 = Mock(EntityConverter)
        EntityConverter ec2 = Mock(EntityConverter)

        EntityConverterManager manager = new EntityConverterManager()
        manager.add(ec1)
        manager.add(ec2)

        when:
        manager.clear()

        then:
        manager.getAll().size() == 0
    }

    def 'When getting entity readers, only entity converters that are readers are returned'() {
        setup:
        EntityWriter ec1 = Mock(EntityWriter)
        EntityReader ec2 = Mock(EntityReader)

        EntityConverterManager manager = new EntityConverterManager()
        manager.add(ec1)
        manager.add(ec2)

        when:
        List<EntityReader> readers = manager.getEntityReaders()

        then:
        readers.size() == 1
        readers.get(0).is ec2
    }

    def 'When getting entity writers, only entity converters that are writers are returned'() {
        setup:
        EntityWriter ec1 = Mock(EntityWriter)
        EntityReader ec2 = Mock(EntityReader)

        EntityConverterManager manager = new EntityConverterManager()
        manager.add(ec1)
        manager.add(ec2)

        when:
        List<EntityWriter> writers = manager.getEntityWriters()

        then:
        writers.size() == 1
        writers.get(0).is ec1
    }

    def 'The correct entity reader is used to perform conversion'() {
        setup:
        EntityReader good = Mock(EntityReader)
        EntityReader bad = Mock(EntityReader)
        EntityWriter writer = Mock(EntityWriter)

        EntityConverterManager converterManager = new EntityConverterManager()
        converterManager.add(bad)
        converterManager.add(good)
        converterManager.add(writer)

        good.supports(String) >> true

        HttpEntity entity = new HttpEntity(new ByteArrayInputStream([1, 2, 3] as byte[]), null, null)

        when:
        String result = converterManager.read(String, entity)

        then:
        0 * bad.read(*_)
        0 * writer.write(*_)
        1 * good.read(*_) >> "foo"
        result == "foo"
    }

    def 'The correct entity writer is used to perform conversion'() {
        setup:
        EntityWriter good = Mock(EntityWriter)
        EntityWriter bad = Mock(EntityWriter)
        EntityReader reader = Mock(EntityReader)

        EntityConverterManager converterManager = new EntityConverterManager()
        converterManager.add(bad)
        converterManager.add(good)
        converterManager.add(reader)

        good.supports(String) >> true

        InputStream inputStream = new ByteArrayInputStream([1, 2, 3] as byte[])

        when:
        HttpEntity result = converterManager.write("foo", null, null)

        then:
        0 * bad.write(*_)
        0 * reader.read(*_)
        1 * good.write(*_) >> inputStream
        ((PushbackInputStream)result.inputStream).in.is inputStream
    }

    def 'When no reader is available to perform conversion, an UnsupportedConversionException is thrown'() {
        setup:
        EntityConverterManager converterManager = new EntityConverterManager()

        HttpEntity entity = new HttpEntity(new ByteArrayInputStream([1, 2, 3] as byte[]), null, null)

        when:
        converterManager.read(String, entity)

        then:
        thrown UnsupportedConversionException
    }

    def 'When no writer is available to perform conversion, an UnsupportedConversionException is thrown'() {
        setup:
        EntityConverterManager converterManager = new EntityConverterManager()

        when:
        converterManager.write('Hello!', null, null)

        then:
        thrown UnsupportedConversionException
    }
}
