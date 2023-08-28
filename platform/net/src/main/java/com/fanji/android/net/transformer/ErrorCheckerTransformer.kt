package com.fanji.android.net.transformer

import android.text.TextUtils
import com.fanji.android.net.BuildConfig
import com.fanji.android.net.HTTP_OK
import com.fanji.android.net.exception.NetException
import com.fanji.android.net.vm.data.RespData
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import retrofit2.Response

/**
 * created by jiangshide on 2019-07-31.
 * email:18311271399@163.com
 */
@Suppress("NULLABLE_TYPE_PARAMETER_AGAINST_NOT_NULL_TYPE_PARAMETER")
class ErrorCheckerTransformer<T : Response<R>?, R : RespData<*>?>
    (private val errorMessage: String?) : ObservableTransformer<T, R> {

    override fun apply(upstream: Observable<T>): ObservableSource<R> {
        return upstream.map { t ->
            var resultMsg: String? = null
            var resultCode = HTTP_OK
            val body: RespData<*>? = t?.body()
            if (!t?.isSuccessful!! || body == null) {
                resultMsg = t.message()
                resultCode = t.code()
            } else if (body.code != BuildConfig.RESP_CODE_VALUE) {
                resultMsg = body.msg
                resultCode = body.code
                if (TextUtils.isEmpty(resultMsg)) {
                    resultMsg = errorMessage
                }
            }
            if (resultCode != HTTP_OK) {
                throw NetException(
                    resultMsg,
                    resultCode
                )
            }
            t.body()
        }
    }
}