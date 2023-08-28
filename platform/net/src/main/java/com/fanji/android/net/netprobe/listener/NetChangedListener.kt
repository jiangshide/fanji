package com.fanji.android.net.netprobe.listener

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午1:37
 */
internal interface NetChangedListener {
    fun onNetChanged(isConnected:Boolean)
}