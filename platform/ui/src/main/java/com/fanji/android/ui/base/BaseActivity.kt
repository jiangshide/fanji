package com.fanji.android.ui.base

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.fanji.android.net.state.NetState
import com.fanji.android.net.state.annotation.INetType
import com.fanji.android.net.state.annotation.NetType
import com.fanji.android.ui.FJTipsView
import com.fanji.android.ui.FJTopView
import com.fanji.android.ui.R
import com.fanji.android.ui.refresh.FJRefresh
import com.fanji.android.ui.refresh.api.RefreshLayout
import com.fanji.android.ui.refresh.listener.OnLoadMoreListener
import com.fanji.android.ui.refresh.listener.OnRefreshListener
import com.fanji.android.util.FJEvent
import com.fanji.android.util.LogUtil
import com.fanji.android.util.SystemUtil


/**
 * created by jiangshide on 4/9/21.
 * email:18311271399@163.com
 */
abstract class BaseActivity<T : ViewBinding> : FragmentActivity(), OnRefreshListener,
    OnLoadMoreListener,
    FJTipsView.OnRetryListener {

    private lateinit var _binding: T
    protected val binding get() = _binding
    protected abstract fun getViewBinding(): T

    protected val panel: Panel? = Panel()
    private var topView: FJTopView? = null
    private var refresh: FJRefresh? = null
    protected var tipsView: FJTipsView? = null

    public fun initView(
        t: T,
        isRefresh: Boolean = false,
        isMore: Boolean = false,
        isTips: Boolean = false,
        bgColor: Int = R.drawable.bg_sweep,
        isTitle: Boolean = false,
        title: String? = null,
        isTopPadding: Boolean = false
    ): T {
        panel!!.initView(isRefresh, isMore, isTips, bgColor, isTitle, title, isTopPadding)
        return t
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Main)
        super.onCreate(savedInstanceState)
        _binding = getViewBinding()
        val root = panel!!.view(
            this, _binding.root, this, this
        )
        if (panel.mIsTopPadding) {
            root?.setPadding(0, SystemUtil.getStatusBarHeight(), 0, 0)
        }
        topView = panel.topView
        refresh = panel.refresh
        tipsView = panel.tipsView
        setContentView(root)
    }

    open fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelProvider.NewInstanceFactory.instance.create(modelClass)
    }

    open fun <T : AndroidViewModel> create(modelClass: Class<T>): T {
        return ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(application)
        )[modelClass]
    }

    fun color(res: Int): Int {
        return ContextCompat.getColor(this, res)
    }

    open fun openUrl(url: String?) {
        openUrl(url, "web")
    }

    open fun openUrl(
        url: String?,
        title: String?
    ) {
        WebActivity.openUrl(this, title, url)
    }

    open fun setTopBgIcon(topBgIcon: Int): BaseActivity<*> {
        topView?.setBg(topBgIcon)
        return this
    }

    open fun setTitleGravity(gravity: Int): BaseActivity<*>? {
        topView?.setTitleGravity(gravity)
        return this
    }

    open fun setTopTitle(title: Any?): BaseActivity<*>? {
        topView?.setTitle(title)
        return this
    }

//    open fun setTitleColor(color: Int): BaseActivity<*>? {
//        topView?.setTitleColor(color)
//        return this
//    }

    open fun setSmallTitle(smallTitle: Any?): BaseActivity<*>? {
        topView?.setSmallTitle(smallTitle)
        return this
    }

    open fun setSmallTitleColor(smallTitleColor: Int): BaseActivity<*>? {
        topView?.setSmallTitleColor(smallTitleColor)
        return this
    }

    open fun setLeft(any: Any): BaseActivity<*>? {
        topView?.setLefts(any)
        return this
    }

    open fun setLeftColor(color: Int): BaseActivity<*>? {
        topView?.setLeftColor(color)
        return this
    }

    open fun setLeftListener() {
        finish()
    }

    open fun setLeftListener(listener: View.OnClickListener): BaseActivity<*> {
        topView?.setOnLeftClick(listener)
        return this
    }

    open fun setRightListener(listener: View.OnClickListener): BaseActivity<*> {
        topView?.setOnRightClick(listener);
        return this
    }

    open fun setItemClickListener(listener: AdapterView.OnItemClickListener): BaseActivity<*> {
        topView?.setOnItemListener(listener);
        return this
    }

    open fun setLeftEnable(isEnable: Boolean): BaseActivity<*>? {
        return setLeftEnable(0.5f, isEnable)
    }

    open fun setLeftEnable(alpha: Float, isEnable: Boolean): BaseActivity<*>? {
        if (topView == null) return this
        topView?.topLeftBtn?.alpha = alpha
        topView?.topLeftBtn?.isEnabled = isEnable
        return this
    }

    open fun setRightEnable(isEnable: Boolean): BaseActivity<*>? {
        return setRightEnable(if (isEnable) 1.0f else 0.5f, isEnable)
    }

    open fun setRightEnable(alpha: Float, isEnable: Boolean): BaseActivity<*>? {
        if (topView == null) return this
        topView?.topRightBtn?.alpha = alpha
        topView?.topRightBtn?.isEnabled = isEnable
        return this
    }

    open fun setRight(`object`: Any?): BaseActivity<*>? {
        if (topView != null) {
            topView?.setRights(`object`)
        } else {
        }
        return this
    }

    open fun setRightColor(color: Int): BaseActivity<*>? {
        if (topView != null) {
            topView?.setRightColor(color)
        }
        return this
    }

    /**
     * 网络状态监控
     * int NONE = -1;
     * int AVAILABLE = 1;
     * int AUTO = 2;
     * int CELLULAR = 3;//cmwap
     * int WIFI = 4;
     * int BLUETOOTH = 5;
     * int ETHERNET = 6;
     * int VPN = 7;
     * int WIFI_AWARE = 8;
     * int LOWPAN = 9;
     */
    @NetType(netType = INetType.AUTO)
    open fun netState(@INetType netType: Int) {
        FJEvent.get().with(NetState.Companion.instance.NetType).post(netType)
        LogUtil.e("-------netState~netType:", netType)
    }

    open fun push(
        fragment: BaseFragment<*>,
        bundle: Bundle? = null,
        @AnimatorRes @AnimRes enter: Int = R.anim.fade_in,
        @AnimatorRes @AnimRes exit: Int = R.anim.fade_out
    ) {
        fragment.arguments = bundle
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.setCustomAnimations(enter, exit)
        beginTransaction.add(R.id.content, fragment)
//        beginTransaction.setMaxLifecycle(fragment, Lifecycle.State.DESTROYED)
        beginTransaction.addToBackStack(fragment.javaClass.canonicalName)
        beginTransaction.commitAllowingStateLoss()
    }

    open fun pop(flags: Int = 0) {
        supportFragmentManager.popBackStackImmediate(null, flags)
    }

    fun finishTips() {
        tipsView?.setStatus(false, false, false)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
    }

    fun refreshFinish(isFinishRefresh: Boolean = true, isFinishMore: Boolean) {
        if (isFinishRefresh) {
            refresh?.finishRefresh()
        }
        if (isFinishMore) {
            refresh?.finishLoadMore()
        }
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

    fun viewImg(vararg urls: String?) {
        viewImg(urls.toList(), 0)
    }

    fun viewImg(urls: List<String?>, index: Int = 0) {
        panel?.viewImg(this, urls, index)
    }

    override fun onRetry(view: View?) {
    }
}
