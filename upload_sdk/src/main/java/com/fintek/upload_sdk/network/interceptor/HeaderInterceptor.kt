package com.fintek.upload_sdk.network.interceptor

import com.fintek.upload_sdk.UploadUtils
import com.fintek.utils_androidx.app.AppUtils
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by ChaoShen on 2020/10/26
 */
class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        builder.addHeader("x-auth-token", UploadUtils.requiredConfig.userToken)
        builder.addHeader("x-merchant", UploadUtils.requiredConfig.merchant)
        // 配置版本信息
        builder.addHeader("x-version", AppUtils.getAppVersionName())
        builder.addHeader("x-package-name", UploadUtils.requiredContext.packageName)
        return chain.proceed(builder.build())
    }
}