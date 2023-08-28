package com.fanji.android.ui.spedit.gif.drawable

import com.fanji.android.ui.spedit.gif.listener.RefreshListener

interface InvalidateDrawable {
    var mRefreshListeners: MutableCollection<RefreshListener>
    fun addRefreshListener(callback: RefreshListener)
    fun removeRefreshListener(callback: RefreshListener)
    fun refresh()
}
