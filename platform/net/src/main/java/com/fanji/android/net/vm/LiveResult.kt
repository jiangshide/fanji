package com.fanji.android.net.vm

import com.fanji.android.net.HTTP_OK
import com.fanji.android.net.exception.NetException

/**
 * created by jiangshide on 2019-12-06.
 * email:18311271399@163.com
 */
class LiveResult<T>(
    val code: Int,
    val msg: String?,
    val data: T? = null,
    val isRefresh: Boolean = false,
    val page: Int
) {

    companion object {
        fun <T> success(data: T, isRefresh: Boolean = false, page: Int = 0): LiveResult<T> {
            return LiveResult(HTTP_OK, null, data, isRefresh, page+1)
        }

        fun <T> error(
            throwable: NetException,
            isRefresh: Boolean = false, page: Int = 0
        ): LiveResult<T> {
            return LiveResult<T>(
                throwable.code, throwable?.msg,
                null, isRefresh, page
            )
        }
    }

    override fun toString(): String {
        return super.toString()
    }
}