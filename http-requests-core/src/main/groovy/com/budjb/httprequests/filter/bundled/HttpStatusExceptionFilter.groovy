package com.budjb.httprequests.filter.bundled

import com.budjb.httprequests.HttpContext
import com.budjb.httprequests.exception.HttpStatusException
import com.budjb.httprequests.filter.HttpClientLifecycleFilter

/**
 * A filter that throws an exception specific to an HTTP status if that status is not in the 200-299 range.
 */
class HttpStatusExceptionFilter implements HttpClientLifecycleFilter {
    @Override
    void onComplete(HttpContext context) {
        if (context.getResponse().getStatus() >= 300) {
            throw HttpStatusException.build(context.getResponse())
        }
    }
}
