package com.fanji.android.net.preferred.speed.base

import com.fanji.android.net.preferred.model.IpModel
import com.fanji.android.net.preferred.speed.listener.IChecker
import com.fanji.android.net.preferred.speed.listener.IResultListener
import java.util.concurrent.ConcurrentHashMap

/**
 * 针对速度探测的抽象类，提供其它类需要速度探测功能时继承这个类进行实现支持
 * @author 蒋世德
 * @email 18311271399@163.com
 * @since 21-3-18 下午1:09
 */
abstract class BaseSpeed : IChecker<IpModel> {

    /**
     * 优先级
     */
    val PRIORITY_HTTP = 1

    /**
     * 默认激活状态：true
     */
    val ACTIVATE = true

    /**
     * 提供对外实现速度探测调用接口的方法
     * @param ipModel 需要探测的ip源
     * @param listener 探测结果回调接口
     */
    override fun check(ipModel: IpModel, listener: IResultListener<IpModel>) {}

    /**
     * 探测优先级
     * @return Int 返回优先级
     */
    abstract fun priority(): Int

    /**
     * 探测时候被激活，默认激活：true
     * @return 返回激活状态
     */
    abstract fun isActivate(): Boolean
}

/**
 * 标记探测优先级的存储容器
 */
internal val priorityMap = ConcurrentHashMap<String, Int>()

/**
 * 标记探测时候激活的存储容器
 */
internal val activateMap = ConcurrentHashMap<String, Boolean>()