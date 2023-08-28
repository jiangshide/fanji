package com.fanji.android.okhttp.download

import android.text.TextUtils
import com.fanji.android.okhttp.download.listener.IDownloadBlockListener
import com.fanji.android.okhttp.download.listener.IDownloadListener
import com.fanji.android.okhttp.download.runnable.DownloadCheckRunnable
import com.fanji.android.util.ThreadPoolUtil
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.ConcurrentHashMap

/**
 * @author 蒋世德
 * @email 18311271399@163.com
 * @since 21-5-25 下午2:31
 *
 * download file:
 * 1.downloadSize:389471748
 * 2.download time:
 * okdownload:167514
 * filedownload:164389
 * netDownload:159217
 *
 * todo:Multiple Thread with RandomFile download
 */
object Downloader {

    @Volatile
    private var downloadFiles = ConcurrentHashMap<String, DownloadCheckRunnable>()

    val DOWNLOAD_CHECK_RUNNABLE = "downloadCheck"

    fun download(
        client: OkHttpClient,
        request: Request,
        filePath: String?,
        listener: IDownloadListener, blockListener: IDownloadBlockListener? = null
    ) {
        if (downloadFiles.contains(request)) return
        val runnable =
            DownloadCheckRunnable(
                client,
                request,
                filePath, listener
            )
        downloadFiles[request.url.toString()] = runnable
        ThreadPoolUtil.addTask(DOWNLOAD_CHECK_RUNNABLE, runnable)
    }

    fun remove(url: String) {
        if (TextUtils.isEmpty(url)) {
            downloadFiles.remove(url)
            return
        }
    }

    fun cancel(url: String = "") {
        if (!TextUtils.isEmpty(url)) {
            downloadFiles[url]?.cancel()
            return
        }
        downloadFiles.forEach {
            it.value.cancel()
        }
    }
}