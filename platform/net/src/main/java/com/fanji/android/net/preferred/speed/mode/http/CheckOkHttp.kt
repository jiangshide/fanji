package com.fanji.android.net.preferred.speed.mode.http

import com.fanji.android.net.preferred.dns.ProbeDns
import com.fanji.android.net.preferred.model.IpModel
import com.fanji.android.net.preferred.speed.listener.IResultListener
import okhttp3.*
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit

/**
 * Http探测请求
 * @author 蒋世德
 * @email 18311271399@163.com
 * @since 21-3-24 下午7:35
 */
internal class CheckOkHttp(
    private val ipModel: IpModel,
    private val client: OkHttpClient,
    private val listener: IResultListener<IpModel>
) : Runnable, Comparable<CheckOkHttp> {

    /**
     * @return OkHttpClient
     */
    private fun getClient(): OkHttpClient {
        ipModel.startTime = System.currentTimeMillis()
        return client
            .newBuilder().dns(ProbeDns(ipModel)).connectTimeout(5, TimeUnit.SECONDS)
            .eventListener(eventListener).build()
    }

    override fun run() {
        var response: Response? = null
        try {
            response = getClient().newCall(
                Request.Builder().get().url("https://www.baidu.com/").build()
            ).execute()
        } catch (e: Exception) {
        } finally {
            try {
                response?.close()
            } catch (e: Exception) {
            }
        }
    }

    private val eventListener = object : EventListener() {
        override fun connectFailed(
            call: Call,
            inetSocketAddress: InetSocketAddress,
            proxy: Proxy,
            protocol: Protocol?,
            ioe: IOException
        ) {
            super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe)
            val endTime = System.currentTimeMillis()
            val duration = endTime - ipModel.startTime
            ipModel?.rtt = Int.MAX_VALUE
            ipModel?.endTime = endTime
            ipModel?.exception = ioe
            listener?.result(ipModel)
        }

        override fun responseHeadersEnd(call: Call, response: Response) {
            super.responseHeadersEnd(call, response)
            val serviceDuration = response.header("x-backend-response")?.toFloatOrNull() ?: 0f
            val endTime = System.currentTimeMillis()
            val duration = endTime - ipModel.startTime
            ipModel?.rtt = duration.toInt()
            ipModel?.endTime = endTime
            listener?.result(ipModel)
        }
    }

    override fun compareTo(other: CheckOkHttp): Int {
        return when {
            ipModel.priority > other.ipModel.priority -> {
                1
            }
            ipModel.priority == other.ipModel.priority -> {
                0
            }
            else -> {
                -1
            }
        }
    }
}