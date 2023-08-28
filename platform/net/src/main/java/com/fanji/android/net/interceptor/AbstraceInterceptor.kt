package com.fanji.android.net.interceptor

import android.util.Base64
import androidx.annotation.VisibleForTesting
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-16 上午11:06
 */
abstract class AbstractInterceptor(
    private var encoder: Encoder? = null,
    private val encrypt: Encrypt? = null,
    private val encryptionVersion: String? = "101_1_1.0"
) :
    Interceptor {

    init {
        encoder =
            object : Encoder {
                override fun encode(input: ByteArray?): ByteArray? {
                    return Base64.encodeToString(
                        input,
                        Base64.NO_WRAP
                    ).toByteArray()
                }
            }
    }

    private val ENCRYPTION_HEADER = "X-Zse-93"

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }

    /**
     * 加密器
     */
    @VisibleForTesting
    interface Encrypt {
        /**
         * 对指定 byte 数组进行加密
         *
         * @param input 目标 byte 数组
         * @return 加密后的 byte 数组
         */
        fun encrypt(input: ByteArray?): ByteArray?
    }

    /**
     * 编码器
     */
    @VisibleForTesting
    interface Encoder {
        /**
         * 对指定 byte 数组进行编码
         *
         * @param input 目标 byte 数组
         * @return 编码后的 byte 数组
         */
        fun encode(input: ByteArray?): ByteArray?
    }
}