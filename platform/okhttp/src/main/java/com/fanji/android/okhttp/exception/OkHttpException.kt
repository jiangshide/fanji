package com.fanji.android.okhttp.exception

/**
 * @author 蒋世德 @ zhihu inc
 * @email jiangshide@zhihu.com
 * @since 21-5-26 下午3:22
 */
data class OkHttpException(
        val code: Int = -1,
        val msg: String = "invalid",
        val case: Throwable? = null
) : Exception(msg, case)