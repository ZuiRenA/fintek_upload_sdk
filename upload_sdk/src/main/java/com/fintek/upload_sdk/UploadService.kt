package com.fintek.upload_sdk

import android.app.IntentService
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

/**
 * Created by ChaoShen on 2020/11/27
 */
class UploadService : IntentService("Upload"), CoroutineScope by MainScope() {
    override fun onHandleIntent(intent: Intent?) {

    }
}