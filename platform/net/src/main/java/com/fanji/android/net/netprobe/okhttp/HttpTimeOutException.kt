package com.fanji.android.net.netprobe.okhttp

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午3:52
 */
internal class HttpTimeOutException(val host: String) : Exception("Http request timeout:host$host")