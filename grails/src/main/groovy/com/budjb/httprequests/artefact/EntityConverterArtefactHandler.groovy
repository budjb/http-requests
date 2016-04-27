package com.budjb.httprequests.artefact

import com.budjb.httprequests.converter.EntityConverter
import grails.core.ArtefactHandlerAdapter

class EntityConverterArtefactHandler extends ArtefactHandlerAdapter {
    /**
     * Our artefact type.
     */
    public static final String TYPE = "EntityConverter"

    /**
     * Class suffix.
     */
    public static final String SUFFIX = "Converter"

    /**
     * Constructor.
     */
    EntityConverterArtefactHandler() {
        super(TYPE, GrailsEntityConverterClass, DefaultGrailsEntityConverterClass, SUFFIX)
    }

    /**
     * Determines if a class is an artefact of the type that this handler owns.
     *
     * @param clazz
     * @return
     */
    boolean isArtefactClass(@SuppressWarnings("rawtypes") Class clazz) {
        if (clazz == null) {
            return false
        }

        return clazz.getName().endsWith(SUFFIX) && EntityConverter.isAssignableFrom(clazz)
    }

    /**
     * Returns the name of the plugin responsible for this artefact type.
     *
     * @return
     */
    @Override
    String getPluginName() {
        return 'http-requests-grails'
    }
}
