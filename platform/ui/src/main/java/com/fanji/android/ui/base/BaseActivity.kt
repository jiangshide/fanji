package com.fanji.android.ui.base

import android.os.Bundle
import android.view.View
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
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

    public fun initView(
        t: T, isRefresh: Boolean = false,
        isMore: Boolean = false,
        isTips: Boolean = false,
        bgColor: Int = R.drawable.bg_sweep,
        isTitle: Boolean = false,
        title: String? = null,
        isTopPadding: Boolean = false
    ): T {
        panel!!.initView(t, isRefresh, isMore, isTips, bgColor, isTitle, title, isTopPadding)
        return t
    }

    private val panel: Panel<T>? = Panel()
    private var mTitle: String? = null
    private var topView: FJTopView? = null
    private var refresh: FJRefresh? = null
    protected var tipsView: FJTipsView? = null

    protected var mIsRefresh = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Main)
        super.onCreate(savedInstanceState)
        _binding = getViewBinding()
        val root = panel!!.view(
            this,
            _binding.root, this, this
        )
        if (panel.mIsTopPadding) {
            root.setPadding(0, SystemUtil.getStatusBarHeight(), 0, 0)
        }
        topView = panel.topView
        refresh = panel.refresh
        tipsView = panel.tipsView
        setContentView(root)
    }

    open fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelProvider.NewInstanceFactory.instance.create(modelClass)
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
    }

    open fun pop(flags: Int = 0) {
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
    }

    override fun onRetry(view: View?) {
    }
}
