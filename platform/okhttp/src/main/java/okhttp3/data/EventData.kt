package okhttp3.data

//import androidx.room.Entity

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-9 下午1:10
 */
//@Entity
open class EventData(
    var host: String? = "",//主机
    var path: String? = "",//请求action
    var method: String? = "",//请求方式
    var connectTimeout: Int = 0,//连接超时
    var readTimeout: Int = 0,//读取超时
    var writeTimeout: Int = 0,//写入超时
    var pingInterval: Int = 0,//心跳间隔
    var netState: Boolean = false,//网络状态
    var netLevel: Int = 0,//网络级别
    var netSpeed: Float = 0F,//网络速度
    var position: Int = 0,//请求路由位置
    var tls: String? = "",//请求tls版本
    var protocolType: String? = "",//请求协议
    var name: String? = "",
    var psw: String? = "",
    var dnsSize: Int = 0,//Dns长度
    var requestHeadersCount: Long = 0L,
    var requestHeadersSize: Int = 0,//请求内容大小
    var requestBodySize: Long? = 0L,//请求内容大小
    var responseHeadersCount: Long? = 0L,//
    var responseHeadersSize: Int? = 0,//响应内容大小
    var responseBodySize: Long? = 0L,//响应内容大小
) : BaseData()

const val UNKNOWN = 0
const val CONNECTING = 1
const val CONNECTED = 2
const val CONNECT_FAIL = -1