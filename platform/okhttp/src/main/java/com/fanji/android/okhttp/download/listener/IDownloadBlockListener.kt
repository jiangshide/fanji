package com.fanji.android.okhttp.download.listener

import com.fanji.android.okhttp.download.model.DownloadData
import com.fanji.android.okhttp.exception.OkHttpException
import okhttp3.Request

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-4 下午2:57
 */
interface IDownloadBlockListener {
    fun onProgress(
        index: Int,
        total: Long,
        downloadedBlockSize: Long = 0,
        progress: Int = 0,
        downloadedSize: Long = 0
    )

    fun onSuccess(request: Request, downloadData: DownloadData)
    fun onFail(request: Request, downloadData: DownloadData, e: OkHttpException)
}