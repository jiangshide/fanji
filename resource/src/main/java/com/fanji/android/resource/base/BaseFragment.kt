package com.fanji.android.resource.base

import android.os.Bundle
import android.text.TextUtils
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
import com.fanji.android.net.state.NetState
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
import com.fanji.android.util.FJEvent
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
    protected abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): T

    private var topView: FJTopView? = null
    private var refresh: FJRefresh? = null
    private var tipsView: FJTipsView? = null

    private var hashMap: HashMap<String, Any>? = null

    private val mShowTop = true

    protected var isRefresh = false
    protected var page = 0
    protected var pageSize = 20

    var transferee: Transferee? = null
    var config: TransferConfig? = null

    open val uiScope = CoroutineScope(Dispatchers.Main)

    init {
        hashMap = HashMap()
    }

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
        _binding = getViewBinding(inflater, container)
        return _binding.root
    }

    open fun openUrl(url: String?) {
        openUrl(url, "web")
    }

    open fun openUrl(
        url: String?,
        title: String?
    ) {
        WebActivity.openUrl(requireContext(),title,url)
    }

    fun view(
        layout: Int,
        isRefresh: Boolean = false,
        isMore: Boolean = false,
        isTips: Boolean = false
    ): View {
        FJEvent.get().with(NetState.instance.NetType, Int::class.java).observes(this, {
            netState(it)
        })
        return view(
            LayoutInflater.from(requireActivity()).inflate(layout, null),
            isRefresh,
            isMore,
            isTips = isTips
        )
    }

    fun view(
        view: View,
        isRefresh: Boolean,
        isMore: Boolean,
        bgColor: Int = com.fanji.android.ui.R.color.white, isTips: Boolean = true
    ): View {
        val frameLayout = FrameLayout(requireContext())
        frameLayout.setBackgroundColor(color(bgColor))
        tipsView = FJTipsView(context)
        if (isRefresh || isMore) {
            refresh = FJRefresh(context)
            refresh?.setOnRefreshListener(this)?.setOnLoadMoreListener(this)
                ?.setEnableRefresh(isRefresh)?.setEnableLoadMore(isMore)
                ?.setRefreshHeader(MaterialHeader(activity))
                ?.setRefreshFooter(ClassicsFooter(activity))
            refresh?.addView(view)
            frameLayout.addView(refresh)
        } else {
            frameLayout.addView(view)
        }
        if (isTips) {
            frameLayout.addView(tipsView)
        }
        return frameLayout
    }

    fun setTitleView(
        layout: Int, title: String = "", isRefresh: Boolean = false,
        isMore: Boolean = false,
        isTips: Boolean = true
    ): View {
        val root = LinearLayout(context)
        root?.isClickable = true
        root.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
//        root.setBackgroundColor(color(R.color.white))
        root.orientation = LinearLayout.VERTICAL
        if (topView == null) {
            topView = FJTopView(context)
        }
        if (!TextUtils.isEmpty(title)) {
            topView?.setTitle(title)
        } else if (hashMap!!.contains("title")) {
            topView?.setTitle(hashMap?.get("title"))
        }
        if (hashMap!!.containsKey("titleColor")) {
            topView?.setTitleColor(hashMap!!["titleColor"] as Int)
        }
        if (hashMap!!.containsKey("topBgIcon")) {
            topView?.setBg(hashMap!!["topBgIcon"] as Int)
        }
        if (hashMap!!.containsKey("titleGravity")) {
            topView?.setTitleGravity(hashMap!!["titleGravity"] as Int)
        }
        if (hashMap!!.containsKey("smallTitle")) {
            topView?.setSmallTitle(hashMap!!["smallTitle"])
        }
        if (hashMap!!.containsKey("smallTitleColor")) {
            topView?.setSmallTitleColor(hashMap!!["smallTitleColor"] as Int)
        }
        if (hashMap!!.containsKey("left")) {
            topView?.setLefts(hashMap!!["left"])
        }
        if (hashMap!!.containsKey("leftColor")) {
            topView?.setLeftColor(hashMap!!["leftColor"] as Int)
        }
        if (hashMap!!.containsKey("right")) {
            topView?.setRights(hashMap!!["right"])
        }
        if (hashMap!!.containsKey("rightColor")) {
            topView?.setRightColor(hashMap!!["rightColor"] as Int)
        }
        topView?.setOnLeftClick(if (hashMap!!.containsKey("leftClick")) hashMap!!["leftClick"] as View.OnClickListener? else this)

        if (hashMap!!.containsKey("rightClick")) {
            topView?.setOnRightClick(hashMap!!["rightClick"] as View.OnClickListener?)
        }
        if (hashMap!!.containsKey("datas")) {
            topView?.setDataList(hashMap!!["datas"] as List<String?>?)
        }
        if (hashMap!!.containsKey("itemClick")) {
            topView?.setOnItemListener(hashMap!!["itemClick"] as OnItemClickListener?)
        }
        root.removeView(topView)
        root.addView(
            topView, LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        root.addView(
            view(layout, isRefresh, isMore, isTips), LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        if (mShowTop) {
//            setTopBar(root)
        }
        return root
    }

    open fun setTopBar(view: View?) {
        if (view == null) return
        view.setPadding(0, SystemUtil.getStatusBarHeight(), 0, 0)
    }

    open fun setTopBgIcon(topBgIcon: Int): BaseFragment<*> {
        topView?.setBg(topBgIcon)
        hashMap!!["topBgIcon"] = topBgIcon
        return this
    }

    open fun setTitleGravity(gravity: Int): BaseFragment<*>? {
        topView?.setTitleGravity(gravity)
        hashMap!!["titleGravity"] = gravity
        return this
    }

    open fun setTitle(title: Any?): BaseFragment<*>? {
        topView?.setTitle(title)
        hashMap!!["title"] = title!!
        return this
    }

    open fun setTitleColor(color: Int): BaseFragment<*>? {
        topView?.setTitleColor(color)
        hashMap!!["titleColor"] = color
        return this
    }

    open fun setSmallTitle(smallTitle: Any?): BaseFragment<*>? {
        topView?.setSmallTitle(smallTitle)
        hashMap!!["smallTitle"] = smallTitle!!
        return this
    }

    open fun setSmallTitleColor(smallTitleColor: Int): BaseFragment<*>? {
        topView?.setSmallTitleColor(smallTitleColor)
        hashMap!!["smallTitleColor"] = smallTitleColor
        return this
    }

    open fun setLeft(any: Any): BaseFragment<*>? {
        topView?.setLefts(any)
        hashMap!!["left"] = any
        return this
    }

    open fun setLeftColor(color: Int): BaseFragment<*>? {
        topView?.setLeftColor(color)
        hashMap!!["leftColor"] = color
        return this
    }

    open fun setLeftListener() {
        pop()
    }

    open fun setLeftListener(listener: View.OnClickListener): BaseFragment<*> {
        topView?.setOnLeftClick(listener)
        hashMap!!["leftClick"] = listener
        return this
    }

    open fun setRightListener(listener: View.OnClickListener): BaseFragment<*> {
        topView?.setOnRightClick(listener);
        hashMap!!["rightClick"] = listener;
        return this
    }

    open fun setItemClickListener(listener: OnItemClickListener): BaseFragment<*> {
        topView?.setOnItemListener(listener);
        hashMap!!["itemClick"] = listener;
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
            hashMap!!["right"] = `object`!!
        }
        return this
    }

    open fun setRightColor(color: Int): BaseFragment<*>? {
        if (topView != null) {
            topView?.setRightColor(color)
        }
        hashMap!!["rightColor"] = color
        return this
    }

    fun tips(
        res: Int = R.mipmap.no_data,
        tips: String = "No Content!",
        onTipsListener: FJTipsView.OnRetryListener? = null
    ): BaseFragment<*> {
        hiddenTips()
        tipsView?.setTipsImg(res)?.setBtnTips(tips)
            ?.setListener(if (onTipsListener !== null) onTipsListener else this)?.noNet()
        return this
    }

    fun loading(): BaseFragment<*> {
        tipsView?.loading()
        SystemUtil.hideKeyboard(activity)
        return this
    }

    fun hiddenTips() {
        tipsView?.hidden()
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
        hashMap?.clear()
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
        isRefresh = true
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        isRefresh = false
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