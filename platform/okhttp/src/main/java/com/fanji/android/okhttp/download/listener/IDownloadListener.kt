package com.fanji.android.okhttp.download.listener

import com.fanji.android.okhttp.download.model.DownloadData
import com.fanji.android.okhttp.exception.OkHttpException
import okhttp3.Request

/**
 * @author 蒋世德
 * @email 18311271399@163.com
 * @since 21-5-25 下午2:33
 */
interface IDownloadListener {
    fun onSuccess(request: Request, downloadData: DownloadData?)
    fun onProgress(total: Long, downloadSize: Long, progress: Int)
    fun onFail(request: Request, downloadData: DownloadData?, e: OkHttpException)
}