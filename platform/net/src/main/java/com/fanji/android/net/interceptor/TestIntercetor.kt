package com.fanji.android.net.interceptor

import com.fanji.android.util.LogUtil
import okhttp3.Interceptor
import okhttp3.Response

/**
 * created by jiangshide on 12/3/20.
 * email:18311271399@163.com
 */
class TestIntercetor:Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val build = request.newBuilder()
        build.addHeader("jsd","the jankey!!")
        val res = chain.proceed(request)
        LogUtil.e("--------jsd~res:",res)
        return res
    }
}