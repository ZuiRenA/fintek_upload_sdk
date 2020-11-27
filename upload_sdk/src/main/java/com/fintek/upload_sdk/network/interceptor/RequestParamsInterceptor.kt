package com.fintek.upload_sdk.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by ChaoShen on 2020/10/26
 */
class RequestParamsInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val httpUrl = request.url
        val url = httpUrl.newBuilder().build()
        val builder = request.newBuilder().url(url)
        return chain.proceed(builder.build())
    }
}