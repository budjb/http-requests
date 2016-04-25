package com.budjb.httprequests.converter.bundled

import com.budjb.httprequests.FormData
import com.budjb.httprequests.converter.EntityWriter

/**
 * An entity writer that formats form data.
 */
class FormDataEntityWriter implements EntityWriter {
    /**
     * Returns a Content-Type of the converted object that will be set in the HTTP request.
     *
     * If no Content-Type is known, null is returned.
     *
     * @return Content-Type of the converted object, or null if unknown.
     */
    @Override
    String getContentType() {
        return 'application/x-www-form-urlencoded'
    }

    /**
     * Determines whether the given class type is supported by the writer.
     *
     * @param type Type to convert.
     * @return Whether the type is supported.
     */
    @Override
    boolean supports(Class<?> type) {
        return FormData.isAssignableFrom(type)
    }

    /**
     * Convert the given entity.
     *
     * If an error occurs, null may be returned so that another converter may attempt conversion.
     *
     * @param entity Entity as an {@link InputStream}.
     * @param characterSet The character set of the request.
     * @return The converted object, or null if an error occurs.
     * @throws Exception when an unexpected error occurs.
     */
    @Override
    InputStream write(Object entity, String characterSet) throws Exception {
        if (!(entity instanceof FormData)) {
            return null
        }

        List<String> parts = []

        entity.getFields().each { name, values ->
            name = URLEncoder.encode(name, characterSet)

            if (values instanceof Collection) {
                values.each { value ->
                    parts << "${name}=${URLEncoder.encode(value.toString(), characterSet)}"
                }
            }
            else {
                parts << "${name}=${URLEncoder.encode(values.toString(), characterSet)}"
            }
        }

        return new ByteArrayInputStream(parts.join('&').getBytes(characterSet))
    }
}
