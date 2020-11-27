package com.fintek.upload_sdk

import android.content.Context
import com.fintek.utils_androidx.FintekUtils

/**
 * Created by ChaoShen on 2020/11/27
 */
object UploadUtils {
    private var context: Context? = null

    internal val requiredContext get() = checkNotNull(context) { "UploadUtils init first" }

    fun init(context: Context) {
        this.context = context
        FintekUtils.init(context)
    }
}