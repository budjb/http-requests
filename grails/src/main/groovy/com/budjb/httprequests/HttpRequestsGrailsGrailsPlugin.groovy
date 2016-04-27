package com.budjb.httprequests

import com.budjb.httprequests.artefact.EntityConverterArtefactHandler
import com.budjb.httprequests.artefact.HttpClientFilterArtefactHandler
import com.budjb.httprequests.converter.EntityConverter
import com.budjb.httprequests.filter.HttpClientFilter
import grails.plugins.Plugin
import groovy.util.logging.Slf4j
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AssignableTypeFilter

@Slf4j
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
            if (autoLoadFactory()) {
                httpClientFactory(scanClasspathForProvider(), autoLoadConverters()) { bean ->
                    bean.autowire = true
                }
            }
            else {
                log.debug("Not attempting to automatically load the httpClientFactory bean due to this feature being " +
                    "disabled in the application configuration.")
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
            log.debug("Adding HttpClientFilter '${it.getClazz().getSimpleName()}'")
            httpClientFactory.addFilter(applicationContext.getBean(it.getClazz().getName(), HttpClientFilter))
        }

        grailsApplication.getArtefacts(EntityConverterArtefactHandler.TYPE).each {
            log.debug("Adding EntityConverter '${it.getClazz().getSimpleName()}'")
            httpClientFactory.addEntityConverter(applicationContext.getBean(it.getClazz().getName(), EntityConverter))
        }
    }

    private Class<? extends HttpClientFactory> scanClasspathForProvider() throws IllegalStateException {
        List<String> packages = ['com.budjb.httprequests']
        packages.addAll(getAdditionalPackages())

        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false)
        provider.addIncludeFilter(new AssignableTypeFilter(HttpClientFactory))

        List<Class<? extends HttpClientFactory>> candidates = []

        for (String pkg : packages) {
            provider.findCandidateComponents(pkg).each {
                log.debug("Located HttpClientFactory provider: ${it.getBeanClassName()}")
                candidates.add(Class.forName(it.getBeanClassName()) as Class<? extends HttpClientFactory>)
            }
        }

        if (candidates.size() == 0) {
            throw new IllegalStateException("no provider with type 'com.budjb.httprequests.HttpClientFactory' found on the classpath")
        }
        else if (candidates.size() > 1) {
            throw new IllegalStateException("multiple providers with type 'com.budjb.httprequests.HttpClientFactory' found on the classpath: ${candidates*.simpleName.join(', ')}")
        }

        Class<? extends HttpClientFactory> clazz = candidates.get(0)
        log.debug("Registering HttpClientFactory provider: ${clazz.getName()}")
        return clazz
    }

    /**
     * Returns whether to automatically load the built-in converters.
     *
     * @return
     */
    boolean autoLoadConverters() {
        return config.httprequests.autoLoadConverters != false
    }

    /**
     * Returns whether to automatically load an {@link HttpClientFactory} from the classpath.
     *
     * @return
     */
    boolean autoLoadFactory() {
        return config.httprequests.autoLoadFactory != false
    }

    /**
     * Return the list of additional classpath packages to scan for {@link HttpClientFactory}
     * implementations.
     *
     * @return
     */
    List<String> getAdditionalPackages() {
        if (config.httprequests.scanPackages instanceof List) {
            return config.httprequests.scanPackages
        }
        return []
    }
}
