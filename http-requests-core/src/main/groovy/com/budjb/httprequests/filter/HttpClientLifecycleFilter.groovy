package com.budjb.httprequests.filter

import com.budjb.httprequests.HttpContext

trait HttpClientLifecycleFilter implements HttpClientFilter {
    void onRequest(HttpContext context, OutputStream outputStream) {

    }

    void onResponse(HttpContext context) {

    }

    void onComplete(HttpContext context) {

    }
}