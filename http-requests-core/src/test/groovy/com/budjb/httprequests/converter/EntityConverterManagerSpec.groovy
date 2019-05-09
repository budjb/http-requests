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
    def 'When an EntityConverter is added, the manager contains it'() {
        setup:
        EntityConverter converter = Mock(EntityConverter)
        EntityConverterManager manager = new EntityConverterManager([converter])

        expect:
        manager.getAll().size() == 1
        manager.getAll().get(0).is converter
    }

    def 'Getting all converters returns all converters that have been added to the manager'() {
        setup:
        EntityConverter c1 = Mock(EntityConverter)
        EntityConverter c2 = Mock(EntityConverter)

        EntityConverterManager manager = new EntityConverterManager([c1, c2])

        expect:
        manager.getAll() == [c1, c2]
    }

    def 'When getting entity readers, only entity converters that are readers are returned'() {
        setup:
        EntityWriter ec1 = Mock(EntityWriter)
        EntityReader ec2 = Mock(EntityReader)

        EntityConverterManager manager = new EntityConverterManager([ec1, ec2])

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

        EntityConverterManager manager = new EntityConverterManager([ec1, ec2])

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

        EntityConverterManager converterManager = new EntityConverterManager([bad, good, writer])

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

        EntityConverterManager converterManager = new EntityConverterManager([bad, good, reader])

        good.supports(String) >> true

        InputStream inputStream = new ByteArrayInputStream([1, 2, 3] as byte[])

        when:
        HttpEntity result = converterManager.write("foo", null, null)

        then:
        0 * bad.write(*_)
        0 * reader.read(*_)
        1 * good.write(*_) >> inputStream
        ((PushbackInputStream) result.inputStream).in.is inputStream
    }

    def 'When no reader is available to perform conversion, an UnsupportedConversionException is thrown'() {
        setup:
        EntityConverterManager converterManager = new EntityConverterManager([])

        HttpEntity entity = new HttpEntity(new ByteArrayInputStream([1, 2, 3] as byte[]), null, null)

        when:
        converterManager.read(String, entity)

        then:
        thrown UnsupportedConversionException
    }

    def 'When no writer is available to perform conversion, an UnsupportedConversionException is thrown'() {
        setup:
        EntityConverterManager converterManager = new EntityConverterManager([])

        when:
        converterManager.write('Hello!', null, null)

        then:
        thrown UnsupportedConversionException
    }

    def 'When writing an entity with no content type or character set, null values are used'() {
        setup:
        EntityWriter converter = Mock(EntityWriter)
        converter.supports(_) >> true

        EntityConverterManager manager = new EntityConverterManager([converter])

        when:
        manager.write('foo')

        then:
        1 * converter.write('foo', null) >> new ByteArrayInputStream('foo'.getBytes())
    }

    def 'If an entity writer returns null, other entity writers are attempted'() {
        setup:
        ByteArrayInputStream inputStream = new ByteArrayInputStream('foo'.getBytes())

        EntityWriter c1 = Mock(EntityWriter)
        c1.supports(_) >> true

        EntityWriter c2 = Mock(EntityWriter)
        c2.supports(_) >> true
        c2.write(*_) >> inputStream

        EntityConverterManager manager = new EntityConverterManager([c1, c2])

        when:
        HttpEntity entity = manager.write('foo')

        then:
        entity.inputStream.in.is inputStream
    }

    def 'If an entity writer throws an exception, other entity writers are attempted'() {
        setup:
        ByteArrayInputStream inputStream = new ByteArrayInputStream('foo'.getBytes())

        EntityWriter c1 = Mock(EntityWriter)
        c1.supports(_) >> true
        c1.write(*_) >> { throw new RuntimeException() }

        EntityWriter c2 = Mock(EntityWriter)
        c2.supports(_) >> true
        c2.write(*_) >> inputStream

        EntityConverterManager manager = new EntityConverterManager([c1, c2])

        when:
        HttpEntity entity = manager.write('foo')

        then:
        entity.inputStream.in.is inputStream
    }

    def 'If an entity reader throws an exception, other entity readers are attempted'() {
        setup:
        EntityReader c1 = Mock(EntityReader)
        c1.supports(_) >> true
        c1.read(*_) >> { throw new RuntimeException() }

        EntityReader c2 = Mock(EntityReader)
        c2.supports(_) >> true
        c2.read(*_) >> 'foo'

        EntityConverterManager manager = new EntityConverterManager([c1, c2])

        HttpEntity entity = Mock(HttpEntity)
        entity.getInputStream() >> Mock(InputStream)

        when:
        String result = manager.read(String, entity)

        then:
        result == 'foo'
    }

    def 'If an entity reader throws an IOException, the manager throws it'() {
        setup:
        EntityReader c1 = Mock(EntityReader)
        c1.supports(_) >> true
        c1.read(*_) >> { throw new IOException() }

        EntityConverterManager manager = new EntityConverterManager([c1])

        HttpEntity entity = Mock(HttpEntity)
        entity.getInputStream() >> Mock(InputStream)

        when:
        manager.read(String, entity)

        then:
        thrown IOException
    }
}
