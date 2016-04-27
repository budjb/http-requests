package com.budjb.httprequests.converter

import com.budjb.httprequests.EntityInputStream
import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.exception.UnsupportedConversionException
import groovy.util.logging.Slf4j

@Slf4j
class EntityConverterManager {
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
        Class<?> type = entity.getClass()

        for (EntityWriter writer : getEntityWriters()) {
            if (writer.supports(type)) {
                try {
                    InputStream inputStream = writer.write(entity, request.getCharset())

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
