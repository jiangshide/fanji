package com.fanji.android.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.fanji.android.ui.FJTipsView
import com.fanji.android.ui.FJTopView
import com.fanji.android.ui.R
import com.fanji.android.ui.refresh.FJRefresh
import com.fanji.android.ui.refresh.api.RefreshLayout
import com.fanji.android.ui.refresh.listener.OnLoadMoreListener
import com.fanji.android.ui.refresh.listener.OnRefreshListener
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
        isTips: Boolean = false,
        bgColor: Int = R.color.bg,
        isTitle: Boolean = false,
        leftBtn: Any? = null,
        title: Any? = null,
        rightBtn: Any? = null,
        fragment: BaseFragment<*>? = null,
        isTopPadding: Boolean = true
    ): T {
        this.fragment = fragment
        panel!!.initView(
            isRefresh,
            isMore,
            isTips,
            bgColor,
            isTitle,
            leftBtn,
            title,
            rightBtn,
            isTopPadding
        )
        return t
    }

    private val panel: Panel? = Panel()
    private var topView: FJTopView? = null
    private var refresh: FJRefresh? = null
    protected var tipsView: FJTipsView? = null
    private var fragment: BaseFragment<*>? = null

    protected var page = 0
    protected var pageSize = 20

//    var transferee: Transferee? = null

    open val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        transferee = Transferee.getDefault(activity)
        retainInstance = true
    }

    open fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelProvider.NewInstanceFactory.instance.create(modelClass)
    }

    open fun <T : AndroidViewModel> create(modelClass: Class<T>): T {
        return ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[modelClass]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = viewBinding(inflater, container)
        val root = panel!!.view(
            requireContext(),
            _binding.root, this, this
        )
        if (panel.mIsTopPadding) {
            root?.setPadding(0, SystemUtil.getStatusBarHeight(), 0, 0)
        }
        topView = panel.topView
        if (topView != null) {
            topView!!.setOnLeftClick {
                if (panel.mLeftBtn != null && panel.mLeftBtn == "") {
                    return@setOnLeftClick
                }
                setLeftListener()
            }
            topView!!.setOnRightClick {
                setRightListener()
            }
        }
        refresh = panel.refresh
        tipsView = panel.tipsView
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

    open fun setTopBgIcon(topBgIcon: Int): BaseFragment<*> {
        topView?.setBg(topBgIcon)
        return this
    }

    open fun setTitleGravity(gravity: Int): BaseFragment<*>? {
        topView?.setTitleGravity(gravity)
        return this
    }

    open fun setTopTitle(title: Any?): BaseFragment<*>? {
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

    open fun setRightListener() {
        if (fragment != null) {
            push(fragment!!)
            return
        }
        pop()
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
        btnTips: String = "重试",
        onTipsListener: FJTipsView.OnRetryListener? = null
    ): BaseFragment<*> {
        var des = tips
        if (code == -100) {
            des = "Request Service Fail!"
        }
        tipsView?.setStatus(true, false, true)?.setTipsImg(res)?.setTipsDes(des)
            ?.setBtnTips(btnTips)
            ?.setListener(if (onTipsListener !== null) onTipsListener else this)
        return this
    }

    fun loading(): BaseFragment<*> {
        tipsView?.setStatus(true, true, false)
        SystemUtil.hideKeyboard(activity)
        return this
    }

    fun finishTips() {
        tipsView?.setStatus(false, false, false)
    }

    fun viewImg(vararg urls: String?) {
        viewImg(urls.toList(), 0)
    }

    fun viewImg(urls: List<String?>, index: Int = 0) {
        (activity as BaseActivity<*>).viewImg(urls, index)
    }

    open fun push(
        fragment: BaseFragment<*>,
        bundle: Bundle? = null,
        @AnimatorRes @AnimRes enter: Int = R.anim.slide_right_in,
        @AnimatorRes @AnimRes exit: Int = R.anim.slide_right_out
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

    fun getResArr(res: Int): List<String> {
        return resources.getStringArray(res).toList()
    }

    fun color(res: Int): Int {
        return ContextCompat.getColor(requireContext(), res)
    }

    open fun getDim(resDim: Int): Int {
        return resources.getDimensionPixelSize(resDim)
    }

    fun refreshFinish(isFinishRefresh: Boolean = true, isFinishMore: Boolean) {
        if (isFinishRefresh) {
            refresh?.finishRefresh()
        }
        if (isFinishMore) {
            refresh?.finishLoadMore()
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
    }

    fun finishData(
        isFinishRefresh: Boolean = false,
        isFinishMore: Boolean = false,
        isFinishTips: Boolean = false
    ) {
        refreshFinish(isFinishRefresh, isFinishMore)
        if (isFinishTips) {
            finishTips()
        }
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