package com.fanji.android.net.preferred

import com.fanji.android.net.preferred.model.IpModel
import com.fanji.android.net.preferred.speed.SpeedCheck
import com.fanji.android.net.preferred.speed.listener.IResultListener
import com.fanji.android.util.SPUtil
import java.net.Inet4Address
import java.net.InetAddress
import java.util.concurrent.ConcurrentHashMap

/**
 * Ip优选api调用
 * @author 蒋世德
 * @email 18311271399@163.com
 * @since 21-3-18 下午12:57
 */
object IPPreferred {

    /**
     * 优选IP集合
     */
    private val addressHashMap = ConcurrentHashMap<String, List<IpModel>>()

    /**
     * 针对主机上次探测结束时的时间
     */
    private val hostTimeHashMap = ConcurrentHashMap<String, Long>()

    /**
     * 针对单个域名探测时的间隔最小时间
     */
    private const val INTERVALS = 5 * 60 * 1000

    @JvmStatic
    var isEnable = true

    /**
     * 默认优先级,优先级范围:1(高)~12(低)
     */
    private const val DEFAULT_PRIORITY = 12

    /**
     * 存储具有默认优先级的域名,支持云控下发进行替换或扩展其域名与优先级
     * 云控参数为：[ProbeHosts]
     */
    private val priorityMap = HashMap<String, Int>()

//    @Volatile
//    private var probeHosts: JsonNode? = null

    init {
        priorityMap["www.baidu.com"] = 1
    }

    /**
     * 外部调用探测入口，同时返回上次探测的结果数据(缓存:优选IP)
     * @param host 探测的主机域名
     * @param inetAddressList 探测的IP地址(DNS解析获取的IP源)
     * @param consumer 针对探测结果提供外部进行回调实现
     * @return List<InetAddress>? 返回已优选的IP列表，可以为空
     */
    @JvmStatic
    @JvmOverloads
    fun readAndUpdateAddressList(
        host: String,
        inetAddressList: List<InetAddress>? = null,
        listener: IResultListener<List<InetAddress>>? = null
    ): List<InetAddress>? {
        if (!isEnable) {
            return inetAddressList
        }
        if (!checkHostPriority(host)) {
            return inetAddressList
        }
        if (!checkIntervals(host)) {
            return inetAddressList
        }
        startProbe(host, inetAddressList, listener)
        if (addressHashMap.contains(host)) {
            return convertAddressList(addressHashMap[host])
        }
        return inetAddressList
    }

    /**
     * 核查探测时时候为安全时间内:
     * 当未探测过或上次探测的时间大于探测的间隔[INTERVALS]时间即为探测安全时间
     * @param host
     */
    private fun checkIntervals(host: String): Boolean {
        val time = hostTimeHashMap[host] ?: return true
        return (System.currentTimeMillis() - time) > INTERVALS
    }

    private fun startProbe(
        host: String,
        inetAddressList: List<InetAddress>?,
        listener: IResultListener<List<InetAddress>>?
    ) {
        val speedCheck = SpeedCheck()
        val addressList = ArrayList<IpModel>()
        val size = inetAddressList?.size
        var isProbeAllFail = true
        val priority = getPriority(host)
        inetAddressList?.forEach {
            val ipModel = checkIp(it.hostAddress)
            if (ipModel != null) {
                if (ipModel.rtt != Int.MAX_VALUE) {
                    isProbeAllFail = false
                }
                ipModel.host = it.hostName
                addressList.add(ipModel)
                sorted(host, addressList, size, listener, isProbeAllFail)
            } else {
                speedCheck.check(
                    IpModel(it.hostName, it.hostAddress, priority = priority),
                    object : IResultListener<IpModel> {
                        override fun result(it: IpModel) {
                            if (it.rtt != Int.MAX_VALUE) {
                                isProbeAllFail = false
                            }
                            addressList.add(it)
                            sorted(host, addressList, size, listener, isProbeAllFail)
                        }

                    })
            }
        }
    }

    private fun checkHostPriority(host: String): Boolean {
        return priorityMap.containsKey(host)
    }

    /**
     * 根据域名获得探测优先级
     */
    fun getPriority(host: String): Int {
        if (priorityMap.containsKey(host)) {
            return priorityMap[host]!!
        }
        return DEFAULT_PRIORITY
    }

    /**
     * 检查当前IP是否已经探测过，如果已经探测过(探测成功)并且离上次探测时间小于探测的间隔时间[INTERVALS]就不需要探测了
     * @param ip
     * @return IpModel
     */
    private fun checkIp(ip: String): IpModel? {
        hostTimeHashMap.entries.sortedByDescending { it.value }.forEach {
            addressHashMap[it.key]?.forEach { it ->
                if (ip == it.ip) {
                    val intervals = System.currentTimeMillis() - it.endTime
                    if (intervals > INTERVALS && it.rtt != Int.MAX_VALUE) {
                        return it
                    }
                }
            }
        }
        return null
    }

    private fun convertAddressList(list: List<IpModel>?): List<InetAddress> {
        val preferredAddressList = ArrayList<InetAddress>()
        list?.forEach {
            preferredAddressList.add(
                InetAddress.getByAddress(
                    it.host, InetAddress.getByName(it.ip).address
                )
            )
        }
        return preferredAddressList
    }

    /**
     * 针对探测结果列表通过rtt进行排序
     * @param addressList
     */
    private fun sorted(
        host: String, addressList: List<IpModel>, size: Int?,
        listener: IResultListener<List<InetAddress>>?, isProbeAllFail: Boolean
    ) {
        if (addressList.size == size) {
            if (!isProbeAllFail) {
                val sortedList = addressList.sortedBy { it -> it.rtt }
                addressHashMap[host] = sortedList
                hostTimeHashMap[host] = System.currentTimeMillis()
//                Okhttp.forceEvictByHost(host)
                listener?.result(convertAddressList(sortedList))

                val ipModelList = arrayListOf<IpModel>()
                sortedList.forEach {
                    val inetAddress = InetAddress.getByAddress(
                        it.host, InetAddress.getByName(it.ip).address
                    )
                    if (inetAddress is Inet4Address) {
                        ipModelList.add(it)
                    }
                }
                SPUtil.put(host, ipModelList)
            }
        }
    }

    fun getInetAddressListFromSP(host: String): List<InetAddress>? {
        val addressList =
            SPUtil.getList(host, IpModel::class.java)?.sortedBy { it -> it.rtt }
        if (addressList != null) {
            return convertAddressList(addressList)
        }
        return null
    }

    /**
     * 探测结果获取：提供外部主动调用
     * @param host 主机域名：通过传入的主机域名从缓存中获取探测结果
     * @return List<InetAddress> 返回优选的IP列表
     */
    fun getInetAddressList(host: String): List<InetAddress>? {
        return convertAddressList(addressHashMap[host])
    }

    /**
     * 清除缓存
     */
    fun clearAddressList() {
        addressHashMap.clear()
    }
}