package com.fanji.android.net.netprobe.ping

import com.fanji.android.net.netprobe.NPThreadPool
import com.fanji.android.net.netprobe.Params
import com.fanji.android.net.netprobe.cmd.CleanCheckRecordCmd
import com.fanji.android.net.netprobe.data.NetHealthData
import com.fanji.android.net.netprobe.level.NetHealthLevel
import com.fanji.android.net.netprobe.listener.Checker
import com.fanji.android.net.netprobe.listener.HealthComputeListener
import com.fanji.android.net.netprobe.listener.IpChangedListener
import com.fanji.android.net.netprobe.ping.cmd.ComputePingHealthCmd
import com.fanji.android.net.netprobe.ping.cmd.PingProcessCheckCmd
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.abs

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午5:02
 */
internal class PingChecker(private val reporter: CheckReporter) : Checker, HealthComputeListener,
    IpChangedListener {

    @Volatile
    private var isConnected = true
    private val healthMap = ConcurrentHashMap<String, NetHealthData>()
    private val checkDataRecord = ConcurrentLinkedQueue<PingCheckData>()
    private val ongoingCheckCmd = ConcurrentHashMap<String, PingProcessCheckCmd>()

    private val dataReporter = object : PingProcessCheckCmd.PingCheckCallback {
        override fun onFinish(data: PingCheckData) {
            ongoingCheckCmd.remove(data.ipAddress)
            if (!isConnected) return
            if (healthMap.containsKey(data.ipAddress)) {
                checkDataRecord.add(data)
                NPThreadPool.runCmd(
                    ComputePingHealthCmd(
                        data.ipAddress,
                        checkDataRecord,
                        this@PingChecker
                    )
                )
                NPThreadPool.runCmd(CleanCheckRecordCmd(Params.getPingWinSize(), checkDataRecord))
            }
        }
    }

    override fun start() {

    }

    override fun stop() {
        checkDataRecord.clear()
        cancelOngoingCmd()
        healthMap.clear()
    }

    override fun onGlobalStateChanged(isForeground: Boolean) {
        if (isForeground) {
            val now = System.currentTimeMillis()
            healthMap.forEach {
                val delay = it.value.validUtil - now
                doCheck(it.key, if (delay < 0) 0 else delay)
            }
        } else {
            cancelOngoingCmd()
        }
    }

    private fun doCheck(ipAddress: String, delay: Long) {
        if (delay < 0) return
        if (!Params.isForeground() || !Params.isNetConnected()) return
        if (ongoingCheckCmd.contains(ipAddress)) return
        val checkCmd = PingProcessCheckCmd(ipAddress, delay, callback = dataReporter)
        ongoingCheckCmd[ipAddress] = checkCmd
        NPThreadPool.runCmd(checkCmd)
    }

    override fun onNetworkChanged(netChanged: Boolean) {
        isConnected = netChanged
        checkDataRecord.clear()
        if (netChanged) {
            healthMap.keys.forEach {
                healthMap[it] =
                    NetHealthData(System.currentTimeMillis(), NetHealthLevel.UNKNOWN.lowValue)
                doCheck(it, 0)
            }
        } else {
            healthMap.keys.forEach {
                healthMap[it] =
                    NetHealthData(System.currentTimeMillis(), NetHealthLevel.DEAD.lowValue)
            }
            cancelOngoingCmd()
        }
    }

    override fun forceCheck() {
    }

    override fun getHealth(key: String): Float? {
        val record = healthMap[key]
        if (record != null && record.isValid()) return record.value
        return null
    }

    override fun healthComputeDone(target: String, newValue: Float) {
        if (!Params.isIpActive(target)) return
        val currentValue = healthMap[target]?.value ?: NetHealthLevel.UNKNOWN.lowValue
        val healthLifeTime = Params.getPingHealthLifeTime()
        healthMap[target] = NetHealthData(System.currentTimeMillis() + healthLifeTime, newValue)
        reporter.report(target, newValue)
        val currentLevel = NetHealthLevel.judgeHealthFromValue(currentValue)
        val newLevel = NetHealthLevel.judgeHealthFromValue(newValue)
        if (abs(currentValue - newValue) > 0.05 || currentLevel != newLevel) {
            //
        }
        doCheck(target, (healthLifeTime * Params.getPingCheckThreshold()).toLong())
    }

    override fun onIpChanged(host: String, oldIp: String?, newIp: String) {
        if (oldIp != null && healthMap.containsKey(oldIp)) {
            healthMap.remove(oldIp)
            healthMap[newIp] = NetHealthData(
                System.currentTimeMillis() + Params.getPingHealthLifeTime(),
                NetHealthLevel.UNKNOWN.lowValue
            )
            ongoingCheckCmd[oldIp]?.let {
                NPThreadPool.removeCmd(it)
                ongoingCheckCmd.remove(oldIp)
            }
        } else {
            healthMap[newIp] = NetHealthData(
                System.currentTimeMillis() + Params.getPingHealthLifeTime(),
                NetHealthLevel.UNKNOWN.lowValue
            )
        }
        doCheck(newIp, 0)
    }

    private fun cancelOngoingCmd() {
        ongoingCheckCmd.values.forEach { NPThreadPool.removeCmd(it) }
        ongoingCheckCmd.clear()
    }

    interface CheckReporter {
        fun report(ipAddress: String, newValue: Float)
    }
}