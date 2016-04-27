package com.budjb.httprequests.artefact

import com.budjb.httprequests.filter.HttpClientFilter
import grails.core.ArtefactHandlerAdapter

class HttpClientFilterArtefactHandler extends ArtefactHandlerAdapter {
    /**
     * Our artefact type.
     */
    public static final String TYPE = "HttpClientFilter"

    /**
     * Class suffix.
     */
    public static final String SUFFIX = "Filter"

    /**
     * Constructor.
     */
    HttpClientFilterArtefactHandler() {
        super(TYPE, GrailsHttpClientFilterClass, DefaultGrailsHttpClientFilterClass, SUFFIX)
    }

    /**
     * Determines if a class is an artefact of the type that this handler owns.
     *
     * @param clazz
     * @return
     */
    boolean isArtefactClass(@SuppressWarnings("rawtypes") Class clazz) {
        if (!HttpClientFilter.isAssignableFrom(clazz)) {
            return false
        }
        return super.isArtefactClass(clazz)
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
