package com.fintek.upload_sdk

import android.content.Context
import com.fintek.upload_sdk.model.Config
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.device.DeviceUtils

/**
 * Created by ChaoShen on 2020/11/27
 */
object UploadUtils {
    private const val SHARED_PREF_PATH: String = "sdk_sharedPreferences"

    private var context: Context? = null
    private var config: Config? = null

    internal val requiredContext get() = checkNotNull(context) { "UploadUtils init first" }
    internal val requiredConfig get() = checkNotNull(config)
    internal val sharedPreferences get() =
        requiredContext.getSharedPreferences(SHARED_PREF_PATH, Context.MODE_PRIVATE)

    fun init(context: Context) {
        this.context = context
        FintekUtils.init(context)

        DeviceUtils.getGaid(object : FintekUtils.Consumer<String> {
            override fun accept(gaid: String) {
                sharedPreferences.edit()
                    .putString("gaid", gaid)
                    .apply()
            }
        })
    }

    fun setConfig(config: Config) {
        this.config = config
    }
}