package com.fanji.android.net.interceptor

import com.fanji.android.net.state.NetState
import com.fanji.android.util.LogUtil
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response

/**
 * created by jiangshide on 2019-07-31.
 * email:18311271399@163.com
 */
class OfflineCacheInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        LogUtil.e("---------request:",request)
        var response: Response? = null
        if(!NetState.instance.isNetworkAvailable()){
            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build()
            response = chain.proceed(request)
        }else{
            response = chain.proceed(request)
            response.newBuilder().removeHeader("Pragma").header(
                    "Cache-Control",
                    "public,only-if-cached,max-stale="+60*60*24*30).build()
        }
//        LogUtil.e("---------response:",response)
        return response
    }
}