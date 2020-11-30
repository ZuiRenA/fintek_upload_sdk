package com.fintek.upload_sdk.model

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.fintek.upload_sdk.UploadUtils
import com.fintek.utils_androidx.battery.BatteryUtils
import com.fintek.utils_androidx.device.DeviceUtils
import com.fintek.utils_androidx.hardware.HardwareUtils
import com.fintek.utils_androidx.language.LanguageUtils
import com.fintek.utils_androidx.mac.MacUtils
import com.fintek.utils_androidx.network.NetworkUtils
import com.fintek.utils_androidx.phone.PhoneUtils
import com.fintek.utils_androidx.storage.SDCardUtils
import com.fintek.utils_androidx.storage.StorageUtils
import com.google.gson.annotations.SerializedName

data class Hardware(
    val model: String?, //设备型号
    val brand: String?, //设备品牌
    @SerializedName("device_name") val deviceName: String?, //设备名称
    val product: String? = HardwareUtils.getProduct(), //名称
    @SerializedName("system_version") val systemVersion: String?, // 系统版本
    val release: String? = HardwareUtils.getRelease(), //版本
    @SerializedName("sdk_version") val sdkVersion: String?, //SDK 版本
    @SerializedName("physical_size") val physicalSize: String?, //物理尺寸
    @SerializedName("serial_number") val serialNumber: String?//设备序列号
) {
    companion object {
        fun create(): Hardware = Hardware(
            model = HardwareUtils.getModel(),
            brand = HardwareUtils.getBrand(),
            deviceName = HardwareUtils.getDevice(),
            systemVersion = HardwareUtils.getSystemVersion(),
            release = HardwareUtils.getRelease(),
            sdkVersion = HardwareUtils.getSDKVersion().toString(),
            physicalSize = HardwareUtils.getPhysicalSize(),
            serialNumber = HardwareUtils.getSerialNumber()
        )
    }
}

data class GeneralData(
    @SerializedName("deveiceid") val deviceId: String?,
    @SerializedName("and_id") val androidId: String?,
    val gaid: String?,
    @SerializedName("network_operator_name") val networkOperatorName: String?,
    @SerializedName("network_operator") val networkOperator: String?,
    @SerializedName("network_type") val networkType: String?,
    @SerializedName("phone_type") val phoneType: String?,
    @SerializedName("phone_number") val phoneNum: String?,
    val mcc: String?,
    val mnc: String?,
    @SerializedName("locale_iso_3_language") val localeIso3Language: String?,
    @SerializedName("locale_iso_3_country") val localeIso3Country: String?,
    @SerializedName("locale_display_language") val localeDisplayLanguage: String?,
    @SerializedName("time_zone_id") val timeZoneId: String?,
    val imsi: String?,
    val cid: String?,
    val dns: String?,
    val uuid: String?,
    val imei: String?,
    val mac: String?
) {
    companion object {
        @SuppressLint("NewApi")
        @RequiresPermission(anyOf = [
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        ])
        fun create(): GeneralData = GeneralData(
            deviceId = DeviceUtils.getDeviceId(),
            androidId = DeviceUtils.getAndroidId(),
            gaid = UploadUtils.sharedPreferences.getString("gaid", "").orEmpty(),
            networkOperatorName = NetworkUtils.getNetworkOperatorName(),
            networkOperator = NetworkUtils.getNetworkOperator(),
            networkType = NetworkUtils.getNetworkType().createNetworkType(),
            phoneType = PhoneUtils.getPhoneType().toString(),
            phoneNum = PhoneUtils.getPhoneNumber(),
            mcc = PhoneUtils.getMCC(),
            mnc = PhoneUtils.getMNC(),
            localeIso3Language = LanguageUtils.getIso3Language(),
            localeIso3Country = LanguageUtils.getIso3Country(),
            localeDisplayLanguage = LanguageUtils.getDisplayLanguage(),
            timeZoneId = PhoneUtils.getTimeZoneId(),
            imsi = DeviceUtils.getImsi(),
            cid = PhoneUtils.getCID(),
            dns = NetworkUtils.getDns(),
            uuid = DeviceUtils.getUniquePseudoId(),
            imei = DeviceUtils.getImei().orEmpty(),
            mac = MacUtils.getMacAddress()
        )

        internal fun NetworkUtils.NetworkType.createNetworkType(): String = when(this) {
            NetworkUtils.NetworkType.NETWORK_ETHERNET -> "ethernet"
            NetworkUtils.NetworkType.NETWORK_WIFI -> "wifi"
            NetworkUtils.NetworkType.NETWORK_5G -> "5g"
            NetworkUtils.NetworkType.NETWORK_4G -> "4g"
            NetworkUtils.NetworkType.NETWORK_3G -> "3g"
            NetworkUtils.NetworkType.NETWORK_2G -> "2g"
            NetworkUtils.NetworkType.NETWORK_UNKNOWN, NetworkUtils.NetworkType.NETWORK_NO -> ""
        }
    }
}

