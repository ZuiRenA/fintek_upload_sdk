package com.fintek.supermarket_utils

import android.app.Application
import com.fintek.upload_sdk.UploadUtils

/**
 * Created by ChaoShen on 2020/11/30
 */
class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        UploadUtils.init(this)
    }
}