package com.fanji.android.net.netprobe.listener

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午1:36
 */
internal interface IpChangedListener {
    fun onIpChanged(host:String, oldIp:String?, newIp:String)
}