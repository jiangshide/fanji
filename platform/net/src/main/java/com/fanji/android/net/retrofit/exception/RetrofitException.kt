package com.fanji.android.net.retrofit.exception

/**
 * created by jiangshide on 5/28/21.
 * email:18311271399@163.com
 */
class RetrofitException : RuntimeException {
    constructor(detailMessage: String?) : super(detailMessage) {}

    constructor(throwable: Throwable?) : super(throwable) {}

    constructor(detailMessage: String?, throwable: Throwable?) : super(detailMessage, throwable) {}

    companion object {
        private const val serialVersionUID = -2912559384646531479L
    }
}