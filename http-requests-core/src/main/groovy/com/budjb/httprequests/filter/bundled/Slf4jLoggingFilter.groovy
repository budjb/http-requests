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
package com.budjb.httprequests.filter.bundled

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * A {@link LoggingFilter} implementation that logs the HTTP conversation to an SLF4J logger.
 */
class Slf4jLoggingFilter extends LoggingFilter {
    /**
     * Logger level.
     */
    static enum LoggerLevel {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

    /**
     * Logger.
     */
    Logger logger

    /**
     * Log level to log with.
     */
    LoggerLevel loggerLevel

    /**
     * Constructor.
     */
    Slf4jLoggingFilter() {
        setLoggerName(getClass().getName(), LoggerLevel.TRACE)
    }

    /**
     * Sets the logger name.
     *
     * @param name Name of the logger.
     */
    void setLoggerName(String name, LoggerLevel loggerLevel) {
        if (name) {
            logger = LoggerFactory.getLogger(name)
        }
        if (loggerLevel) {
            this.loggerLevel = loggerLevel
        }
    }

    /**
     * Logs the given content to the {@link #logger} with the configured {@link #loggerLevel}.
     *
     * @param content Content of the HTTP request and response..
     */
    protected void log(String content) {
        switch (loggerLevel) {
            case LoggerLevel.TRACE:
                if (logger.isTraceEnabled()) {
                    logger.trace(content)
                }
                break

            case LoggerLevel.DEBUG:
                if (logger.isDebugEnabled()) {
                    logger.debug(content)
                }
                break

            case LoggerLevel.INFO:
                if (logger.isInfoEnabled()) {
                    logger.info(content)
                }
                break

            case LoggerLevel.WARN:
                if (logger.isWarnEnabled()) {
                    logger.warn(content)
                }
                break

            case LoggerLevel.ERROR:
                if (logger.isErrorEnabled()) {
                    logger.error(content)
                }
        }
    }
}
