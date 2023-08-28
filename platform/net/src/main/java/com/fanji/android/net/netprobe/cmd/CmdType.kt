package com.fanji.android.net.netprobe.cmd

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午3:44
 */
internal enum class CmdType(val value: Int) {
    SHUTDOWN(-1),
    COMPUTE_OVERALL_HEATH(0),
    COMPUTE_HTTP_HEALTH(1),
    COMPUTE_PING_HEALTH(2),
    CLEAN_CHECK_RECORD(3),
    HTTP_TIMEOUT(4),
    SAMPLE_HEALTH_LEVEL(5),
    DO_HTTP_CHECK(6),
    DO_PING_CHECK(7)
}