package com.fanji.android.net.netprobe.ping.cmd

import com.fanji.android.net.netprobe.Params
import com.fanji.android.net.netprobe.cmd.AbstractCmd
import com.fanji.android.net.netprobe.cmd.CmdType
import com.fanji.android.net.netprobe.cmd.ExecutorType
import com.fanji.android.net.netprobe.listener.HealthComputeListener
import com.fanji.android.net.netprobe.ping.PingCheckData

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-2 下午3:54
 */
internal class ComputePingHealthCmd(
    private val ipAddress: String,
    private val records: Collection<PingCheckData>,
    private val listener: HealthComputeListener
) : AbstractCmd(CmdType.COMPUTE_PING_HEALTH, ExecutorType.COMPUTE, 0) {
    override fun run() {
        var totalAvgRtt = 0F
        var totalMdevRtt = 0F
        var totalLossRate = 0F
        records.forEach {
            totalAvgRtt += it.rttStatistic?.avgRtt ?: 0F
            totalMdevRtt += it.rttStatistic?.mdevRtt ?: 0F
            totalLossRate += it.pingStatistic?.packLossRate ?: 1F
        }
        val avgRtt = totalAvgRtt / records.size
        val avgMdevRtt = totalMdevRtt / records.size
        val avgLossRate = totalLossRate / records.size
        val value =
            (1.0F - (avgRtt + avgMdevRtt) / ((avgRtt + avgMdevRtt) + Params.getPingSmoothFactor())) * (1.0F - avgLossRate)
        listener?.healthComputeDone(ipAddress, value)
    }
}