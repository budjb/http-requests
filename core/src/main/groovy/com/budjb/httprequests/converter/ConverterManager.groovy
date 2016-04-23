package com.budjb.httprequests.converter

import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.exception.UnsupportedConversionException
import groovy.util.logging.Slf4j

@Slf4j
class ConverterManager {
    /**
     * List of registered entity converters.
     */
    private final List<EntityConverter> converters

    /**
     * Base constructor.
     */
    ConverterManager() {
        converters = []
    }

    /**
     * Creates a converter manager with the contents of another manager.
     *
     * @param other Other converter manager to make a copy of.
     */
    ConverterManager(ConverterManager other) {
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
     * Attempts to convert the given entity into a byte array. If an entity writer is successful,
     * the content type for the conversion is set in the request, if a content type is available.
     *
     * @param request HTTP request properties.
     * @param entity Entity object to convert.
     * @return Converted entity as a byte array.
     * @throws UnsupportedConversionException when there are no entity writers that support the object type.
     */
    byte[] write(HttpRequest request, Object entity) throws UnsupportedConversionException {
        Class<?> type = entity.getClass()

        for (EntityWriter writer : getEntityWriters()) {
            if (writer.supports(type)) {
                try {
                    byte[] bytes = writer.write(entity)

                    if (bytes == null) {
                        continue
                    }

                    if (request.getContentType() == null) {
                        String contentType = writer.getContentType()
                        if (contentType) {
                            log.trace("applying Content-Type '${contentType}' to the request")
                            request.setContentType(contentType)
                        }
                    }
                    return bytes
                }
                catch (Exception e) {
                    log.trace("error occurred during conversion with EntityWriter ${writer.getClass()}", e)
                }
            }
        }

        throw new UnsupportedConversionException(type)
    }

    /**
     *
     * @param type
     * @param entity
     * @param contentType
     * @param charset
     * @return
     * @throws UnsupportedConversionException
     */
    public <T> T read(Class<?> type, byte[] entity, String contentType, String charset) throws UnsupportedConversionException {
        for (EntityReader reader : getEntityReaders()) {
            if (reader.support(type)) {
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
