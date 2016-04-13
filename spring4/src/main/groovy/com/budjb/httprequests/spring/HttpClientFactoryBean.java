package com.budjb.httprequests.spring;

import com.budjb.httprequests.HttpClient;
import com.budjb.httprequests.HttpClientFactory;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.SmartFactoryBean;

/**
 * A FactoryBean implementation that handles the creation of {@link HttpClient} objects.
 */
@Slf4j
public class HttpClientFactoryBean implements FactoryBean<HttpClient> {
    /**
     * HTTP client factory.
     */
    private HttpClientFactory httpClientFactory;

    /**
     * Constructor.
     *
     * @param httpClientFactory An instance of the {@link HttpClientFactory} that will
     *                          create {@link HttpClient} instances.
     */
    public HttpClientFactoryBean(HttpClientFactory httpClientFactory) {
        if (httpClientFactory == null) {
            throw new IllegalArgumentException("httpClientFactory can not be null");
        }
        this.httpClientFactory = httpClientFactory;
    }

    /**
     * Create and return a new {@link HttpClient} instance.
     *
     * @return an instance of the bean (can be {@code null})
     * @throws Exception in case of creation errors
     * @see FactoryBeanNotInitializedException
     */
    @Override
    public HttpClient getObject() throws Exception {
        return httpClientFactory.createHttpClient();
    }

    /**
     * Return the type of object that this FactoryBean creates.
     *
     * @return the type of object that this FactoryBean creates,
     * or {@code null} if not known at the time of the call.
     * @see ListableBeanFactory#getBeansOfType
     */
    @Override
    public Class<?> getObjectType() {
        return HttpClient.class;
    }

    /**
     * Report that this factory bean will always create a new instance (e.g. prototype scope).
     *
     * @return whether the exposed object is a singleton
     * @see #getObject()
     * @see SmartFactoryBean#isPrototype()
     */
    @Override
    public boolean isSingleton() {
        return false;
    }
}