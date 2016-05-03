package com.budjb.httprequests.filter

import com.budjb.httprequests.HttpContext

trait HttpClientLifecycleFilter {
    void onRequest(HttpContext context, OutputStream outputStream) {

    }

    void onResponse(HttpContext context) {

    }
}