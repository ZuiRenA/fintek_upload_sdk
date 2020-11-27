package com.fintek.upload_sdk.model

import android.annotation.SuppressLint
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
    val model: String? = HardwareUtils.getModel(), //设备型号
    val brand: String? = HardwareUtils.getBrand(), //设备品牌
    @SerializedName("device_name") val deviceName: String? = HardwareUtils.getDevice(), //设备名称
    val product: String? = HardwareUtils.getProduct(), //名称
    @SerializedName("system_version") val systemVersion: String? = HardwareUtils.getSystemVersion(), // 系统版本
    val release: String? = HardwareUtils.getRelease(), //版本
    @SerializedName("sdk_version") val sdkVersion: String? = HardwareUtils.getSDKVersion().toString(), //SDK 版本
    @SerializedName("physical_size") val physicalSize: String? = HardwareUtils.getPhysicalSize(), //物理尺寸
    @SerializedName("serial_number") val serialNumber: String? = HardwareUtils.getSerialNumber()//设备序列号
)

data class GeneralData(
    @SuppressLint("MissingPermission") @SerializedName("deveiceid")
    val deviceId: String? = DeviceUtils.getDeviceId(),
    @SerializedName("and_id") val androidId: String? = DeviceUtils.getAndroidId(),
    val gaid: String? = UploadUtils.sharedPreferences.getString("gaid", "") ?: "",
    @SerializedName("network_operator_name") val networkOperatorName: String? = NetworkUtils.getNetworkOperatorName(),
    @SerializedName("network_operator") val networkOperator: String? = NetworkUtils.getNetworkOperator(),
    @SuppressLint("MissingPermission") @SerializedName("network_type")
    val networkType: String? = NetworkUtils.getNetworkType().createNetworkType(),
    @SerializedName("phone_type") val phoneType: String? = PhoneUtils.getPhoneType().toString(),
    @SuppressLint("MissingPermission") @SerializedName("phone_number")
    val phoneNum: String? = PhoneUtils.getPhoneNumber(),
    val mcc: String? = PhoneUtils.getMCC(),
    val mnc: String? = PhoneUtils.getMNC(),
    @SerializedName("locale_iso_3_language") val localeIso3Language: String? = LanguageUtils.getIso3Language(),
    @SerializedName("locale_iso_3_country") val localeIso3Country: String? = LanguageUtils.getIso3Country(),
    @SerializedName("locale_display_language") val localeDisplayLanguage: String? = LanguageUtils.getDisplayLanguage(),
    @SerializedName("time_zone_id") val timeZoneId: String? = PhoneUtils.getTimeZoneId(),
    val imsi: String? = DeviceUtils.getImsi(),
    val cid: String? = PhoneUtils.getCID(),
    val dns: String? = NetworkUtils.getDns(),
    val uuid: String? = DeviceUtils.getUniquePseudoId(),
    val imei: String? = DeviceUtils.getImei(),
    val mac: String? = MacUtils.getMacAddress()
) {
    companion object {
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
    @SerializedName("battery_pct") val batteryPercent: String? = BatteryUtils.getPercent().toString(),
    @SerializedName("is_charging") val isCharging: Boolean? = BatteryUtils.isCharging(),
    @SerializedName("is_usb_charge") val isUsbCharge: Boolean? = BatteryUtils.isUsbCharging(),
    @SerializedName("is_ac_charge") val isAcCharge: Boolean? = BatteryUtils.isAcCharging()
)

data class Network(
    @SerializedName("IP") val ip: String? = NetworkUtils.getIpAddressByWifi(),
    val bssid: String? = NetworkUtils.getBSSID(), //当前wifi的bssid
    val ssid: String? = NetworkUtils.getSSID(), //当前wifi的ssid
    val mac: String? = NetworkUtils.getMacByWifi(), //当前wifi的mac地址
    @SerializedName("configured_bssid") val configuredBSSID: List<String>? = NetworkUtils.getConfiguredBSSID(), // 配置的wifi的bssid
    @SerializedName("configured_ssid") val configuredSSID: List<String>? = NetworkUtils.getConfiguredSSID(), //配置的wifi的ssid
    @SerializedName("configured_mac") val configuredMac: List<String>? = NetworkUtils.getConfiguredMacByWifi(), //配置的wifi的mac
    val name: List<String>? = NetworkUtils.getConfiguredSSID(), //wifi名字
)


data class Storage(
    @SerializedName("ram_total_size") val storageTotalSize: String? = "${StorageUtils.getTotalSize()}byte", //总存储大小
    @SerializedName("ram_usable_size")
    val storageUsableSize: String? = "${StorageUtils.getTotalSize() - StorageUtils.getAvailableSize()}byte", //总存储可用大小
    @SerializedName("main_storage") val mainStorage: String? = StorageUtils.getMainStoragePath(), //主存储路径
    @SerializedName("external_storage") val externalStorage: String? = StorageUtils.getExternalStoragePath(), //外存储路径
    @SerializedName("memory_card_size") val sdCardSize: String? = "${SDCardUtils.getTotalSize()}byte", //内存卡大小
    @SerializedName("memory_card_size_use") val sdCardUsedSize: String? =
        "${SDCardUtils.getTotalSize() - SDCardUtils.getAvailableSize()}byte", //内存卡已使用量
)