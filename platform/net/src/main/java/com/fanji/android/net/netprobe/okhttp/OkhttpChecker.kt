package com.fanji.android.net.netprobe.okhttp

import com.fanji.android.net.netprobe.ForceCheckWorkAround
import com.fanji.android.net.netprobe.NPThreadPool
import com.fanji.android.net.netprobe.Params
import com.fanji.android.net.netprobe.cmd.CleanCheckRecordCmd
import com.fanji.android.net.netprobe.cmd.ComputeHttpHealthCmd
import com.fanji.android.net.netprobe.cmd.HttpCheckCmd
import com.fanji.android.net.netprobe.cmd.HttpTimeOutCmd
import com.fanji.android.net.netprobe.data.NetHealthData
import com.fanji.android.net.netprobe.level.NetHealthLevel
import com.fanji.android.net.netprobe.listener.Checker
import com.fanji.android.net.netprobe.listener.HealthComputeListener
import okhttp3.*
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.abs

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午4:55
 */
internal class OkhttpChecker(private val reporter: CheckReporter) : Checker, HealthComputeListener {

    private val healthMap = ConcurrentHashMap<String, NetHealthData>()
    private val checkDataRecord = ConcurrentLinkedQueue<OkHttpCheckData>()
    private val ongoingCheckCmd = ConcurrentHashMap<String, HttpCheckCmd>()

    private var client: OkHttpClient? = null

    @Volatile
    private var isStop = false

    internal val eventListener = object : EventListener() {

        private val callMap = ConcurrentHashMap<Call, CallRecord>()

        override fun callStart(call: Call) {
            super.callStart(call)
            if (isStop) return
            val host = call.request().url.host
            if (Params.isHostWatching(host)) {
                val now = System.currentTimeMillis()
                val record =
                    CallRecord(now, HttpTimeOutCmd(host, Params.getHostHttpRequestTimeOut(host)) {
                        collectData(
                            OkHttpCheckData(
                                host,
                                Params.getHostHttpRequestTimeOut(host),
                                call.request(),
                                HttpTimeOutException(host)
                            )
                        )
                    })
                callMap[call] = record
                NPThreadPool.runCmd(record.timeOutCmd)
            }
        }

        override fun connectionAcquired(call: Call, connection: Connection) {
            super.connectionAcquired(call, connection)
            callMap[call]?.let {
                connection.socket().inetAddress?.hostAddress?.let {
                    reporter.reportIpAddress(call.request().url.host, it)
                }
            }
        }

        override fun connectFailed(
            call: Call,
            inetSocketAddress: InetSocketAddress,
            proxy: Proxy,
            protocol: Protocol?,
            ioe: IOException
        ) {
            super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe)
            callMap[call]?.let { doCollect(call, it, ioe) }
        }

        override fun requestBodyEnd(call: Call, byteCount: Long) {
            super.requestBodyEnd(call, byteCount)
            callMap[call]?.let {
                val old = it.startTime
                it.startTime = System.currentTimeMillis()
            }
        }

        override fun responseHeadersEnd(call: Call, response: Response) {
            super.responseHeadersEnd(call, response)
            val fixed = response.header("x-backend-response")?.toFloatOrNull() ?: 0F
            callMap[call]?.let { doCollect(call, it, fixedResponse = fixed) }
        }

        override fun callFailed(call: Call, ioe: IOException) {
            super.callFailed(call, ioe)
            callMap[call]?.let { doCollect(call, it, ioe) }
        }

        override fun callEnd(call: Call) {
            super.callEnd(call)
            callMap.remove(call)
        }

