package com.fanji.android.net.netprobe.listener

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午1:38
 */
internal interface Checker {
    fun start()
    fun stop()
    fun onGlobalStateChanged(isForeground: Boolean)
    fun onNetworkChanged(netChanged: Boolean)
    fun forceCheck()
    fun getHealth(key: String): Float?
}