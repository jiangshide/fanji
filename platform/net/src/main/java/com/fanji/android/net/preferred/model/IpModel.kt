package com.fanji.android.net.preferred.model

/**
 * @author 蒋世德
 * @email 18311271399@163.com
 * @since 21-3-18 下午1:02
 */
data class IpModel(
        var host: String = "",//主机
        val ip: String = "",//真实连接的ip地址（来自于DNS源）
        var startTime: Long = 0,//探测开始时间
        var endTime: Long = 0,//探测结束时间
        var rtt: Int = 0,//探测所用时间
        val ips: List<String>? = null, //ip集合
        var exception: Exception? = null, //网络请求失败时异常提示
        var priority: Int = 12//默认优先级:高到低(1~12)
) {
    override fun toString(): String {
        return "IpModel(host='$host', ip='$ip', startTime=$startTime, endTime=$endTime, rtt=$rtt, ips=$ips, exception=$exception, priority=$priority)"
    }
}