data class Battery(
    @SerializedName("battery_pct") val batteryPercent: String?,
    @SerializedName("is_charging") val isCharging: Boolean?,
    @SerializedName("is_usb_charge") val isUsbCharge: Boolean?,
    @SerializedName("is_ac_charge") val isAcCharge: Boolean?
) {
    companion object {
        fun create(): Battery = Battery(
            batteryPercent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) BatteryUtils.getPercent().toString() else "0",
            isCharging = BatteryUtils.isCharging(),
            isUsbCharge = BatteryUtils.isUsbCharging(),
            isAcCharge = BatteryUtils.isAcCharging()
        )
    }
}

data class Network(
    @SerializedName("IP") val ip: String?,
    val bssid: String?, //当前wifi的bssid
    val ssid: String?, //当前wifi的ssid
    val mac: String?, //当前wifi的mac地址
    @SerializedName("configured_bssid") val configuredBSSID: List<String>?, // 配置的wifi的bssid
    @SerializedName("configured_ssid") val configuredSSID: List<String>?, //配置的wifi的ssid
    @SerializedName("configured_mac") val configuredMac: List<String>?, //配置的wifi的mac
    val name: List<String>?, //wifi名字
) {
    companion object {
        @RequiresPermission(anyOf = [
            Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
        ])
        fun create(): Network = Network(
            ip = NetworkUtils.getIPAddress(true),
            bssid = NetworkUtils.getBSSID(),
            ssid = NetworkUtils.getSSID(),
            mac = NetworkUtils.getMacByWifi(),
            configuredBSSID = NetworkUtils.getConfiguredBSSID(),
            configuredSSID = NetworkUtils.getConfiguredSSID(),
            configuredMac = NetworkUtils.getConfiguredMacByWifi(),
            name = NetworkUtils.getConfiguredSSID()
        )
    }
}


data class Storage(
    @SerializedName("ram_total_size") val storageTotalSize: String? = "${StorageUtils.getTotalSize()}byte", //总存储大小
    @SerializedName("ram_usable_size") val storageUsableSize: String?, //总存储可用大小
    @SerializedName("main_storage") val mainStorage: String?, //主存储路径
    @SerializedName("external_storage") val externalStorage: String?, //外存储路径
    @SerializedName("memory_card_size") val sdCardSize: String?, //内存卡大小
    @SerializedName("memory_card_size_use") val sdCardUsedSize: String?, //内存卡已使用量
) {
    companion object {
        fun create(): Storage = Storage(
            storageTotalSize = "${StorageUtils.getTotalSize()}byte",
            storageUsableSize = "${StorageUtils.getUsedSize()}byte",
            mainStorage = StorageUtils.getMainStoragePath(),
            externalStorage = StorageUtils.getExternalStoragePath(),
            sdCardSize = "${SDCardUtils.getTotalSize()}byte",
            sdCardUsedSize = "${SDCardUtils.getUsedSize()}byte"
        )
    }
}