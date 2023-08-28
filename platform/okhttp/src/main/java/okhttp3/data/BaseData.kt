package okhttp3.data

//import androidx.room.Entity
//import androidx.room.PrimaryKey

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-11 下午3:41
 */
//@Entity
open class BaseData(
//    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var url: String? = "",//请求URl
    var contentLength: Long = 0L,//内容大小
    var code: String = "-1",//响应Code
    var exception: String? = "",//异常信息
    var priority: Int = 12,//默认优先级:高到低(1~12)
    var status: Int = UNKNOWN,//状态:0~未执行,1~连接中,2~连接成功,-1~连接失败
    var startTime: Long = 0L,//开始时间
    var endTime: Long = 0L,//结束时间
    var rtt: Long = 0,//执行时间
    var step: Int = 0,//下载步骤
    var ip: String? = "",//请求远程地址
    var port: Int = 0,//请求远程端口
)