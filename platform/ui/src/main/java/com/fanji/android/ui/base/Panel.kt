package com.fanji.android.ui.base

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.fanji.android.img.view.transferee.loader.GlideImageLoader
import com.fanji.android.img.view.transferee.transfer.TransferConfig
import com.fanji.android.img.view.transferee.transfer.Transferee
import com.fanji.android.ui.FJTipsView
import com.fanji.android.ui.FJTopView
import com.fanji.android.ui.refresh.FJRefresh
import com.fanji.android.ui.refresh.footer.ClassicsFooter
import com.fanji.android.ui.refresh.header.MaterialHeader
import com.fanji.android.ui.refresh.listener.OnLoadMoreListener
import com.fanji.android.ui.refresh.listener.OnRefreshListener
import com.fanji.android.util.AppUtil

/**
 * @author: jiangshide
 * @date: 2023/9/10
 * @email: 18311271399@163.com
 * @description:
 */
open class Panel {

    var mIsRefresh = false
    private var mIsMore = false
    var mIsTips = false
    private var mBgColor = 0
    private var mIsTitle = false
    var mIsTopPadding = false

    var mLeftBtn: Any? = null
    private var mTitle: Any? = null
    private var mRightBtn: Any? = null
    var topView: FJTopView? = null
    var refresh: FJRefresh? = null
    var tipsView: FJTipsView? = null

    private var transferee: Transferee? = null

    public fun initView(
        isRefresh: Boolean = false,
        isMore: Boolean = false,
        isTips: Boolean = false,
        bgColor: Int = -1,
        isTitle: Boolean = false,
        leftBtn: Any? = null,
        title: Any? = null,
        rightBtn: Any? = null,
        isTopPadding: Boolean = false
    ) {
        this.mIsRefresh = isRefresh
        this.mIsMore = isMore
        this.mIsTips = isTips
        this.mBgColor = bgColor
        this.mIsTitle = isTitle
        this.mLeftBtn = leftBtn
        this.mTitle = title
        this.mRightBtn = rightBtn
        this.mIsTopPadding = isTopPadding
    }

    fun view(
        context: Context, view: View?, refreshListener: OnRefreshListener,
        onLoadMoreListener: OnLoadMoreListener
    ): View? {
        return view(
            context, view, mIsRefresh,
            mIsMore,
            mIsTips,
            mBgColor,
            mIsTitle, mTitle, refreshListener,
            onLoadMoreListener
        )
    }

    public fun view(
        context: Context,
        view: View?,
        isRefresh: Boolean,
        isMore: Boolean,
        isTips: Boolean,
        bgColor: Int,
        refreshListener: OnRefreshListener,
        onLoadMoreListener: OnLoadMoreListener
    ): View {
        val frameLayout = FrameLayout(context)
        if (bgColor != -1) {
            frameLayout.setBackgroundResource(bgColor)
        }
        if (!isRefresh && !isMore && isTips) {
            frameLayout.addView(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            tipsView = FJTipsView(context)
            frameLayout.addView(tipsView)
            return frameLayout
        }
        refresh = FJRefresh(context)
        refresh?.setOnRefreshListener(refreshListener)?.setOnLoadMoreListener(onLoadMoreListener)
            ?.setEnableRefresh(isRefresh)?.setEnableLoadMore(isMore)
            ?.setRefreshHeader(MaterialHeader(context))
            ?.setRefreshFooter(ClassicsFooter(context))
        refresh?.addView(
            view, ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        frameLayout.addView(refresh)
        tipsView = FJTipsView(context)
        frameLayout.addView(tipsView)
        return frameLayout
    }

    fun view(
        context: Context,
        view: View?,
        isRefresh: Boolean,
        isMore: Boolean,
        isTips: Boolean,
        bgColor: Int,
        isTitle: Boolean,
        title: Any?,
        refreshListener: OnRefreshListener,
        onLoadMoreListener: OnLoadMoreListener
    ): View? {
        if (!isRefresh && !isMore && !isTips && !isTitle && title == null) {
            return view
        }
        if (!isTitle && title == null) {
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
        if (bgColor != -1) {
            root.setBackgroundResource(bgColor)
        }
        root.orientation = LinearLayout.VERTICAL
        if (topView != null) {
            root.removeView(topView)
        }
        topView = FJTopView(context)
        if (mTitle != null || isTitle) {
            topView?.setTitle(mTitle)
            mIsTopPadding = true
        }
        if (mLeftBtn != null) {
            topView?.setLefts(mLeftBtn)
        }
        if (mRightBtn != null) {
            topView?.setRights(mRightBtn)
        }
        root.addView(
            topView, LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        root.addView(
            view(
                context,
                view,
                isRefresh,
                isMore,
                isTips,
                -1,
                refreshListener,
                onLoadMoreListener
            ),
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        return root
    }

    fun viewImg(context: Context, urls: List<String?>, index: Int = 0) {
        if (transferee == null) {
            transferee = Transferee.getDefault(context)
        }
        transferee?.apply(
            TransferConfig.build()
                .setImageLoader(GlideImageLoader.with(AppUtil.getApplicationContext()))
                .setNowThumbnailIndex(index)
                .setSourceImageList(urls)
                .create()
        )?.show()
    }
}