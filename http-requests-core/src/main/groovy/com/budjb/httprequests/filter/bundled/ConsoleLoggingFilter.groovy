package com.budjb.httprequests.filter.bundled

/**
 * A {@link LoggingFilter} implementation the logs the HTTP conversation to the console.
 */
class ConsoleLoggingFilter extends LoggingFilter {
    /**
     * Writes the contents of the conversation to the console.
     *
     * @param content Contents of the conversation.
     */
    @Override
    protected void log(String content) {
        println content
    }
}
