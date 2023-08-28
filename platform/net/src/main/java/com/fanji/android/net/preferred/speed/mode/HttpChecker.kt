package com.fanji.android.net.preferred.speed.mode

import com.fanji.android.net.preferred.model.IpModel
import com.fanji.android.net.preferred.speed.base.BaseSpeed
import com.fanji.android.net.preferred.speed.base.activateMap
import com.fanji.android.net.preferred.speed.base.priorityMap
import com.fanji.android.net.preferred.speed.listener.IResultListener
import com.fanji.android.net.preferred.speed.mode.http.CheckOkHttp
import com.fanji.android.util.ThreadPoolUtil
import okhttp3.OkHttpClient

/**
 * @author 蒋世德
 * @email 18311271399@163.com
 * @since 21-3-18 下午1:11
 */
internal class HttpChecker : BaseSpeed() {

    private val HTTP = "http"

    private val client = OkHttpClient()

    init {
        priorityMap[HTTP] = PRIORITY_HTTP
        activateMap[HTTP] = ACTIVATE
    }

    /**
     * @param ipModel 探测IP源
     * @param listener 用于探测结果回调
     */
    override fun check(ipModel: IpModel, listener: IResultListener<IpModel>) {
        super.check(ipModel, listener)
        ThreadPoolUtil.addTask("IPPreferred", CheckOkHttp(ipModel, client, listener))//开始启动Http请求进行探测
    }

    override fun priority(): Int {
        return priorityMap[HTTP] ?: return PRIORITY_HTTP
    }

    override fun isActivate(): Boolean {
        return activateMap[HTTP] ?: return ACTIVATE
    }
}

