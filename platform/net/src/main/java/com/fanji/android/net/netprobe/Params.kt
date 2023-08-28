package com.fanji.android.net.netprobe

import com.fanji.android.net.netprobe.Params.DEFAULT_HTTP_COMPUTE_WIN_SIZE
import com.fanji.android.net.netprobe.Params.DEFAULT_HTTP_HEALTH_LIFETIME
import com.fanji.android.net.netprobe.Params.DEFAULT_HTTP_REQUEST_TIMEOUT
import com.fanji.android.net.netprobe.Params.DEFAULT_HTTP_SMOOTH_FACTOR
import com.fanji.android.net.netprobe.data.ProbeData
import com.fanji.android.net.netprobe.listener.IpChangedListener
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * {
"hosts":[
{
"compare":true,
"name":"www.fanji.com",
"checkThreshold":0.8,
"isInternal":true,
"requestTimeout":5000,
"checkUrl":"https://www.fanji.com/check_health",
"smoothFactor":800,
"winSize":30000,
"healthLifetime":20000,
"method":"GET"
},
{
"compare":true,
"name":"api.fanji.com",
"checkThreshold":0.8,
"isInternal":true,
"requestTimeout":5000,
"checkUrl":"https://api.fanji.com/check_health",
"smoothFactor":800,
"winSize":30000,
"healthLifetime":20000,
"method":"GET"
},
{
"name":"zhuanlan.fanji.com",
"checkThreshold":-1,
"isInternal":true,
"requestTimeout":5000,
"checkUrl":"https://zhuanlan.fanji.com/check_health",
"smoothFactor":800,
"winSize":30000,
"healthLifetime":20000,
"method":"GET"
},
{
"name":"pic1.zhimg.com",
"checkThreshold":-1,
"isInternal":true,
"requestTimeout":5000,
"checkUrl":"https://pic1.zhimg.com/check_health",
"smoothFactor":800,
"winSize":30000,
"healthLifetime":20000,
"method":"GET"
},
{
"name":"pic2.zhimg.com",
"checkThreshold":-1,
"isInternal":true,
"requestTimeout":5000,
"checkUrl":"https://pic2.zhimg.com/check_health",
"smoothFactor":800,
"winSize":30000,
"healthLifetime":20000,
"method":"GET"
},
{
"name":"pic3.zhimg.com",
"checkThreshold":-1,
"isInternal":true,
"requestTimeout":5000,
"checkUrl":"https://pic3.zhimg.com/check_health",
"smoothFactor":800,
"winSize":30000,
"healthLifetime":20000,
"method":"GET"
},
{
"name":"pic4.zhimg.com",
"checkThreshold":-1,
"isInternal":true,
"requestTimeout":5000,
"checkUrl":"https://pic4.zhimg.com/check_health",
"smoothFactor":800,
"winSize":30000,
"healthLifetime":20000,
"method":"GET"
},
{
"name":"sugar.fanji.com",
"checkThreshold":-1,
"isInternal":true,
"requestTimeout":5000,
"checkUrl":"https://sugar.fanji.com/check_health",
"smoothFactor":800,
"winSize":30000,
"healthLifetime":20000,
"method":"GET"
},
{
"name":"static.fanji.com",
"checkThreshold":-1,
"isInternal":true,
"requestTimeout":5000,
"checkUrl":"http://static.fanji.com/check_health",
"smoothFactor":800,
"winSize":30000,
"healthLifetime":20000,
"method":"HEAD"
},
{
"name":"www.baidu.com",
"checkThreshold":-1,
"isInternal":false,
"requestTimeout":5000,
"checkUrl":"https://www.baidu.com",
"smoothFactor":800,
"winSize":50000,
"healthLifetime":40000,
"method":"HEAD"
},
{
"name":"www.qq.com",
"checkThreshold":-1,
"isInternal":false,
"requestTimeout":5000,
"checkUrl":"https://www.qq.com",
"smoothFactor":800,
"winSize":50000,
"healthLifetime":40000,
"method":"HEAD"
},
{
"name":"www.taobao.com",
"checkThreshold":-1,
"isInternal":false,
"requestTimeout":5000,
"checkUrl":"https://www.taobao.com",
"smoothFactor":800,
"winSize":50000,
"healthLifetime":40000,
"method":"HEAD"
}
],
"ping":{
"healthLifetime":40000,
"winSize":30000,
"smoothFactor":300,
"checkThreshold":0.8
},
"za":{
"compare":true,
"zaSampleRate":1,
"levelSampleInterval":20000,
"sampleInternal":true,
"sampleExternal":true
}
}
 *
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午3:56
 */
internal object Params {

    const val DEFAULT_HTTP_COMPUTE_WIN_SIZE = 30 * 1000L

    const val DEFAULT_HTTP_HEALTH_LIFETIME = 20 * 1000L

    const val DEFAULT_HTTP_REQUEST_TIMEOUT = 5 * 1000L

    const val DEFAULT_HTTP_SMOOTH_FACTOR = 800.0F

    const val DEFAULT_PING_COMPUTE_WIN_SIZE = 30 * 1000L

    const val DEFAULT_PING_HEALTH_LIFETIME = 40 * 1000L

    const val DEFAULT_PING_SMOOTH_FACTOR = 300.0F

    const val DEFAULT_ZA_SAMPLE_RATE = 0.1F

    const val DEFAULT_LEVEL_SAMPLE_INTERVAL = 5 * 60 * 1000L

    @Volatile
    private var params: ProbeData? = null

    private val hostMap = ConcurrentHashMap<String, Host>()

    private var ipChangedListener: IpChangedListener? = null

    @Volatile
    private var isForeground = true

