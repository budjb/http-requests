package com.budjb.httprequests.converter.bundled

import com.budjb.httprequests.converter.EntityWriter

/**
 * An entity writer that will convert a GString.
 */
class GStringEntityWriter implements EntityWriter {
    /**
     * Returns a Content-Type of the converted object that will be set in the HTTP request.
     *
     * If no Content-Type is known, null is returned.
     *
     * @return Content-Type of the converted object, or null if unknown.
     */
    @Override
    String getContentType() {
        return 'text/plain'
    }

    /**
     * Determines whether the given class type is supported by the writer.
     *
     * @param type Type to convert.
     * @return Whether the type is supported.
     */
    @Override
    boolean supports(Class<?> type) {
        return GString.isAssignableFrom(type)
    }

    /**
     * Convert the given entity.
     *
     * If an error occurs, null may be returned so that another converter may attempt conversion.
     *
     * @param entity Entity object to convert into a byte array.
     * @param characterSet The character set of the request.
     * @return An {@link InputStream} containing the converted entity.
     * @throws Exception when an unexpected error occurs.
     */
    @Override
    InputStream write(Object entity, String characterSet) throws Exception {
        return new ByteArrayInputStream(entity.toString().getBytes(characterSet))
    }
}
