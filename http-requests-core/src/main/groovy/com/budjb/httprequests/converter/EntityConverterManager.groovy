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
package com.budjb.httprequests.converter

import com.budjb.httprequests.EntityInputStream
import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.exception.UnsupportedConversionException
import groovy.util.logging.Slf4j

@Slf4j
class EntityConverterManager {
    /**
     * Default character set.
     */
    final static String DEFAULT_CHARSET = 'ISO-8859-1'

    /**
     * List of registered entity converters.
     */
    private final List<EntityConverter> converters

    /**
     * Base constructor.
     */
    EntityConverterManager() {
        converters = []
    }

    /**
     * Creates a converter manager with the contents of another manager.
     *
     * @param other Other converter manager to make a copy of.
     */
    EntityConverterManager(EntityConverterManager other) {
        converters = []

        other.getAll().each {
            add(it)
        }
    }

    /**
     * Adds an entity converter to the manager.
     *
     * @param converter Converter to add to the manager.
     */
    void add(EntityConverter converter) {
        if (!converters.find { it.getClass() == converter.getClass() }) {
            converters.add(converter)
        }
    }

    /**
     * Returns the list of entity converters.
     *
     * @return List of entity converters.
     */
    List<EntityConverter> getAll() {
        return converters
    }

    /**
     * Remove an entity converter.
     *
     * @param converter Entity converter to remove.
     */
    void remove(EntityConverter converter) {
        converters.remove(converter)
    }

    /**
     * Remove all entity converters.
     */
    void clear() {
        converters.clear()
    }

    /**
     * Returns the list of all registered entity readers.
     *
     * @return
     */
    List<EntityReader> getEntityReaders() {
        return converters.findAll { it instanceof EntityReader } as List<EntityReader>
    }

    /**
     * Returns the list of all registered entity writers.
     *
     * @return
     */
    List<EntityWriter> getEntityWriters() {
        return converters.findAll { it instanceof EntityWriter } as List<EntityWriter>
    }

    /**
     * Attempts to convert the given entity into an {@link InputStream}. If an entity writer is successful,
     * the content type for the conversion is set in the request, if a content type is available.
     *
     * @param request HTTP request properties.
     * @param entity Entity object to convert.
     * @return Converted entity as an InputStream.
     * @throws UnsupportedConversionException when there are no entity writers that support the object type.
     */
    InputStream write(HttpRequest request, Object entity) throws UnsupportedConversionException {
        String characterSet = request.getCharset() ?: DEFAULT_CHARSET

        Class<?> type = entity.getClass()

        for (EntityWriter writer : getEntityWriters()) {
            if (writer.supports(type)) {
                try {
                    InputStream inputStream = writer.write(entity, characterSet)

                    if (inputStream == null) {
                        continue
                    }

                    if (request.getContentType() == null) {
                        String contentType = writer.getContentType()
                        if (contentType) {
                            log.trace("applying Content-Type '${contentType}' to the request")
                            request.setContentType(contentType)
                        }
                    }
                    return inputStream
                }
                catch (Exception e) {
                    log.trace("error occurred during conversion with EntityWriter ${writer.getClass()}", e)
                }
            }
        }

        throw new UnsupportedConversionException(type)
    }

    /**
     * Reads an object from the given entity {@link InputStream}.
     *
     * @param type Object type to attempt conversion to.
     * @param entity Entity input stream.
     * @param contentType Content Type of the entity.
     * @param charset Character set of the entity.
     * @return The converted object.
     * @throws UnsupportedConversionException when there are no entity writers that support the object type.
     */
    public <T> T read(Class<?> type, InputStream entity, String contentType, String charset) throws UnsupportedConversionException, IOException {
        if (entity instanceof EntityInputStream && entity.isClosed()) {
            throw new IOException("entity stream is closed")
        }

        for (EntityReader reader : getEntityReaders()) {
            if (reader.supports(type)) {
                try {
                    T object = reader.read(entity, contentType, charset) as T

                    if (object != null) {
                        return object
                    }
                }
                catch (Exception e) {
                    log.trace("error occurred during conversion with EntityReader ${reader.getClass()}", e)
                }
            }
        }

        throw new UnsupportedConversionException(type)
    }
}
