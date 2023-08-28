package com.fanji.android.net.netprobe.cmd

import com.fanji.android.net.netprobe.level.NetHealthLevel
import com.fanji.android.net.netprobe.listener.HealthComputeListener

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-2 下午5:42
 */
internal class ComputeOverallHealthCmd(
    private val host: String,
    private val httpHealth: Float?,
    private val pingHealth: Float?,
    private val listener: HealthComputeListener
) : AbstractCmd(CmdType.COMPUTE_OVERALL_HEATH, ExecutorType.COMPUTE, 0) {
    override fun run() {
        if (valid(httpHealth) && valid(pingHealth)) {
            listener.healthComputeDone(host, httpHealth!! * 0.5F + pingHealth!! * 0.5F)
        } else if (valid(httpHealth) && !valid(pingHealth)) {
            listener.healthComputeDone(host, httpHealth!!)
        } else if (!valid(httpHealth) && valid(pingHealth)) {
            listener.healthComputeDone(host, pingHealth!!)
        } else {
            listener.healthComputeDone(host, NetHealthLevel.UNKNOWN.lowValue)
        }
    }

    private fun valid(value: Float?): Boolean {
        return value != null && value != NetHealthLevel.UNKNOWN.lowValue
    }
}