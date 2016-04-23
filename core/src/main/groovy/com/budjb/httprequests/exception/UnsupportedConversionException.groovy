package com.budjb.httprequests.exception

class UnsupportedConversionException extends Exception {
    UnsupportedConversionException(Class<?> type) {
        super("no converter is available to convert the class type '${type.getName()}")
    }
}
