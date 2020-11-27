package com.fintek.upload_sdk.network

import com.fintek.upload_sdk.model.BaseResponse
import com.fintek.upload_sdk.model.UnitResponse
import com.fintek.upload_sdk.model.UserAuthInfo
import com.fintek.upload_sdk.model.UserExtChecked
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Created by ChaoShen on 2020/10/26
 */
interface ApiService {


    /* 获取额外用户信息过期情况 */
    @POST("/api/auth/check-ext-expired")
    suspend fun fetchUserExtExpired(): BaseResponse<UserExtChecked>

    /* 上传额外用户信息 */
    @POST("/api/auth/ext-info")
    suspend fun postExtInfo(@Body requestBody: RequestBody): UnitResponse
}