        private fun doCollect(
            call: Call,
            callRecord: CallRecord,
            exception: Exception? = null,
            fixedResponse: Float = 0f
        ) {
            NPThreadPool.removeCmd(callRecord.timeOutCmd)
            collectData(
                OkHttpCheckData(
                    call.request().url.host,
                    Math.max(
                        System.currentTimeMillis() - callRecord.startTime - (fixedResponse * 1_000).toLong(),
                        1
                    ),
                    call.request(),
                    exception
                )
            )
            callMap.clear()
        }
    }

    private fun isHealthCheckRequest(call: Call): Boolean {
        return try {
            val url = call.request().url
            Params.getHostCheckUrl(url.host).equals(url.toString(), true)
        } catch (e: Exception) {
            false
        }
    }

    private fun collectData(data: OkHttpCheckData) {
        ongoingCheckCmd.remove(data.host)
        checkDataRecord.add(data)
        NPThreadPool.runCmd(ComputeHttpHealthCmd(data.host, checkDataRecord, this))
        NPThreadPool.runCmd(
            CleanCheckRecordCmd(Params.getHostHttpWinSize(data.host), checkDataRecord)
        )
        ForceCheckWorkAround.onRequestFinished(data)
    }

    private fun doActiveCheck(host: String, delay: Long, startHash: String = "") {
        if (delay < 0) return
        if (!Params.isForeground() || !Params.isNetConnected()) return
        if (ongoingCheckCmd.containsKey(host)) return
        val checkUrl = Params.getHostCheckUrl(host)
        if (checkUrl != null) {
            val request = Request.Builder().url(checkUrl).tag(String::class.java, startHash)
                .method(Params.getHostMethod(host), null).cacheControl(
                    CacheControl.FORCE_NETWORK
                ).build()
            client?.let {
                val checkCmd = HttpCheckCmd(delay, it, request)
                ongoingCheckCmd[host] = checkCmd
                NPThreadPool.runCmd(checkCmd, true)
            }
        }
    }

    private fun cancelOngoingCmd() {
        ongoingCheckCmd.values.forEach { NPThreadPool.removeCmd(it) }
        ongoingCheckCmd.clear()
    }

    override fun healthComputeDone(target: String, newValue: Float) {
        val currentValue = (healthMap[target]?.value ?: NetHealthLevel.UNKNOWN.lowValue)
        val healthLifeTime = Params.getHostHttpHealthLifeTime(target)
        healthMap[target] = NetHealthData(System.currentTimeMillis() + healthLifeTime, newValue)
        reporter.reportHealth(target, newValue)
        val currentLevel = NetHealthLevel.judgeHealthFromValue(currentValue)
        val newLevel = NetHealthLevel.judgeHealthFromValue(newValue)
        if (abs(currentValue - newValue) > 0.05 || currentLevel != newLevel) {
            //
        }
        doActiveCheck(target, (healthLifeTime * Params.getHostCheckThreshold(target)).toLong())
    }

    override fun start() {
        val clientBuilder =
            OkHttpClient.Builder().eventListener(eventListener).build()
        healthMap.clear()
        Params.getAllHost().forEach {
            healthMap[it] = NetHealthData(
                System.currentTimeMillis() + Params.getHostHttpHealthLifeTime(it),
                NetHealthLevel.UNKNOWN.lowValue
            )
            doActiveCheck(it, 0)
        }
    }

    override fun stop() {
        isStop = true
        checkDataRecord.clear()
        cancelOngoingCmd()
        healthMap.clear()
        client = null
    }

    override fun onGlobalStateChanged(isForeground: Boolean) {
        if (isForeground) {
            val now = System.currentTimeMillis()
            healthMap.forEach {
                val delay = it.value.validUtil - now
                doActiveCheck(it.key, if (delay < 0) 0 else delay)
            }
        } else {
            cancelOngoingCmd()
        }
    }

    override fun onNetworkChanged(netChanged: Boolean) {
        if (netChanged) {
            healthMap.keys.forEach {
                healthMap[it] =
                    NetHealthData(System.currentTimeMillis(), NetHealthLevel.UNKNOWN.lowValue)
                doActiveCheck(it, 0)
            }
        } else {
            checkDataRecord.clear()
            healthMap.keys.forEach {
                healthMap[it] =
                    NetHealthData(System.currentTimeMillis(), NetHealthLevel.DEAD.lowValue)
            }
            cancelOngoingCmd()
        }
    }

    override fun forceCheck() {
        cancelOngoingCmd()
        val hash = "forceCheck_" + System.currentTimeMillis().toString()
        ForceCheckWorkAround.start(healthMap.size, hash)
        healthMap.keys.forEach { doActiveCheck(it, 0, hash) }
    }

    override fun getHealth(key: String): Float? {
        val record = healthMap[key]
        if (record != null && record.isValid()) return record.value
        return null
    }

    interface CheckReporter {
        fun reportIpAddress(host: String, ipAddress: String)

        fun reportHealth(host: String, healthValue: Float)
    }
}