package com.fanji.android.net.netprobe.ping.cmd

import android.text.TextUtils
import com.fanji.android.net.netprobe.cmd.AbstractCmd
import com.fanji.android.net.netprobe.cmd.CmdType
import com.fanji.android.net.netprobe.cmd.ExecutorType
import com.fanji.android.net.netprobe.ping.*
import okhttp3.internal.closeQuietly
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.regex.Pattern

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-2 下午3:18
 */
internal class PingProcessCheckCmd(
    private val ipAddress: String,
    delay: Long,
    private val icmpCount: Int = 5,
    private val callback: PingCheckCallback
) : AbstractCmd(CmdType.DO_PING_CHECK, ExecutorType.IO, delay) {

    private val ping = "ping"
    private val ping6 = "ping6"

    private val ipV4Pattern = Pattern.compile(
        "((?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))(?![\\d])",
        Pattern.CASE_INSENSITIVE or Pattern.DOTALL
    )

    private val ipV6Pattern = Pattern.compile(
        "(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|" +
                // 1::
                "([0-9a-fA-F]{1,4}:){1,7}:|" +
                "([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|" +
                "([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|" +
                "([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|" +
                "([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|" +
                "([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|" +
                "[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:" +
                "((:[0-9a-fA-F]{1,4}){1,7}|:)|" +
                "fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|" +
                "::(ffff(:0{1,4}){0,1}:){0,1}" +
                "((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}" +
                "(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|" +
                "([0-9a-fA-F]{1,4}:){1,4}:" +
                "((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}" +
                "(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))",
        Pattern.CASE_INSENSITIVE or Pattern.DOTALL
    )

    override fun run() {
        var stdOut: BufferedReader? = null
        try {
            val cmd: String = when {
                isIpV4(ipAddress) -> ping
                isIpV6(ipAddress) -> ping6
                else -> return
            }
            val pb = ProcessBuilder(cmd, "-c $icmpCount", ipAddress)
            pb.redirectErrorStream(true)
            val process = pb.start()
            stdOut = BufferedReader(InputStreamReader(process.inputStream))
            val data = PingCheckData(ipAddress)
            getPingTitle(stdOut.readLine(), data)
            while (true) {
                val outLine = stdOut.readLine() ?: break
                getPingResponse(outLine, data)
            }
            callback.onFinish(data)
        } finally {
            stdOut?.closeQuietly()
        }
    }

    private fun getPingTitle(title: String, data: PingCheckData) {
        var ipAddress: String? = null
        var packSize: Int = -1
        val segments = title.split(" ")
        val resolvedHost = segments[1]
        val ipString = segments[2].substring(1, segments[2].length - 1)
        if (ipV4Pattern.matcher(ipString).matches() || ipV6Pattern.matcher(ipAddress).matches()) {
            ipAddress = ipString
            packSize = segments[3].substring(0, segments[3].indexOf("(")).toInt()
        }
        data.pingTitle = PingTitle(resolvedHost, ipAddress, packSize)
    }

    private fun getPingResponse(response: String, data: PingCheckData) {
        when {
            TextUtils.isEmpty(response) -> return
            response.contains(" ping statistics ---") -> return
            response.contains(" bytes from ") -> getEchoResponse(response, data)
            response.contains(" packets transmitted, ") -> getPingStat(response, data)
            response.contains("rtt min/avg/max/mdev = ") -> getRttStat(response, data)
            else -> getErrorResponse(data)
        }
    }

    private fun getErrorResponse(data: PingCheckData) {
        data.responseList.add(PingErrorResponse())
    }

    private fun getRttStat(response: String, data: PingCheckData) {
        val segments = response.split(" ")
        val rtts = segments[segments.size - 2].split("/")
        data.rttStatistic =
            RTTStatistic(rtts[0].toFloat(), rtts[1].toFloat(), rtts[2].toFloat(), rtts[3].toFloat())
    }

    private fun getEchoResponse(response: String, data: PingCheckData) {
        val segments = response.split(" ")
        val packSize = segments[0].toInt()
        val host = segments[3]
        val seq = segments[segments.size - 4].split("=")[1].toInt()
        val ttl = segments[segments.size - 3].split("=")[1].toInt()
        val time = segments[segments.size - 2].split("=")[1].toFloat()
        data.responseList.add(
            PingEchoResponse(
                packSize,
                host,
                data.pingTitle!!.ipAddress,
                seq,
                ttl,
                time
            )
        )
    }

    private fun getPingStat(response: String, data: PingCheckData) {
        val segments = response.split(" ")
        val packSent = segments[0].toInt()
        val packReceived = segments[3].toInt()
        val lossRate = segments[5].substring(0, segments[5].length - 1).toFloat() / 100F
        val duration =
            segments[segments.size - 1].substring(0, segments[segments.size - 1].length - 2)
                .toLong()
        data.pingStatistic = PingStatistic(packSent, packReceived, lossRate, duration)
    }

    private fun isIpV4(ipAddress: String): Boolean {
        return ipV4Pattern.matcher(ipAddress).matches()
    }

    private fun isIpV6(ipAddress: String): Boolean {
        return ipV6Pattern.matcher(ipAddress).matches()
    }

    interface PingCheckCallback {
        fun onFinish(data: PingCheckData)
    }
}
