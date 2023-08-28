package com.fanji.android.net.netprobe.cmd

import com.fanji.android.net.netprobe.NPThreadPool
import com.fanji.android.net.netprobe.NetProbe
import com.fanji.android.net.netprobe.Params
import kotlin.random.Random

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-2 下午5:53
 */
internal class SampleHostHealthCmd : AbstractCmd(
    CmdType.SAMPLE_HEALTH_LEVEL,
    ExecutorType.COMPUTE,
    Params.getLevelSampleInternal()
) {
    override fun run() {
        if(Params.isForeground() && Random.nextFloat() <= Params.getZaSampleRate()){
            if(Params.isZaSampleInternal()){
                Params.getAllInternalHostName().forEach {
                    val netValue = NetProbe.getNetHealthValue(it)
                }
            }
            if(Params.isZaSampleExternal()){
                Params.getAllExternalHostName().forEach {
                    val netValue = NetProbe.getNetHealthValue(it)
                }
            }
        }
        NPThreadPool.runCmd(SampleHostHealthCmd())
    }
}