package com.fanji.android.net.netprobe.ping

import com.fanji.android.net.netprobe.Params
import com.fanji.android.net.netprobe.data.CheckData

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午5:03
 */
internal class PingCheckData(val ipAddress: String) :
    CheckData(System.currentTimeMillis() + Params.getPingWinSize()) {
    var pingTitle: PingTitle? = null
    var responseList = mutableListOf<PingResponse>()
    var pingStatistic: PingStatistic? = null
    var rttStatistic: RTTStatistic? = null
}

internal data class PingTitle(
    val resolvedHost: String?,
    val ipAddress: String?,
    val packSize: Int
)

internal abstract class PingResponse

internal data class PingEchoResponse(
    val packSize: Int,
    val host: String,
    val ipAddress: String?,
    val seq: Int,
    val ttl: Int,
    val time: Float
) : PingResponse()

internal data class PingErrorResponse(val type: Int = INVALID) : PingResponse() {
    companion object {
        const val INVALID = -1
        const val TIME_OUT = 0
        const val UNREACHABLE = 1
        const val BAD_IP_ADDRESS = 2
        const val UNKNOWN_HOST = 4
    }
}

internal data class PingStatistic(
    val packSent: Int,
    val packReceived: Int,
    val packLossRate: Float,
    val duration: Long
) : PingResponse()

internal data class RTTStatistic(
    val minRtt: Float,
    val avgRtt: Float,
    val maxRtt: Float,
    val mdevRtt: Float
) : PingResponse()