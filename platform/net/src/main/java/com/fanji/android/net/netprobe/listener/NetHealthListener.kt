package com.fanji.android.net.netprobe.listener

import com.fanji.android.net.netprobe.level.NetHealthLevel

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午12:57
 */
abstract class NetHealthListener {
    fun onHealthValueChanged(host:String,oldValue:Float,newValue:Float){}

    fun onHealthLevelChanged(host:String,oldLevel:NetHealthLevel,newLevel:NetHealthLevel){}
}