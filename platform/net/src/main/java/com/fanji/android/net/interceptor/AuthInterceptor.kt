package com.fanji.android.net.interceptor

import android.content.Context
import android.text.TextUtils
import com.fanji.android.net.Net
import com.fanji.android.net.state.NetState
import com.fanji.android.util.LogUtil
import okhttp3.Interceptor
import okhttp3.Response

/**
 * created by jiangshide on 2019-07-31.
 * email:18311271399@163.com
 */
class AuthInterceptor(private val context: Context?) : Interceptor {
    private val appVersionName = ""
    private val androidId = ""

    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        val builder = originRequest.newBuilder()

        var token:String? = Net.token
        LogUtil.e("token:", token)
//      builder.addHeader("X-Token", token)
        builder.addHeader("Authorization", token?.replace("\"", "").toString())
//      builder.addHeader("Authorization", "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjo1MywidXNlcl90eXBlIjoiVXNlciIsInN5c3RlbSI6ImlzZWN1cml0aWVzIiwiZXhwIjoxNTc5ODM0MDAxfQ.c7Npy_H1m7g2Qi1FOB8zv9ingxmH2J8E7ZnMEk71ClE")

        builder.addHeader("net", "${NetState.instance.isAvailable(context)}")
        builder.addHeader("lat", "${Net.mLat}")
        builder.addHeader("lng", "${Net.mLng}")

        val uid = ""
        val urlBuilder = originRequest.url
            .newBuilder()
        if (!TextUtils.isEmpty(uid)) {
            urlBuilder.addQueryParameter("uid", uid)
        }
//    urlBuilder.addQueryParameter("appVersion", appVersionName)
//    urlBuilder.addQueryParameter("platform", "Android")
//    urlBuilder.addQueryParameter("deviceId", androidId)
//    urlBuilder.addQueryParameter("zone", DateUtil.getGmtTimeZone())
//    urlBuilder.addQueryParameter("model", android.os.Build.MODEL)
        builder.url(urlBuilder.build())
        val request = builder.build()
        LogUtil.e("-------headers:", request.headers)
        return chain.proceed(request)
    }
}