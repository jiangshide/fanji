package com.fanji.android.net.netprobe.okhttp

import com.fanji.android.net.netprobe.cmd.HttpTimeOutCmd

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午3:40
 */
internal class CallRecord(
    var startTime: Long,
    val timeOutCmd: HttpTimeOutCmd
)