    @Volatile
    private var netConnected = true

    fun init() {

    }

    fun unInit() {
        hostMap?.clear()
    }

    fun setIpChangedListener(listener: IpChangedListener) {
        this.ipChangedListener = listener
    }

    fun updateNetState(isConnected: Boolean) {
        this.netConnected = isConnected
    }

    fun isNetConnected(): Boolean {
        return netConnected
    }

    fun updateHostAndIp(host: String, ipAddress: String) {
        hostMap[host]?.let {
            val oldAddress = it.ipAddress
            it.ipAddress = ipAddress
            if (ipChangedListener != null && oldAddress != ipAddress) {
                ipChangedListener?.onIpChanged(host, oldAddress, ipAddress)
            }
        }
    }

    fun getIpAddress(host: String): String? {
        return hostMap[host]?.ipAddress
    }

    fun getHosts(ipAddress: String): List<String> {
        val list = mutableListOf<String>()
        hostMap.forEach {
            if (it.value.ipAddress == ipAddress) {
                list.add(it.key)
            }
        }
        return list
    }

    fun isIpActive(ipAddress: String): Boolean {
        hostMap.values.forEach {
            if (it.ipAddress == ipAddress) {
                return true
            }
        }
        return false
    }

    fun getHostMethod(host: String): String {
        return hostMap[host]?.params?.method ?: "HEAD"
    }

    fun getSameAddressHosts(host: String): List<String> {
        val ipAddress = getIpAddress(host)
        if (ipAddress != null) {
            val list = mutableListOf<String>()
            hostMap.forEach {
                if (it.value.ipAddress == ipAddress && it.key != host) {
                    list.add(it.key)
                }
            }
            return list
        }
        return listOf()
    }

    fun getAllHost(): List<String> {
        return hostMap.keys.toList()
    }

    fun getAllInternalHostName(): List<String> {
        return hostMap.values.filter { it.params.isInternal }.map { it.name }
    }

    fun getAllInternalHost(): List<Host> {
        return hostMap.values.filter { it.params.isInternal }
    }

    fun getAllExternalHostName(): List<String> {
        return hostMap.values.filter { !it.params.isInternal }.map { it.name }
    }

    fun getAllExternalHost(): List<Host> {
        return hostMap.values.filter { !it.params.isInternal }
    }

    fun isHostWatching(host: String): Boolean {
        return hostMap.contains(host)
    }

    fun updateState(isForeground: Boolean) {
        this.isForeground = isForeground
        NetProbe.globalStateChange(isForeground)
    }

    fun isForeground(): Boolean {
        return isForeground
    }

    fun isHostInternal(host: String): Boolean {
        return hostMap[host]?.params?.isInternal ?: false
    }

    fun getHostHttpWinSize(host: String): Long {
        return hostMap[host]?.params?.winSize ?: 0
    }

    fun getHostHttpHealthLifeTime(host: String): Long {
        return hostMap[host]?.params?.healthLifeTime ?: 0
    }

    fun getHostHttpRequestTimeOut(host: String): Long {
        return hostMap[host]?.params?.requestTimeOut ?: 0
    }

    fun getHttpSmoothFactor(host: String): Float {
        return hostMap[host]?.params?.smoothFactor ?: 0F
    }

    fun getHostCheckUrl(host: String): String? {
        return hostMap[host]?.params?.checkUrl
    }

    fun getHostCheckThreshold(host: String): Float {
        return hostMap[host]?.params?.checkThreshold ?: 0.8F
    }

    fun getPingWinSize(): Long {
        return params?.ping?.winSize ?: DEFAULT_PING_COMPUTE_WIN_SIZE
    }

    fun getPingHealthLifeTime(): Long {
        return params?.ping?.healthLifetime ?: DEFAULT_PING_HEALTH_LIFETIME
    }

    fun getPingCheckThreshold(): Float {
        return params?.ping?.checkThreshold ?: 0.8F
    }

    fun getPingSmoothFactor(): Float {
        return params?.ping?.smoothFactor ?: DEFAULT_PING_SMOOTH_FACTOR
    }

    fun getZaSampleRate(): Float {
        return params?.za?.zaSampleRate ?: DEFAULT_ZA_SAMPLE_RATE
    }

    fun getLevelSampleInternal(): Long {
        return params?.za?.levelSampleInterval ?: DEFAULT_LEVEL_SAMPLE_INTERVAL
    }

    fun isZaSampleInternal(): Boolean {
        return params?.za?.sampleInternal ?: true
    }

    fun isZaSampleExternal(): Boolean {
        return params?.za?.sampleExternal ?: false
    }

    fun isZaCompare(): Boolean {
        return params?.za?.compare ?: false
    }

    fun ignoreConnection(): Boolean {
        return params?.ignoreConnection ?: false
    }


    class Host(
        val name: String,
        val params: HostParams,
        var ipAddress: String?
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Host

            if (name != other.name) return false

            return true
        }

        override fun hashCode(): Int {
            return name.hashCode()
        }

        fun canCompare(): Boolean {
            return params.compare;
        }
    }
}

data class HostParams(
    val name: String = "",
    val compare: Boolean = false,
    val isInternal: Boolean = true,
    val winSize: Long = DEFAULT_HTTP_COMPUTE_WIN_SIZE,
    val healthLifeTime: Long = DEFAULT_HTTP_HEALTH_LIFETIME,
    val requestTimeOut: Long = DEFAULT_HTTP_REQUEST_TIMEOUT,
    val smoothFactor: Float = DEFAULT_HTTP_SMOOTH_FACTOR,
    val checkUrl: String = "",
    val checkThreshold: Float = -1.0F,
    val method: String = ""
)