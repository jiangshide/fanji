package com.fanji.android.net.netprobe.cmd

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午3:41
 */
internal class HttpTimeOutCmd(
    private val host: String,
    delay: Long,
    private val block: () -> Unit
) : AbstractCmd(CmdType.HTTP_TIMEOUT, ExecutorType.COMPUTE, delay) {
    override fun run() {
        block.invoke()
    }

}