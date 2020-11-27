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
            parameter["remoteAddr"] = getIp()
            parameter["storageTotalSize"] = "${StorageUtils.getTotalSize()}byte"
            parameter["storageAdjustedTotalSize"] = "${getTotalInternalMemorySize()}Byte"
            parameter["storageAvailableSize"] = "${StorageUtils.getAvailableSize()}Byte"
            parameter["sdCardTotalSize"] = SDCardUtils.getTotalSize().toString()
            parameter["sdCardAvailableSize"] = SDCardUtils.getAvailableSize().toString()
            parameter["imsi"] = DeviceUtils.getImsi() ?: ""
            parameter["isRoot"] = DeviceUtils.isRoot().toString()
            parameter["isLocServiceEnable"] = LocationUtils.isLocationServiceEnable().toString()
            parameter["isNetwork"] = NetworkUtils.isConnected().toString()
            parameter["language"] = LanguageUtils.getCurrentLocale()?.language ?: ""
            parameter["hardware"] = Hardware().toJson()
            parameter["generalData"] = GeneralData().toJson()
            parameter["battery"] = Battery().toJson()
            parameter["network"] = Network().toJson()
            parameter["storage"] = Storage().toJson()
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (t: Throwable) {
            t.printStackTrace()
        }

        return parameter
    }

    /**
     * 获取公网ip地址
     */
    private fun getIp(): String {
        val publicNetIp: String = ""
        if ("" != publicNetIp) {
            return publicNetIp
        }
        val wifiIp = getWifiIp()
        val gprsIP = getGprsIp()
        var ip = "0.0.0.0"
        if (wifiIp != 0) {
            ip = wifiIp.toIp()
        } else if (!TextUtils.isEmpty(gprsIP)) {
            ip = gprsIP
        }
        return ip
    }

    /**
     * 获取wifi ip
     */
    private fun getWifiIp(): Int { // 获取wifi服务
        val wifiManager =
            UploadUtils.requiredContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        // 判断wifi是否开启
        if (!wifiManager.isWifiEnabled) {
            //wifiManager.setWifiEnabled(true);
            return 0
        }
        val wifiInfo = wifiManager.connectionInfo
        return wifiInfo.ipAddress
    }

    /**
     * 获取gprs ip
     */
    private fun getGprsIp(): String {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val networkInterface = en.nextElement()
                val addresses =
                    networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val inetAddress = addresses.nextElement()
                    if (!inetAddress.isLoopbackAddress) {
                        return inetAddress.hostAddress.toString()
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun getTotalInternalMemorySize(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSize.toLong()
        val totalBlocks = stat.blockCount.toLong()
        return totalBlocks * blockSize
    }

    private fun Int.toIp(): String = (this and 0xFF).toString() + "." +
            (this shr 8 and 0xFF) + "." +
            (this shr 16 and 0xFF) + "." +
            (this shr 24 and 0xFF)

    private fun Any?.toJson(): String = Gson().toJson(this)
}