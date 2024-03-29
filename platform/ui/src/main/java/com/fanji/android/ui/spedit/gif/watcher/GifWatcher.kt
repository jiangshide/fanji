package com.fanji.android.ui.spedit.gif.watcher

import android.app.Activity
import android.os.Build
import android.text.SpanWatcher
import android.text.Spannable
import android.view.View
import com.fanji.android.ui.spedit.gif.listener.RefreshListener
import com.fanji.android.ui.spedit.gif.span.RefreshSpan
import java.lang.ref.WeakReference

class GifWatcher(view: View) : SpanWatcher, RefreshListener {
    private var mLastTime: Long = 0
    private val mViewWeakReference: WeakReference<View> = WeakReference(view)

    override fun onSpanAdded(text: Spannable, what: Any, start: Int, end: Int) {
        if (what is RefreshSpan) {
            val drawable = what.getInvalidateDrawable()
            drawable?.addRefreshListener(this)
        }
    }

    override fun onSpanRemoved(text: Spannable, what: Any, start: Int, end: Int) {
        if (what is RefreshSpan) {
            val drawable = what.getInvalidateDrawable()
            drawable?.removeRefreshListener(this)
        }
    }

    override fun onSpanChanged(text: Spannable, what: Any, ostart: Int, oend: Int, nstart: Int, nend: Int) {

    }

    override fun onRefresh(): Boolean {
        val view = mViewWeakReference.get() ?: return false
        val context = view.context
        if (context is Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (context.isDestroyed) {
                    mViewWeakReference.clear()
                    return false
                }
            }
            if (context.isFinishing) {
                mViewWeakReference.clear()
                return false
            }
        }
        val currentTime = System.currentTimeMillis()
        if (currentTime - mLastTime > REFRESH_INTERVAL) {
            mLastTime = currentTime
            view.invalidate()
        }
        return true
    }

    companion object {
        private const val REFRESH_INTERVAL = 60
    }
}
