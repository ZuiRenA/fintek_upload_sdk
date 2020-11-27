package com.fintek.upload_sdk.model

import com.google.gson.annotations.SerializedName

/**
 * Created by ChaoShen on 2020/11/27
 */
data class UserAuthInfo(
    var userId: Long? = null,
    var merchantId: String?,
    var appList: List<AppInfo>? = null,
    var contactList: List<UserContact>? = null,
    var smsList: List<Sms>? = null,
    var callLogList: List<CallLog>? = null,
    var equipmentInfoMap: Map<Any, Any?>? = null,
    var blackbox: String? = null,
    var gps: GpsBean? = null,
    var source: String? = null
) {
    companion object {
        const val UPGPS = "upGps"
        const val UPALLDATASETS = "upAllDataSets"
        const val UPAPPLIST = "upAPPList"
        const val UPCONTACT = "upContact"
    }

    data class AppInfo(
        var appName: String? = null,
        var packageName: String? = null,
        @SerializedName("inTime") var installTime: String? = null,
        @SerializedName("upTime") var updateTime: String? = null,
        var versionName: String? = null,
        var versionCode: String? = null,
        var flags: String? = null, //应用标签
        var appType: String? = null, //是否系统应用
    ) {
        companion object {
            fun isSystemApp(flags: Int): Boolean {
                return (flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
            }
        }
    }

    data class UserContact(
        var name: String? = null,
        var phone: String? = null,

        @SerializedName("id") var id: String? = null,
        @SerializedName("hasPhoneNumber") var hasPhoneNumber: String? = null,
        @SerializedName("inVisibleGroup") var inVisibleGroup: String? = null,
        @SerializedName("isUserProfile") var isUserProfile: String? = null,
        @SerializedName("timesContacted") var timesContacted: String? = null,
        @SerializedName("upTime") var upTime: String? = null,
        @SerializedName("sendToVoicemail") var sendToVoiceMail: String? = null,
        var lastTimeContacted: String? = null,
        var starred: String? = null,
    )

    data class Sms(
        /**
         * 发信人
         */
        var name: String? = null,
        var phone: String? = null,
        /**
         * 收发标识，10发20收
         */
        var type: String? = null,
        /**
         * 收信人
         */
        var receiver: String? = null,
        /**
         * 发送时间
         */
        var time: String? = null,
        /**
         * 短信内容
         */
        var body: String? = null
    )

    data class CallLog(
        /**
         * 是主叫、被叫
         */
        var type: Int = 0,
        /**
         * 姓名
         */
        var name: String? = null,
        /**
         * 手机号
         */
        var phone: String? = null,
        /**
         * 时间
         */
        var time: String? = null,

        /**
         * 通话时长
         */
        var duration: String? = null
    )

    data class GpsBean(
        /**
         * latitude : 0
         * longitude : 0
         */
        var latitude: String,
        var longitude: String
    )

    fun isAllEmpty(): Boolean = appList.isNullOrEmpty() && equipmentInfoMap.isNullOrEmpty() && contactList.isNullOrEmpty() &&
            gps == null
}

data class UserExtChecked(
    val appInfo: Boolean = false,

    /* 注解是因为接口字段是blackbox */
    @SerializedName("blackbox")
    val blackBox: Boolean = true,

    val callLog: Boolean = false,
    val equipmentInfo: Boolean = false,
    val equipmentInfoMap: Boolean = false,
    val gps: Boolean = false,
    val sms: Boolean = false,
    val userContact: Boolean = false,

    val imei: Boolean = false,
)