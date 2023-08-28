package com.fanji.android.net.netprobe.cmd

import com.fanji.android.net.netprobe.Params
import com.fanji.android.net.netprobe.listener.HealthComputeListener
import com.fanji.android.net.netprobe.okhttp.OkHttpCheckData

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-2 上午11:46
 */
internal class ComputeHttpHealthCmd(
    private val host: String,
    private val records: Collection<OkHttpCheckData>,
    private val listener: HealthComputeListener
) : AbstractCmd(CmdType.COMPUTE_HTTP_HEALTH, ExecutorType.COMPUTE, 0) {
    override fun run() {
        var totalDuration = 0L
        var failCount = 0
        val dataSize = records.size
        if (dataSize == 0) return
        records.forEach {
            if (it.exception == null) {
                totalDuration += it.duration
            } else {
                failCount++
            }

            val httpDuration = totalDuration / dataSize
            val failRate = failCount * 1.0F / dataSize

            val healthValue =
                (1.0F - httpDuration * 1.0F / (httpDuration + Params.getHttpSmoothFactor(host))) * (1.0F - failRate)
            listener?.healthComputeDone(host, healthValue)
        }
    }
}