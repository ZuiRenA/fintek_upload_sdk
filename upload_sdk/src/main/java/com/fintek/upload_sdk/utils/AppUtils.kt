package com.fintek.upload_sdk.utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import com.fintek.upload_sdk.model.UserAuthInfo
import com.fintek.upload_sdk.model.UserAuthInfo.AppInfo.Companion.isSystemApp

/**
 * Created by ChaoShen on 2020/11/27
 */
object AppUtils {

    fun getAllApk(context: Context): List<UserAuthInfo.AppInfo>? = try {
        val appBeanList = mutableListOf<UserAuthInfo.AppInfo>()
        val packageManager = context.packageManager
        val packageInfoList = packageManager.getInstalledPackages(0)
        packageInfoList.forEach {
            appBeanList.add(UserAuthInfo.AppInfo(
                appName = packageManager.getApplicationLabel(it.applicationInfo).toString(),
                packageName = it.applicationInfo.packageName,
                installTime = it.firstInstallTime.toString(),
                updateTime = it.lastUpdateTime.toString(),
                versionName = it.versionName,
                versionCode = it.versionCode.toString(),
                flags = it.applicationInfo.flags.toString(),
                appType = if (isSystemApp(it.applicationInfo.flags)) "1" else "0"
            ))
        }
        appBeanList
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }


    @TargetApi(Build.VERSION_CODES.R)
    @RequiresPermission(android.Manifest.permission.QUERY_ALL_PACKAGES)
    fun queryAllPackage(context: Context) {

    }
}