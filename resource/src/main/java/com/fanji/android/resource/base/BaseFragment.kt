package com.fanji.android.resource.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.fanji.android.resource.R
import com.fanji.android.ui.FJTipsView
import com.fanji.android.ui.FJTopView
import com.fanji.android.ui.files.view.transferee.loader.GlideImageLoader
import com.fanji.android.ui.files.view.transferee.transfer.TransferConfig
import com.fanji.android.ui.files.view.transferee.transfer.Transferee
import com.fanji.android.ui.refresh.FJRefresh
import com.fanji.android.ui.refresh.api.RefreshLayout
import com.fanji.android.ui.refresh.footer.ClassicsFooter
import com.fanji.android.ui.refresh.header.MaterialHeader
import com.fanji.android.ui.refresh.listener.OnLoadMoreListener
import com.fanji.android.ui.refresh.listener.OnRefreshListener
import com.fanji.android.util.AppUtil
import com.fanji.android.util.SystemUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * created by jiangshide on 4/9/21.
 * email:18311271399@163.com
 */
abstract class BaseFragment<T : ViewBinding> : Fragment(), View.OnClickListener, OnRefreshListener,
    OnLoadMoreListener,
    FJTipsView.OnRetryListener {

    private lateinit var _binding: T
    protected val binding get() = _binding
    protected abstract fun viewBinding(inflater: LayoutInflater, container: ViewGroup?): T

    protected fun initView(
        t: T, isRefresh: Boolean = false,
        isMore: Boolean = false,
        bgColor: Int = com.fanji.android.ui.R.color.white,
        isTitle: Boolean = false,
        isTopPadding: Boolean = false
    ): T {
        this.mIsRefresh = isRefresh
        this.mIsMore = isMore
        this.mBgColor = bgColor
        this.mIsTitle = isTitle
        this.mIsTopPadding = isTopPadding
        return t
    }

    private var topView: FJTopView? = null
    private var refresh: FJRefresh? = null
    protected var tipsView: FJTipsView? = null

    protected var mIsRefresh = false
    private var mIsMore = false
    private var mBgColor = 0
    private var mIsTitle = false
    private var mIsTopPadding = false

    protected var page = 0
    protected var pageSize = 20

    var transferee: Transferee? = null
    var config: TransferConfig? = null

    open val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transferee = Transferee.getDefault(activity)
        retainInstance = true
    }

    open fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelProvider.NewInstanceFactory.instance.create(modelClass)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = viewBinding(inflater, container)
        val root = view(
            _binding.root,
            mIsRefresh,
            mIsMore,
            mBgColor,
            mIsTitle
        )
        if (mIsTopPadding) {
            root.setPadding(0, SystemUtil.getStatusBarHeight(), 0, 0)
        }
        return root
    }

    open fun openUrl(url: String?) {
        openUrl(url, "web")
    }

    open fun openUrl(
        url: String?,
        title: String?
    ) {
        WebActivity.openUrl(requireContext(), title, url)
    }

    fun view(
        view: View,
        isRefresh: Boolean,
        isMore: Boolean,
        bgColor: Int,
    ): View {
        if (!isRefresh && !isMore) {
            return view
        }
        val frameLayout = FrameLayout(requireContext())
        if (bgColor != -1) {
            frameLayout.setBackgroundColor(color(bgColor))
        }
        refresh = FJRefresh(context)
        refresh?.setOnRefreshListener(this)?.setOnLoadMoreListener(this)
            ?.setEnableRefresh(isRefresh)?.setEnableLoadMore(isMore)
            ?.setRefreshHeader(MaterialHeader(activity))
            ?.setRefreshFooter(ClassicsFooter(activity))
        refresh?.addView(view)
        frameLayout.addView(refresh)
        tipsView = FJTipsView(context)
        frameLayout.addView(tipsView)
        return frameLayout
    }

    fun view(
        view: View,
        isRefresh: Boolean,
        isMore: Boolean,
        bgColor: Int = com.fanji.android.ui.R.color.white, isTitle: Boolean
    ): View {
        if (!isRefresh && !isMore && !isTitle) {
            return view
        }
        if (!isTitle) {
            return view(view, isRefresh, isMore, bgColor)
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
        if (!isRefresh && !isMore) {
            root.addView(
                view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            return root
        }
        root.addView(
            view(view, isRefresh, isMore, -1), LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        return root
    }

    open fun setTopBar(view: View?) {
        if (view == null) return
        view.setPadding(0, SystemUtil.getStatusBarHeight(), 0, 0)
    }

    open fun setTopBgIcon(topBgIcon: Int): BaseFragment<*> {
        topView?.setBg(topBgIcon)
        return this
    }

    open fun setTitleGravity(gravity: Int): BaseFragment<*>? {
        topView?.setTitleGravity(gravity)
        return this
    }

    open fun setTitle(title: Any?): BaseFragment<*>? {
        topView?.setTitle(title)
        return this
    }

    open fun setTitleColor(color: Int): BaseFragment<*>? {
        topView?.setTitleColor(color)
        return this
    }

    open fun setSmallTitle(smallTitle: Any?): BaseFragment<*>? {
        topView?.setSmallTitle(smallTitle)
        return this
    }

    open fun setSmallTitleColor(smallTitleColor: Int): BaseFragment<*>? {
        topView?.setSmallTitleColor(smallTitleColor)
        return this
    }

    open fun setLeft(any: Any): BaseFragment<*>? {
        topView?.setLefts(any)
        return this
    }

    open fun setLeftColor(color: Int): BaseFragment<*>? {
        topView?.setLeftColor(color)
        return this
    }

    open fun setLeftListener() {
        pop()
    }

    open fun setLeftListener(listener: View.OnClickListener): BaseFragment<*> {
        topView?.setOnLeftClick(listener)
        return this
    }

    open fun setRightListener(listener: View.OnClickListener): BaseFragment<*> {
        topView?.setOnRightClick(listener);
        return this
    }

    open fun setItemClickListener(listener: OnItemClickListener): BaseFragment<*> {
        topView?.setOnItemListener(listener);
        return this
    }

    open fun setLeftEnable(isEnable: Boolean): BaseFragment<*>? {
        return setLeftEnable(0.5f, isEnable)
    }

    open fun setLeftEnable(alpha: Float, isEnable: Boolean): BaseFragment<*>? {
        if (topView == null) return this
        topView?.topLeftBtn?.alpha = alpha
        topView?.topLeftBtn?.isEnabled = isEnable
        return this
    }

    open fun setRightEnable(isEnable: Boolean): BaseFragment<*>? {
        return setRightEnable(if (isEnable) 1.0f else 0.5f, isEnable)
    }

    open fun setRightEnable(alpha: Float, isEnable: Boolean): BaseFragment<*>? {
        if (topView == null) return this
        topView?.topRightBtn?.alpha = alpha
        topView?.topRightBtn?.isEnabled = isEnable
        return this
    }

    open fun setRight(`object`: Any?): BaseFragment<*>? {
        if (topView != null) {
            topView?.setRights(`object`)
        } else {
        }
        return this
    }

    open fun setRightColor(color: Int): BaseFragment<*>? {
        if (topView != null) {
            topView?.setRightColor(color)
        }
        return this
    }

    fun tips(
        code: Int = 0,
        res: Int = R.mipmap.no_data,
        tips: String = "No Content!",
        onTipsListener: FJTipsView.OnRetryListener? = null
    ): BaseFragment<*> {
        var des = tips
        if (code == -100) {
            des = "Request Service Fail!"
        }
        tipsView?.setStatus(true, false, true)?.setTipsImg(res)?.setBtnTips(des)
            ?.setListener(if (onTipsListener !== null) onTipsListener else this)
        return this
    }

    fun loading(): BaseFragment<*> {
        tipsView?.setStatus(true, true, false)
        SystemUtil.hideKeyboard(activity)
        return this
    }

    fun hiddenTips() {
        tipsView?.setStatus(false, false, false)
    }

    fun viewImg(vararg urls: String?) {
        viewImg(urls.toList(), 0)
    }

    fun viewImg(urls: List<String?>, index: Int = 0) {
        transferee?.apply(
            TransferConfig.build()
                .setImageLoader(GlideImageLoader.with(AppUtil.getApplicationContext()))
                .setNowThumbnailIndex(index)
                .setSourceImageList(urls)
                .create()
        )?.show()
    }

    open fun push(
        fragment: BaseFragment<*>,
        bundle: Bundle? = null,
        @AnimatorRes @AnimRes enter: Int = R.anim.fade_in,
        @AnimatorRes @AnimRes exit: Int = R.anim.fade_out
    ) {
        (activity as BaseActivity<*>).push(fragment, bundle, enter, exit)
    }

    open fun pop(flags: Int = 0) {
        (activity as BaseActivity<*>).pop(flags)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        topView = null
        refresh = null
        tipsView = null
    }

    fun color(res: Int): Int {
        return ContextCompat.getColor(requireContext(), res)
    }

    open fun getDim(resDim: Int): Int {
        return resources.getDimensionPixelSize(resDim)
    }

    fun refreshFinish(isRefresh: Boolean) {
        if (isRefresh) {
            refresh?.finishRefresh()
        } else {
            refresh?.finishLoadMore()
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mIsRefresh = true
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mIsRefresh = false
    }

    fun finishData() {
        refresh?.finishRefresh()
        refresh?.finishLoadMore()
    }

    open fun netState(netType: Int) {

    }
//
//    override fun onBacked(): Boolean {
//        return false
//    }

    override fun onRetry(view: View?) {
        loading()
    }

    override fun onClick(v: View?) {
    }
}