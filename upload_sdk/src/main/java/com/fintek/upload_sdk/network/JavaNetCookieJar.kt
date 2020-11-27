package com.fintek.upload_sdk.network


import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.internal.delimiterOffset
import okhttp3.internal.trimSubstring
import java.io.IOException
import java.net.CookieHandler
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by ChaoShen on 2020/5/26
 */
class JavaNetCookieJar(private val cookieHandler: CookieHandler): CookieJar {
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val headers = emptyMap<String, List<String>>()
        val cookieHeaders: Map<String, List<String>>
        try {
            cookieHeaders = cookieHandler.get(url.toUri(), headers)
        } catch (e: IOException) {
            return emptyList()
        }

        var cookies: MutableList<Cookie>? = null
        cookieHeaders.forEach { (key, value) ->
            if (("Cookie".equals(key, true) || "Cookie2".equals(key, true))
                && value.isNotEmpty()) {
                value.forEach { header ->
                    if (cookies == null) {
                        cookies = mutableListOf()
                    }

                    cookies?.addAll(decodeHeaderAsJavaNetCookies(url, header))
                }
            }
        }

        return if (cookies != null) Collections.unmodifiableList(cookies!!) else emptyList()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val cookieStrings: List<String> = ArrayList<String>(cookies.map { it.toString() })
        val multiMap = Collections.singletonMap("Set-Cookie", cookieStrings)
        try {
            cookieHandler.put(url.toUri(), multiMap)
        } catch (e: IOException) {
        }
    }

    private fun decodeHeaderAsJavaNetCookies(url: HttpUrl, header: String): List<Cookie> {
        val result = arrayListOf<Cookie>()
        var pos = 0; val limit = header.length; var pairEnd: Int
        while (pos < limit) {
            pairEnd = header.delimiterOffset(";,", pos, limit)
            val equalsSign: Int = header.delimiterOffset('=', pos, pairEnd)
            val name = header.trimSubstring(pos, equalsSign)
            if (name.startsWith("$")) {
                pos = pairEnd + 1
                continue
            }

            // We have either name=value or just a name.
            var value =
                if (equalsSign < pairEnd) header.trimSubstring(equalsSign + 1, pairEnd) else ""

            // If the value is "quoted", drop the quotes.
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length - 1)
            }
            result.add(
                Cookie.Builder()
                    .name(name)
                    .value(value)
                    .domain(url.host)
                    .build()
            )
            pos = pairEnd + 1
        }
        return result
    }
}