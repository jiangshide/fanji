package com.fanji.android.net.preferred.speed.listener

import com.fanji.android.net.preferred.model.IpModel

/**
 * 针对速度探测的接口调用
 * @author 蒋世德
 * @email 18311271399@163.com
 * @since 21-3-18 下午1:07
 */
internal interface IChecker<T> {
    /**
     * 提供对外实现的速度探测调用接口的方法
     * @param ipModel 需要探测的ip源
     * @param listener 探测结果回调接口
     */
    fun check(ipModel: IpModel, listener: IResultListener<T>)
}