package com.fanji.android.net.netprobe.okhttp

import com.fanji.android.net.netprobe.Params
import com.fanji.android.net.netprobe.data.CheckData
import okhttp3.Request

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午3:53
 */
internal class OkHttpCheckData(
    val host: String,
    val duration: Long,
    val request: Request,
    val exception: Exception?
) : CheckData(System.currentTimeMillis() + (Params.getHostHttpWinSize(host))) {

}