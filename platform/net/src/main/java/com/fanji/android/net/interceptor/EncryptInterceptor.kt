package com.fanji.android.net.interceptor

import com.fanji.android.util.LogUtil
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-16 上午10:45
 */
class EncryptInterceptor(
    private val encoder: Encoder? = null,
    private val encrypt: Encrypt? = null
) :
    AbstractInterceptor(encoder, encrypt) {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val body = request.body
        if (body != null) {
            LogUtil.e("-----------length:", body.contentLength())
            val bodyBuffer = okio.Buffer()
            body.writeTo(bodyBuffer)
            val bodyContent = bodyBuffer.readByteArray()
            val encryptedBody =
                RequestBody.create(body.contentType(),
                    encoder!!.encode(encrypt!!.encrypt(bodyContent))!!
                )
            return chain.proceed(request.newBuilder().post(encryptedBody).build())
        }
        return chain.proceed(request)
    }
}