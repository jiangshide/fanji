package com.fanji.android.resource.base

import android.content.Intent
import android.os.Bundle
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.fanji.android.net.state.NetState
import com.fanji.android.net.state.annotation.INetType
import com.fanji.android.net.state.annotation.NetType
import com.fanji.android.resource.R
import com.fanji.android.util.FJEvent
import com.fanji.android.util.LogUtil


/**
 * created by jiangshide on 4/9/21.
 * email:18311271399@163.com
 */
abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {

    private lateinit var _binding: T
    protected val binding get() = _binding
    protected abstract fun getViewBinding(): T

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Main)
        super.onCreate(savedInstanceState)
        _binding = getViewBinding()
        setContentView(_binding.root)
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
        val bundle = Bundle()
        bundle.putString("title", title)
        bundle.putString("url", url)
        val intent = Intent(this, WebActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
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
}
