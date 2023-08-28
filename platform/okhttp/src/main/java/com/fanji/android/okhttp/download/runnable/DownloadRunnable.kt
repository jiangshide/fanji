package com.fanji.android.okhttp.download.runnable

import com.fanji.android.okhttp.download.listener.IDownloadBlockListener
import com.fanji.android.okhttp.download.model.DOWNLOADING
import com.fanji.android.okhttp.download.model.DOWNLOAD_FAIL
import com.fanji.android.okhttp.download.model.DOWNLOAD_SUCCESS
import com.fanji.android.okhttp.download.model.DownloadData
import com.fanji.android.okhttp.exception.OkHttpException
import com.fanji.android.util.ThreadPoolUtil
import okhttp3.*
import okhttp3.internal.closeQuietly
import java.io.InputStream
import java.io.RandomAccessFile

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-4 下午2:32
 */
class DownloadRunnable(
    private var okHttpClient: OkHttpClient,
    private var request: Request,
    private val downloadBlockData: DownloadData,
    private val listener: IDownloadBlockListener
) : Runnable, EventListener() {

    private var call: Call? = null
    private val DOWNLOADED = 100

    override fun run() {
        var startSize = downloadBlockData.startSize
        if (downloadBlockData.downloadedBlockSize > 0) {
            startSize += downloadBlockData.downloadedBlockSize
        }
        val request = Request.Builder().url(request.url).addHeader(
            "Range",
            "bytes=$startSize-${downloadBlockData.offsetSize}"
        ).build()
        var response: Response? = null
        try {
            call = OkHttpClient.Builder().eventListener(this).build().newCall(request)
            response = call!!.execute()
            saveFile(request, response)
        } finally {
            response?.closeQuietly()
        }
    }

    fun cancel() {
        call?.cancel()
    }

    private fun saveFile(request: Request, response: Response) {
        var inputStream: InputStream? = null
        val randomAccessFile = RandomAccessFile(downloadBlockData.filePath, "rw")
        randomAccessFile.seek(downloadBlockData.startSize!!)
        try {
            inputStream = response?.body!!.byteStream()
            var length: Int = 0
            var progress: Int = 0
            val buf = ByteArray(32 * 1024)
            downloadBlockData.status = DOWNLOADING
            while ((inputStream.read(buf).also { length = it }) != -1) {
                randomAccessFile.write(buf, 0, length)
                downloadBlockData.downloadedBlockSize += length
                val currProgress =
                    (downloadBlockData.downloadedBlockSize * 1.0F / downloadBlockData.contentBlockLength * DOWNLOADED).toInt()
                downloadBlockData.downloadedSize = randomAccessFile.length()
                if (currProgress != progress) {
                    progress = currProgress
                    listener?.onProgress(
                        downloadBlockData.blockIndex,
                        downloadBlockData.contentBlockLength,
                        downloadBlockData.downloadedBlockSize,
                        progress = progress,
                        downloadedSize = downloadBlockData.downloadedSize
                    )
                }
                if (progress == DOWNLOADED) {
                    downloadBlockData.status = DOWNLOAD_SUCCESS
                    listener?.onSuccess(request, downloadBlockData)
                }
            }
        } catch (e: Exception) {
            downloadBlockData.url = url
            downloadBlockData.status = DOWNLOAD_FAIL
            downloadBlockData.endTime = System.currentTimeMillis()
            downloadBlockData.rtt = endTime - startTime
            downloadBlockData.exception = "block${downloadBlockData.blockIndex}:" + e.message
            downloadBlockData.code = e.javaClass.canonicalName
            ThreadPoolUtil.addTask("saveDownloadDb", downloadBlockData)
            listener?.onFail(request, downloadBlockData, OkHttpException(case = e))
        } finally {
            inputStream?.closeQuietly()
            response?.closeQuietly()
        }
    }
}