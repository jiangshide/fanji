package com.fanji.android.net.preferred.speed

import com.fanji.android.net.preferred.model.IpModel
import com.fanji.android.net.preferred.speed.listener.IChecker
import com.fanji.android.net.preferred.speed.listener.IResultListener
import com.fanji.android.net.preferred.speed.mode.HttpChecker

/**
 * 速度探测管理类：内部调用
 * @author 蒋世德
 * @email 18311271399@163.com
 * @since 21-3-18 下午1:06
 */
internal class SpeedCheck : IChecker<IpModel> {

    /**
     * 探测模型容器列表
     */
    private val speedList = arrayListOf(HttpChecker())

    /**
     *探测模型调用入口
     * @param ipModel 探测IP源
     * @param listener 用于探测结果回调
     * @param size 当前探测数组大小
     */
    override fun check(ipModel: IpModel, listener: IResultListener<IpModel>) {
        speedList.forEach {
            if (it.isActivate()) {
                it.check(ipModel, listener)
            }
        }
    }

}