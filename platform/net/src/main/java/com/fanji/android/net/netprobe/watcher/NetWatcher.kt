package com.fanji.android.net.netprobe.watcher

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import com.fanji.android.net.netprobe.listener.NetChangedListener
import com.fanji.android.net.state.NetState
import com.fanji.android.thread.NamedRunnable
import com.fanji.android.thread.RulerScheduler
import com.fanji.android.util.LogUtil
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午1:45
 */
internal object NetWatcher {
    private var listener: NetChangedListener? = null

    private var connectivityManager: ConnectivityManager? = null

    private var future: Future<out Any>? = null

    private val networkCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network) {
            super.onLost(network)
            future?.cancel(false)
            val avalaiable = NetState.instance.isAvailable()
            if (avalaiable) {
                listener?.onNetChanged(true)
            } else {
                val runnable = object : NamedRunnable("recheck") {
                    var times = 0
                    val maxTimes = 5
                    override fun execute() {
                        times++
                        val available =
                            NetState.instance.isAvailable()
                        if (available || times >= maxTimes) {
                            listener?.onNetChanged(true)
                            future?.cancel(false)
                        }
                    }
                }
                future = RulerScheduler.runAtFixedRate(
                    runnable = runnable,
                    500,
                    1000,
                    TimeUnit.MILLISECONDS
                )
            }
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            LogUtil.e("Net onAvailable:$network")
            listener?.onNetChanged(true)
            future?.cancel(false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun init(context: Context) {
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (!NetState.instance.isAvailable()) {
            listener?.onNetChanged(false)
        }
        val networkRequestBuilder = NetworkRequest.Builder()
            // 数据网络
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            // 有线以太网
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            // VPN 登录和登出
            .addTransportType(NetworkCapabilities.TRANSPORT_VPN)
            // wifi
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 8.0 的 wifi aware，传递近场信息的
            networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI_AWARE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            // 新的物联网通讯协议
            networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_LOWPAN)
        }

        // 正式注册系统网络变化监听器
        try {
            connectivityManager?.registerNetworkCallback(
                networkRequestBuilder.build(),
                networkCallback
            )
        } catch (e: Exception) {
            LogUtil.e("App cloud config registerNetworkCallback is fail! Error: ${e.message}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun unInit() {
        connectivityManager?.unregisterNetworkCallback(networkCallback)
    }

    fun addListener(listener: NetChangedListener) {
        this.listener = listener
    }
}