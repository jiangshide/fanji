package com.fanji.android.ui.vm

import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import com.fanji.android.net.Net
import com.fanji.android.net.vm.LiveResult
import com.fanji.android.ui.FJTipsView
import com.fanji.android.util.EncryptUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * created by jiangshide on 2019-08-14.
 * email:18311271399@163.com
 */
@Keep
open class FJVM : ViewModel() {
    protected var net: Net? = null
    private var mFjTipsView: FJTipsView? = null

    val uiScope = CoroutineScope(Dispatchers.Main)

    init {
        if (null == net) net = Net
    }

    fun loading(fjTipsView: FJTipsView?) {
        mFjTipsView = fjTipsView
        mFjTipsView!!.setStatus(true, true, false)
    }

    fun finishLoading() {
        if (mFjTipsView != null) {
            mFjTipsView!!.setStatus(false, false, false)
        }
    }

    fun md5(psw: String?): String {
        return EncryptUtil.encryptMd5(psw)
    }

    interface VMListener<T> {
        fun onRes(res: LiveResult<T>)
    }
}