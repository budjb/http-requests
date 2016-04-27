package com.budjb.httprequests

import com.budjb.httprequests.artefact.EntityConverterArtefactHandler
import com.budjb.httprequests.artefact.HttpClientFilterArtefactHandler
import com.budjb.httprequests.converter.EntityConverter
import com.budjb.httprequests.filter.HttpClientFilter
import grails.plugins.Plugin
import groovy.util.logging.Log4j

@Log4j
class HttpRequestsGrailsGrailsPlugin extends Plugin {
    /**
     * Grails version the plugin's intended for.
     */
    def grailsVersion = "3.0 > *"

    /**
     * Plugin title.
     */
    def title = "HTTP Requests Plugin"

    /**
     * Author name.
     */
    def author = "Bud Byrd"

    /**
     * Author email address.
     */
    def authorEmail = "bud.byrd@gmail.com"

    /**
     * Plugin description.
     */
    def description = 'The HTTP Requests Plugin provides the http-requests library and artefacts for filters and converters.'

    /**
     * Plugin documentation.
     */
    def documentation = "https://budjb.github.io/http-requests/TODO"

    /**
     * Plugin license.
     */
    def license = "APACHE"

    /**
     * Issue tracker.
     */
    def issueManagement = [system: "GITHUB", url: "https://github.com/budjb/http-requests/issues"]

    /**
     * SCM.
     */
    def scm = [url: "https://github.com/budjb/http-requests"]

    /**
     * Create beans for the {@link HttpClientFactory} and any {@link HttpClientFilter} or
     * {@link EntityConverter} artefacts.
     *
     * @return
     */
    Closure doWithSpring() {
        { ->
            if (config.httprequests.autoLoadProvider != false) {
                httpClientFactory(ProviderScanner.scanClasspathForProvider()) { bean ->
                    bean.autowire = true
                }
            }
            else {
                log.debug("not attempting to automatically load the httpClientFactory bean due to this feature being " +
                    "disabled in the application configuration")
            }

            grailsApplication.getArtefacts(HttpClientFilterArtefactHandler.TYPE).each {
                "${it.getClazz().getName()}"(it.getClazz())
            }

            grailsApplication.getArtefacts(EntityConverterArtefactHandler.TYPE).each {
                "${it.getClazz().getName()}"(it.getClazz())
            }
        }
    }

    /**
     * Registers any filter or converter artefacts to the HTTP client factory instance.
     */
    @Override
    void doWithApplicationContext() {
        HttpClientFactory httpClientFactory = applicationContext.getBean('httpClientFactory', HttpClientFactory)

        grailsApplication.getArtefacts(HttpClientFilterArtefactHandler.TYPE).each {
            httpClientFactory.addFilter(applicationContext.getBean(it.getClazz().getName(), HttpClientFilter))
        }

        grailsApplication.getArtefacts(EntityConverterArtefactHandler.TYPE).each {
            httpClientFactory.addEntityConverter(applicationContext.getBean(it.getClazz().getName(), EntityConverter))
        }
    }
}
