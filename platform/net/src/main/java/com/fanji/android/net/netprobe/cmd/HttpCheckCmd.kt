package com.fanji.android.net.netprobe.cmd

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.closeQuietly

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-2 上午11:17
 */
internal class HttpCheckCmd(
    delay: Long,
    private val client: OkHttpClient,
    private val request: Request
) : AbstractCmd(CmdType.DO_HTTP_CHECK, ExecutorType.IO, delay) {
    override fun run() {
        var response: Response? = null
        try {
            response = client.newCall(request).execute()
        } finally {
            response?.closeQuietly()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HttpCheckCmd

        if (request.url.host != other.request.url.host) return false

        return true
    }

    override fun hashCode(): Int {
        return request.url.host.hashCode()
    }
}