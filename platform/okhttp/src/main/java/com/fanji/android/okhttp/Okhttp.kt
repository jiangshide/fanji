package com.fanji.android.okhttp

import com.fanji.android.okhttp.download.Downloader
import com.fanji.android.okhttp.download.listener.IDownloadListener
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * @author 蒋世德
 * @email 18311271399@163.com
 * @since 21-5-24 下午4:33
 */
object Okhttp {

    @Volatile
    private var okHttpClient: OkHttpClient? = null

    fun okhttp(): OkHttpClient {
        if (okHttpClient == null) {
            okHttpClient = OkHttpClient()
        }
        return okHttpClient!!
    }

    fun evictAll() {
        okhttp()?.connectionPool?.evictAll()
    }

    fun forceEvictAll() {
        okhttp()?.connectionPool?.forceEvictAll()
    }

    fun forceEvictByHost(host: String?) {
        okhttp()?.connectionPool?.forceEvictByHost(host!!)
    }

    fun download(url: String, filePath: String?, listener: IDownloadListener) {
        val request = Request.Builder().url(url).build()
        download(okhttp(), request, filePath, listener)
    }

    fun download(
        client: OkHttpClient,
        request: Request,
        filePath: String?,
        listener: IDownloadListener,
    ) {
        Downloader.download(client, request, filePath, listener)
    }

    fun cancelDownload(request: Request) {
        cancelDownload(request.url.toString())
    }

    fun cancelDownload(url: String = "") {
        Downloader.cancel(url)
    }
}