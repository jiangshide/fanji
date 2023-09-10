package com.fanji.android.resource.base

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.fanji.android.ui.FJTipsView
import com.fanji.android.ui.FJTopView
import com.fanji.android.ui.refresh.FJRefresh
import com.fanji.android.ui.refresh.footer.ClassicsFooter
import com.fanji.android.ui.refresh.header.MaterialHeader
import com.fanji.android.ui.refresh.listener.OnLoadMoreListener
import com.fanji.android.ui.refresh.listener.OnRefreshListener

/**
 * @author: jiangshide
 * @date: 2023/9/10
 * @email: 18311271399@163.com
 * @description:
 */
open class Panel<T> {

    var mIsRefresh = false
    private var mIsMore = false
    var mIsTips = false
    private var mBgColor = 0
    private var mIsTitle = false
    var mIsTopPadding = false

    var topView: FJTopView? = null
    var refresh: FJRefresh? = null
    var tipsView: FJTipsView? = null

    public fun initView(
        t: T, isRefresh: Boolean = false,
        isMore: Boolean = false,
        isTips: Boolean = false,
        bgColor: Int = com.fanji.android.ui.R.color.white,
        isTitle: Boolean = false,
        isTopPadding: Boolean = false
    ): T {
        this.mIsRefresh = isRefresh
        this.mIsMore = isMore
        this.mIsTips = isTips
        this.mBgColor = bgColor
        this.mIsTitle = isTitle
        this.mIsTopPadding = isTopPadding
        return t
    }

    fun view(
        context: Context, view: View, refreshListener: OnRefreshListener,
        onLoadMoreListener: OnLoadMoreListener
    ): View {
        return view(
            context, view, mIsRefresh,
            mIsMore,
            mIsTips,
            mBgColor,
            mIsTitle, refreshListener,
            onLoadMoreListener
        )
    }

    public fun view(
        context: Context,
        view: View,
        isRefresh: Boolean,
        isMore: Boolean,
        isTips: Boolean,
        bgColor: Int,
        refreshListener: OnRefreshListener,
        onLoadMoreListener: OnLoadMoreListener
    ): View {
        val frameLayout = FrameLayout(context)
        if (bgColor != -1) {
//            frameLayout.setBackgroundColor(color(bgColor))
        }
        if (!isRefresh && !isMore && isTips) {
            frameLayout.addView(view)
            tipsView = FJTipsView(context)
            frameLayout.addView(tipsView)
            return frameLayout
        }
        refresh = FJRefresh(context)
        refresh?.setOnRefreshListener(refreshListener)?.setOnLoadMoreListener(onLoadMoreListener)
            ?.setEnableRefresh(isRefresh)?.setEnableLoadMore(isMore)
            ?.setRefreshHeader(MaterialHeader(context))
            ?.setRefreshFooter(ClassicsFooter(context))
        refresh?.addView(view)
        frameLayout.addView(refresh)
        tipsView = FJTipsView(context)
        frameLayout.addView(tipsView)
        return frameLayout
    }

    fun view(
        context: Context,
        view: View,
        isRefresh: Boolean,
        isMore: Boolean,
        isTips: Boolean,
        bgColor: Int = com.fanji.android.ui.R.color.white,
        isTitle: Boolean,
        refreshListener: OnRefreshListener,
        onLoadMoreListener: OnLoadMoreListener
    ): View {
        if (!isRefresh && !isMore && !isTips && !isTitle) {
            return view
        }
        if (!isTitle) {
            return view(
                context,
                view,
                isRefresh,
                isMore,
                isTips,
                bgColor,
                refreshListener,
                onLoadMoreListener
            )
        }
        val root = LinearLayout(context)
        root?.isClickable = true
        root.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
//        root.setBackgroundColor(color(bgColor))
        root.orientation = LinearLayout.VERTICAL
        if (topView != null) {
            root.removeView(topView)
        }
        topView = FJTopView(context)
        root.addView(
            topView, LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        root.addView(
            view(context, view, isRefresh, isMore, isTips, -1, refreshListener, onLoadMoreListener),
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        return root
    }
}