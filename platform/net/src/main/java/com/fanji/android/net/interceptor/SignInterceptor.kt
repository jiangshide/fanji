package com.fanji.android.net.interceptor

import com.fanji.android.util.LogUtil
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-16 上午10:43
 */
class SignInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val body = request.body
        if (body != null) {
            val length = body.contentLength()
            LogUtil.e("-----------length:", length)
        }
        return chain.proceed(request)
    }
}