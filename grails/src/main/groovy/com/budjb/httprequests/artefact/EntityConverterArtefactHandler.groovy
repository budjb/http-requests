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
