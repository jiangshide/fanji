package com.fanji.android.net.netprobe.data

import com.fanji.android.net.netprobe.Params.DEFAULT_HTTP_COMPUTE_WIN_SIZE
import com.fanji.android.net.netprobe.Params.DEFAULT_HTTP_HEALTH_LIFETIME
import com.fanji.android.net.netprobe.Params.DEFAULT_HTTP_REQUEST_TIMEOUT
import com.fanji.android.net.netprobe.Params.DEFAULT_HTTP_SMOOTH_FACTOR
import com.fanji.android.net.netprobe.Params.DEFAULT_LEVEL_SAMPLE_INTERVAL
import com.fanji.android.net.netprobe.Params.DEFAULT_PING_COMPUTE_WIN_SIZE
import com.fanji.android.net.netprobe.Params.DEFAULT_PING_HEALTH_LIFETIME
import com.fanji.android.net.netprobe.Params.DEFAULT_PING_SMOOTH_FACTOR
import com.fanji.android.net.netprobe.Params.DEFAULT_ZA_SAMPLE_RATE

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-2 下午4:12
 */
data class ProbeData(
    val za: Za,
    val ping: Ping,
    val hosts: List<Hosts>,
    val ignoreConnection: Boolean = false
)

data class Za(
    val zaSampleRate: Float = DEFAULT_ZA_SAMPLE_RATE,
    val sampleInternal: Boolean = true,
    val levelSampleInterval: Long = DEFAULT_LEVEL_SAMPLE_INTERVAL,
    val sampleExternal: Boolean = false,
    val compare: Boolean = false
)

data class Ping(
    val healthLifetime: Long = DEFAULT_PING_HEALTH_LIFETIME,
    val winSize: Long = DEFAULT_PING_COMPUTE_WIN_SIZE,
    val smoothFactor: Float = DEFAULT_PING_SMOOTH_FACTOR,
    val checkThreshold: Float = -1.0F
)

data class Hosts(
    val name: String = "",
    val checkThreshold: Float = -1.0F,
    val compare: Boolean = false,
    val isInternal: Boolean = true,
    val requestTimeout: Long = DEFAULT_HTTP_REQUEST_TIMEOUT,
    val checkUrl: String,
    val smoothFactor: Float = DEFAULT_HTTP_SMOOTH_FACTOR,
    val winSize: Long = DEFAULT_HTTP_COMPUTE_WIN_SIZE,
    val healthLifetime: Long = DEFAULT_HTTP_HEALTH_LIFETIME,
    val method: String
)