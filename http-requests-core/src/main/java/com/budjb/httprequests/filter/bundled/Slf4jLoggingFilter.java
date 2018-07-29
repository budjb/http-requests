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
package com.budjb.httprequests.filter.bundled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link LoggingFilter} implementation that logs the HTTP conversation to an SLF4J logger.
 */
public class Slf4jLoggingFilter extends LoggingFilter {
    /**
     * Logger.
     */
    private final Logger logger;

    /**
     * Log level to log with.
     */
    private final LoggerLevel level;

    /**
     * Constructor.
     */
    public Slf4jLoggingFilter() {
        this(Slf4jLoggingFilter.class);
    }

    /**
     * Constructor.
     *
     * @param name Name of the logger.
     */
    public Slf4jLoggingFilter(String name) {
        this(LoggerFactory.getLogger(name), LoggerLevel.TRACE);
    }

    /**
     * Constructor.
     *
     * @param name  Name of the logger.
     * @param level Logger level to output logs to.
     */
    public Slf4jLoggingFilter(String name, LoggerLevel level) {
        this(LoggerFactory.getLogger(name), level);
    }

    /**
     * Constructor.
     *
     * @param clazz Class to base the logger on.
     */
    public Slf4jLoggingFilter(Class clazz) {
        this(LoggerFactory.getLogger(clazz), LoggerLevel.TRACE);
    }

    /**
     * Constructor.
     *
     * @param clazz Class to base the logger on.
     * @param level Logger level to output logs to.
     */
    public Slf4jLoggingFilter(Class clazz, LoggerLevel level) {
        this(LoggerFactory.getLogger(clazz), level);
    }

    /**
     * Constructor.
     *
     * @param logger Logger instance to use.
     * @param level  Logger level to output logs to.
     */
    public Slf4jLoggingFilter(Logger logger, LoggerLevel level) {
        this.logger = logger;
        this.level = level;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void write(String content) {
        switch (level) {
            case TRACE:
                if (logger.isTraceEnabled()) {
                    logger.trace(content);
                }
                break;

            case DEBUG:
                if (logger.isDebugEnabled()) {
                    logger.debug(content);
                }
                break;

            case INFO:
                if (logger.isInfoEnabled()) {
                    logger.info(content);
                }
                break;

            case WARN:
                if (logger.isWarnEnabled()) {
                    logger.warn(content);
                }
                break;

            case ERROR:
                if (logger.isErrorEnabled()) {
                    logger.error(content);
                }
        }
    }

    /**
     * Logger level.
     */
    public enum LoggerLevel {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    }
}
