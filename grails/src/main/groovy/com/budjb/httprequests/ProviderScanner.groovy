package com.budjb.httprequests

import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AssignableTypeFilter

class ProviderScanner {
    /**
     * Scans for and returns the first {@link HttpClientFactory} instance found on the classpath.
     *
     * The package <code>com.budjb.httprequests</code> will be scanned. More base packages can
     * optionally be provided for scanning, in the case of custom providers.
     *
     * @param packages List of additional classpath packages to scan.
     * @return The first provider factory found on the classpath.
     * @throws IllegalStateException when no provider is found or multiple providers are found.
     */
    static Class<? extends HttpClientFactory> scanClasspathForProvider(List<String> packages = []) throws IllegalStateException {
        packages.add('com.budjb.httprequests')

        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false)
        provider.addIncludeFilter(new AssignableTypeFilter(HttpClientFactory))

        List<Class<? extends HttpClientFactory>> candidates = []

        for (String pkg : packages) {
            provider.findCandidateComponents(pkg).each {
                candidates.add(Class.forName(it.getBeanClassName()) as Class<? extends HttpClientFactory>)
            }
        }

        if (candidates.size() == 0) {
            throw new IllegalStateException("no provider with supertype 'com.budjb.httprequests.HttpClientFactory' found on the classpath")
        }
        else if (candidates.size() > 1) {
            throw new IllegalStateException("multiple providers with supertype 'com.budjb.httprequests.HttpClientFactory' found on the classpath: ${candidates*.simpleName.join(', ')}")
        }

        return candidates.get(0)
    }
}
