package com.fanji.android.net.netprobe

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.fanji.android.net.netprobe.cmd.ComputeOverallHealthCmd
import com.fanji.android.net.netprobe.cmd.SampleHostHealthCmd
import com.fanji.android.net.netprobe.level.NetHealthLevel
import com.fanji.android.net.netprobe.level.NetHealthLevel.Companion.judgeHealthFromValue
import com.fanji.android.net.netprobe.listener.HealthComputeListener
import com.fanji.android.net.netprobe.listener.NetChangedListener
import com.fanji.android.net.netprobe.listener.NetHealthListener
import com.fanji.android.net.netprobe.okhttp.OkhttpChecker
import com.fanji.android.net.netprobe.ping.PingChecker
import com.fanji.android.net.netprobe.watcher.NetWatcher
import io.reactivex.disposables.Disposable
import okhttp3.EventListener
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.math.abs

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 上午11:58
 */
object NetProbe {

    private val listenerMap = HashMap<String, CopyOnWriteArraySet<NetHealthListener>>()

    private val healthMap = ConcurrentHashMap<String, Float>()

    private var healthLevelMax = NetHealthLevel.UNKNOWN.lowValue

    private val overalHealthComputeListener = object : HealthComputeListener {
        override fun healthComputeDone(target: String, newValue: Float) {
            updateHealth(target, newValue)
        }
    }

    private var pingChecker: PingChecker? = null

    private var okHttpChecker: OkhttpChecker? = null

    private var netChangedDisposal: Disposable? = null

    @Volatile
    private var started = false

    private val netChangedListener = object : NetChangedListener {
        override fun onNetChanged(isConnected: Boolean) {
            Params.updateNetState(isConnected)
            val newValue =
                if (isConnected) NetHealthLevel.UNKNOWN.lowValue else NetHealthLevel.DEAD.lowValue
            healthMap.keys.forEach { updateHealth(it, newValue) }
            pingChecker?.onNetworkChanged(isConnected)
            okHttpChecker?.onNetworkChanged(isConnected)
        }
    }

    private fun updateHealth(host: String, newValue: Float) {
        val currentValue = healthMap[host] ?: NetHealthLevel.UNKNOWN.lowValue
        val currentLevel = judgeHealthFromValue(currentValue)
        val newLevel = judgeHealthFromValue(newValue)
        healthMap[host] = newValue
        val fixedCurValue = (currentValue * 100).toInt() / 100.0F
        val fixedNewValue = (newValue * 100).toInt() / 100F
        if (abs(fixedCurValue - fixedNewValue) > 0.05 || currentLevel != newLevel) {
            listenerMap[host]?.forEach {
                it.onHealthValueChanged(host, currentValue, newValue)
                if (currentLevel != newLevel) it.onHealthLevelChanged(host, currentLevel, newLevel)
            }
        }
        healthLevelMax =
            healthMap.entries.maxBy { it.value }?.value ?: NetHealthLevel.UNKNOWN.lowValue
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun start(context: Context) {
        if (started) return
        started = true
        Params.init()
        NPThreadPool.start()
        NetWatcher.addListener(netChangedListener)
        NetWatcher.init(context)
        pingChecker = PingChecker(object : PingChecker.CheckReporter {
            override fun report(ipAddress: String, newValue: Float) {
                Params.getHosts(ipAddress).forEach {
                    NPThreadPool.runCmd(
                        ComputeOverallHealthCmd(
                            it, pingChecker?.getHealth(it), newValue,
                            overalHealthComputeListener
                        )
                    )
                }
            }
        })
        Params.setIpChangedListener(pingChecker!!)
        pingChecker?.start()
        okHttpChecker = OkhttpChecker(object : OkhttpChecker.CheckReporter {
            override fun reportIpAddress(host: String, ipAddress: String) {
                Params.updateHostAndIp(host, ipAddress)
            }

            override fun reportHealth(host: String, healthValue: Float) {
                val ipAddress = Params.getIpAddress(host)
                val pingHealthValue =
                    if (ipAddress != null) pingChecker?.getHealth(ipAddress) else null
                NPThreadPool.runCmd(
                    ComputeOverallHealthCmd(
                        host, healthValue, pingHealthValue,
                        overalHealthComputeListener
                    )
                )
            }
        })
        okHttpChecker?.start()
        Params.getAllHost().forEach { listenerMap[it] = CopyOnWriteArraySet() }
        NPThreadPool.runCmd(SampleHostHealthCmd())
    }

    fun getNetHealthLevel(): NetHealthLevel {
        return getNetHealthLevel(null)
    }

    fun getNetHealthLevel(host: String?, business: String? = null): NetHealthLevel {
        if (host == null) {
            return judgeHealthFromValue(healthLevelMax)
        }
        val netValue = getNetHealthValue(host)
        return judgeHealthFromValue(netValue)
    }

    fun getNetHealthValue(host: String?): Float {
        if (host == null || !Params.isHostWatching(host)) return healthLevelMax
        if (!Params.isNetConnected()) return NetHealthLevel.DEAD.lowValue
        healthMap[host]?.let { return it }
        return getAltValue(host)
    }

    private fun getAltValue(host: String): Float {
        Params.getSameAddressHosts(host).forEach { healthMap[it]?.let { it -> return it } }
        return NetHealthLevel.UNKNOWN.lowValue
    }

    fun addListener(vararg hosts: String, listener: NetHealthListener): Array<String> {
        val notAllowedHost = mutableListOf<String>()
        hosts.forEach {
            if (listenerMap.containsKey(it)) {
                listenerMap[it]!!.add(listener)
                val value = healthMap[it] ?: NetHealthLevel.UNKNOWN.lowValue
                val level = judgeHealthFromValue(value)
                listener.onHealthValueChanged(it, value, value)
                listener.onHealthLevelChanged(it, level, level)
            } else {
                notAllowedHost.add(it)
            }
        }
        return notAllowedHost.toTypedArray()
    }

    fun removeListener(vararg hosts: String, listener: NetHealthListener): Array<String> {
        val notAllowedHost = mutableListOf<String>()
        hosts.forEach {
            if (listenerMap.containsKey(it)) {
                if (!listenerMap[it]!!.remove(listener)) notAllowedHost.add(it)
            } else {
                notAllowedHost.add(it)
            }
        }
        return notAllowedHost.toTypedArray()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun stop() {
        if (!started) return
        NetWatcher.unInit()
        NPThreadPool.stop()
        Params.unInit()
        netChangedDisposal?.dispose()
        okHttpChecker?.stop()
        pingChecker?.stop()
        started = false
    }

    fun forceCheck() {
        okHttpChecker?.forceCheck()
    }

    fun getEventListener(): EventListener? {
        if (started) return okHttpChecker?.eventListener
        return null
    }

    fun getSupportHosts(): Set<String> {
        return Params.getAllHost().toSet()
    }

    fun isHostSupported(host: String): Boolean {
        return Params.isHostWatching(host)
    }

    /**
     * 通知全局状态改变，用于感知前后台变化
     *
     * @param isForeground 是否为前台
     */
    internal fun globalStateChange(isForeground: Boolean) {
        okHttpChecker?.onGlobalStateChanged(isForeground)
        pingChecker?.onGlobalStateChanged(isForeground)
    }
}