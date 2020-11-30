package com.fintek.upload_sdk.utils

import android.Manifest
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.fintek.upload_sdk.UploadUtils
import com.fintek.upload_sdk.model.*
import com.fintek.utils_androidx.device.DeviceUtils
import com.fintek.utils_androidx.language.LanguageUtils
import com.fintek.utils_androidx.location.LocationUtils
import com.fintek.utils_androidx.mac.MacUtils
import com.fintek.utils_androidx.network.NetworkUtils
import com.fintek.utils_androidx.storage.SDCardUtils
import com.fintek.utils_androidx.storage.StorageUtils
import com.google.gson.Gson
import java.net.NetworkInterface
import java.net.SocketException

/**
 * Created by ChaoShen on 2020/11/27
 */
object EstimateUtils {

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresPermission(anyOf = [Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_NETWORK_STATE])
    fun getEstimateMap(): Map<Any, Any?> {
        val parameter = mutableMapOf<Any, Any?>()

        try {
            parameter["information"] = NetworkUtils.getUserAgent()
            parameter["imei"] = DeviceUtils.getDeviceId() ?: ""
            parameter["imei2"] = DeviceUtils.getUniquePseudoId()
            parameter["gaid"] = UploadUtils.sharedPreferences.getString("gaid", "") ?: ""
            parameter["androidId"] = DeviceUtils.getAndroidId()
            parameter["mac"] = MacUtils.getMacAddress()
            parameter["remoteAddr"] = NetworkUtils.getIpIgnorePublicIp()
            parameter["storageTotalSize"] = "${StorageUtils.getTotalSize()}byte"
            parameter["storageAdjustedTotalSize"] = "${StorageUtils.getAdjustSize()}Byte"
            parameter["storageAvailableSize"] = "${StorageUtils.getAvailableSize()}Byte"
            parameter["sdCardTotalSize"] = SDCardUtils.getTotalSize().toString()
            parameter["sdCardAvailableSize"] = SDCardUtils.getAvailableSize().toString()
            parameter["imsi"] = DeviceUtils.getImsi() ?: ""
            parameter["isRoot"] = DeviceUtils.isRoot().toString()
            parameter["isLocServiceEnable"] = LocationUtils.isLocationServiceEnable().toString()
            parameter["isNetwork"] = NetworkUtils.isConnected().toString()
            parameter["language"] = LanguageUtils.getCurrentLocale().language ?: ""
            parameter["hardware"] = Hardware.create().toJson()
            parameter["generalData"] = GeneralData.create().toJson()
            parameter["battery"] = Battery.create().toJson()
            parameter["network"] = Network.create().toJson()
            parameter["storage"] = Storage.create().toJson()
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (t: Throwable) {
            t.printStackTrace()
        }

        return parameter
    }

    private fun Any?.toJson(): String = Gson().toJson(this)
}