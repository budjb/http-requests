/*
 * Copyright 2016-2018 the original author or authors.
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
package com.budjb.httprequests;

import java.util.Objects;

/**
 * A holder class that is useful to contain an instance of {@link HttpClientFactory} and make it available to the rest
 * of an application as a global singleton.
 * <p>
 * It also serves to isolate the rest of the application from having to be aware of the specific HTTP client
 * implementation, since the {@link HttpClientFactory} can be created and stored during an application's initialization,
 * and classes that use this holder will no longer need to create a factory instance any time an {@link HttpClient} is
 * required.
 * <p>
 * This class is useful in applications that do not use Spring Framework, but for those applications that are using
 * Spring Framework, a better option is to register the {@link HttpClientFactory} implementation as a bean that can be
 * injected into other Spring beans. This holder effectively serves to provide global access to a factory object in
 * the absence of Spring Framework bean injection.
 */
public class HttpClientFactoryHolder {
    /**
     * Singleton instance of the holder.
     */
    private static final HttpClientFactoryHolder instance = new HttpClientFactoryHolder();

    /**
     * Instance of the HTTP client factory contained in the holder.
     */
    private HttpClientFactory factory;

    /**
     * Returns the {@link HttpClientFactory} registered with the holder.
     * <p>
     * An {@link IllegalStateException} will be thrown if there is no client registered.
     *
     * @return The {@link HttpClientFactory} registered with the holder.
     */
    public static HttpClientFactory getHttpClientFactory() {
        return Objects.requireNonNull(instance.factory, "no HttpClientFactory is registered with the HttpClientFactoryHolder");
    }
}
