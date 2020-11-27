package com.fintek.upload_sdk.network

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.fintek.upload_sdk.UploadUtils
import com.fintek.upload_sdk.network.interceptor.HeaderInterceptor
import com.fintek.upload_sdk.network.interceptor.RequestParamsInterceptor
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * Created by ChaoShen on 2020/5/26
 */
class RetrofitHelper private constructor() {
    companion object {
        private const val TAG = "RetrofitManager -> "

        private val INSTANCE by lazy { RetrofitHelper() }

        private var context: Context? = null

        private var myTrustManager: MyTrustManager? = null

        @Synchronized
        fun getInstance(): RetrofitHelper {
            return INSTANCE
        }

        @Synchronized
        fun <T> getApi(api: Class<T>): T = getInstance().createApi(api)

        fun init(context: Context) {
            Companion.context = context
        }

        private fun initSSLSocketFactory(): SSLSocketFactory? = try {
            myTrustManager = MyTrustManager()
            val sc = SSLContext.getInstance("TLS")
            sc.init(null, arrayOf(myTrustManager), SecureRandom())
            sc.socketFactory
        } catch (e: Exception) {
            null
        }
    }


    fun <T> createApi(api: Class<T>): T {
        val gson = GsonBuilder()
            .setDateFormat("yyy-MM-dd HH:mm:ss")
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(UploadUtils.requiredConfig.baseUrl)
            .client(createClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(api)
    }

    private fun createClient(
        connectTimeout: Long = 60,
        readTimeout: Long = 60,
        writeTimeout: Long = 60,
        timeUnit: TimeUnit = TimeUnit.SECONDS
    ): OkHttpClient {
        val cookieManager = CookieManager().also { it.setCookiePolicy(CookiePolicy.ACCEPT_ALL) }
        val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.e("Retrofit", TAG + message)
            }
        })
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(RequestParamsInterceptor())
            .cookieJar(cookieJar = JavaNetCookieJar(cookieManager))
            .addInterceptor(loggingInterceptor)
            .sslSocketFactory(initSSLSocketFactory()!!, myTrustManager!!)
            .hostnameVerifier { _, _ ->
                true
            }
            .retryOnConnectionFailure(true)
            .connectTimeout(connectTimeout, timeUnit)
            .readTimeout(readTimeout, timeUnit)
            .writeTimeout(writeTimeout, timeUnit)
            .build()
    }

    class MyTrustManager : X509TrustManager {
        @SuppressLint("TrustAllX509TrustManager")
        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

        }

        @SuppressLint("TrustAllX509TrustManager")
        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {

        }

        override fun getAcceptedIssuers(): Array<X509Certificate?> {
            return arrayOfNulls(0)
        }

    }
}