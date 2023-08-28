package com.fanji.android.net.netprobe.data

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午1:42
 */
internal data class NetHealthData(
    val validUtil: Long,
    val value: Float
) {
    fun isValid(): Boolean {
        return System.currentTimeMillis() < this.validUtil
    }
}