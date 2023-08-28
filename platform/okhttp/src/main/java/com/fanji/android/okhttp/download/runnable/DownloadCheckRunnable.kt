package com.fanji.android.okhttp.download.runnable

import android.text.TextUtils
import androidx.annotation.Nullable
import com.fanji.android.okhttp.download.Downloader.DOWNLOAD_CHECK_RUNNABLE
import com.fanji.android.okhttp.download.listener.IDownloadBlockListener
import com.fanji.android.okhttp.download.listener.IDownloadListener
import com.fanji.android.okhttp.download.model.DOWNLOAD_FAIL
import com.fanji.android.okhttp.download.model.DOWNLOAD_START
import com.fanji.android.okhttp.download.model.DOWNLOAD_SUCCESS
import com.fanji.android.okhttp.download.model.DownloadData
import com.fanji.android.okhttp.download.room.DownloadManager
import com.fanji.android.okhttp.exception.OkHttpException
import com.fanji.android.util.AppUtil
import com.fanji.android.util.ThreadPoolUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.internal.closeQuietly
import java.io.File
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-4 下午2:31
 */
class DownloadCheckRunnable(
    private var okHttpClient: OkHttpClient,
    private val request: Request,
    private val filePath: String?,
    private val listener: IDownloadListener,
    private val blockListener: IDownloadBlockListener? = null
) : Runnable, EventListener(), IDownloadBlockListener, CoroutineScope by MainScope() {

    private lateinit var call: Call
    private var downloadData: DownloadData? = null

    @Volatile
    private var downloadProgress = 0

    @Volatile
    private var downloadedSuccess = false

    @Volatile
    private var currProgress = 0

    private val BLOCK_UPPER_LIMIT: Long = 1024 * 1024

    override fun run() {
        if (checkedDownload()) return
        status = DOWNLOAD_START
        var response: Response? = null
        downloadData = DownloadData()
        downloadData?.filePath = filePath
        okHttpClient = okHttpClient.newBuilder().saveEventDb(true)
            .eventListener(this)
            .build()
        try {
            call = okHttpClient.newCall(
                request.newBuilder().head().addHeader(RANGE, "bytes=0-0").build()
            )
            response = call.execute()
        } catch (e: Exception) {
            downloadData?.exception = "run:" + e.message
            downloadData?.code = e.javaClass.canonicalName
            runnableSaveDownloadDb(DOWNLOAD_FAIL)
        } finally {
            response?.closeQuietly()
        }
    }

    private fun checkedDownload(): Boolean {
        val url = request.url.toString()
//        val downloadData = DownloadManager.getDownload(url) ?: return false
//        if (File(downloadData?.filePath).exists()) {
//            DownloadManager.delete(downloadData)
//            return false
//        }
//        if (downloadData?.contentLength == downloadData?.downloadedSize) {
//            listener?.onFail(
//                request,
//                downloadData = downloadData,
//                e = OkHttpException(msg = "file exits!")
//            )
//            return false
//        }
//        val downloadDatas = DownloadManager.getDownloadFails(request.url.toString(), DOWNLOAD_FAIL)
//        if (downloadDatas == null || downloadDatas.isEmpty()) {
//            return false
//        }
//        this.downloadData = downloadData
//        downloadDatas.forEach {
//            val fileDownloadRunnable = DownloadRunnable(
//                okHttpClient,
//                request,
//                it,
//                this
//            )
//            ThreadPoolUtil.addTask("download", fileDownloadRunnable)
//        }
        return true
    }

    override fun responseHeadersEnd(call: Call, response: Response) {
        super.responseHeadersEnd(call, response)
        downloadData?.isRanges =
            "bytes" == response?.header(ACCEPT_RANGES) || response.code == 206
        downloadData?.etag = response?.header(ETAG)
        downloadData?.chunked = response?.header(CHUNKED)
        if (TextUtils.isEmpty(downloadData?.filePath)) {
            downloadData?.filePath = getPath(response?.header(CONTENT_DISPOSITION))
        }
        downloadTask(okHttpClient, request)
    }

    fun cancel() {
        call?.cancel()
    }

    /**
     * The same to com.android.providers.downloads.Helpers#parseContentDisposition.
     *
     * Parse the Content-Disposition HTTP Header. The format of the header
     * is defined here: http://www.w3.org/Protocols/rfc2616/rfc2616-sec19.html
     * This header provides a filename for content that is going to be
     * downloaded to the file system. We only support the attachment type.
     */
    @Nullable
    @Throws(IOException::class)
    private fun getPath(contentDisposition: String?): String? {
        if (contentDisposition == null) {
            return getDefaultPath()
        }
        try {
            var fileName: String? = null
            var m: Matcher = CONTENT_DISPOSITION_QUOTED_PATTERN.matcher(
                contentDisposition
            )
            if (m.find()) {
                fileName = m.group(1)
            } else {
                m = CONTENT_DISPOSITION_NON_QUOTED_PATTERN.matcher(
                    contentDisposition
                )
                if (m.find()) {
                    fileName = m.group(1)
                }
            }
            if (fileName != null && fileName.contains("../")) {
                throw OkHttpException(
                    msg =
                    "The filename [" + fileName + "] from"
                            + " the response is not allowable, because it contains '../', which "
                            + "can raise the directory traversal vulnerability"
                )
            }
            return File(AppUtil.getApplicationContext().cacheDir, fileName).absolutePath
        } catch (ex: IllegalStateException) {
            // This function is defined as returning null when it can't parse the header
        }
        return getDefaultPath()
    }

    private fun getDefaultPath(): String? {
        return File(
            AppUtil.getApplicationContext().cacheDir,
            "${System.currentTimeMillis()}"
        ).absolutePath
    }

    private val CONTENT_DISPOSITION_QUOTED_PATTERN =
        Pattern.compile("attachment;\\s*filename\\s*=\\s*\"([^\"]*)\"")

    // no note
    private val CONTENT_DISPOSITION_NON_QUOTED_PATTERN =
        Pattern.compile("attachment;\\s*filename\\s*=\\s*(.*)")

    private fun downloadTask(okHttpClient: OkHttpClient, request: Request) {
        val count = getBlockCount(contentLength)
        val blockSize = contentLength / count
        ThreadPoolUtil.setCorePoolSize("download", count)
        var startSize: Long = 0
        var offsetSize: Long = 0
        downloadedSuccess = false
        for (i in 0 until count) {
            startSize = i * blockSize
            offsetSize = if (i == count - 1) {
                contentLength
            } else {
                (i + 1) * blockSize
            }
            val data = DownloadData(
                blockIndex = i,
                startSize = startSize,
                offsetSize = offsetSize,
                contentBlockLength = offsetSize - startSize, filePath = downloadData?.filePath
            )
            val fileDownloadRunnable = DownloadRunnable(
                okHttpClient,
                request,
                data,
                this
            )
            ThreadPoolUtil.addTask("download", fileDownloadRunnable)
        }
    }

    fun getThreadNums(num: Int): Int {
        val process = Runtime.getRuntime().availableProcessors()
        if (num * 2 > process) return process
        return num
    }

    private fun getBlockCount(contentLength: Long): Int {
        if (contentLength < 0.5 * BLOCK_UPPER_LIMIT) return getThreadNums(1)
        if (contentLength < BLOCK_UPPER_LIMIT) return getThreadNums(2)
        if (contentLength < 5 * BLOCK_UPPER_LIMIT) return getThreadNums(3)
        if (contentLength < 10 * BLOCK_UPPER_LIMIT) return getThreadNums(4)
        if (contentLength < 50 * BLOCK_UPPER_LIMIT) return getThreadNums(5)
        if (contentLength < 100 * BLOCK_UPPER_LIMIT) return getThreadNums(6)
        return 8
    }

    override fun onProgress(
        index: Int,
        total: Long,
        downloadedBlockSize: Long,
        progress: Int,
        downloadedSize: Long
    ) {
        blockListener?.onProgress(index, total, downloadedBlockSize, progress, downloadedSize)
        currProgress = (downloadedSize * 1.0F / contentLength * 100).toInt()
        if (downloadProgress != currProgress) {
            downloadProgress = currProgress
            launch {
                listener?.onProgress(contentLength, downloadedSize, downloadProgress)
            }
        }
    }

    override fun onSuccess(request: Request, downloadBlockData: DownloadData) {
        blockListener?.onSuccess(request, downloadBlockData)
        if (contentLength == downloadBlockData.downloadedSize && !downloadedSuccess) {
            downloadedSuccess = true
            runnableSaveDownloadDb(DOWNLOAD_SUCCESS)
            launch {
                listener?.onSuccess(request, downloadData)
            }
        }
    }

    override fun onFail(request: Request, downloadBlockData: DownloadData, e: OkHttpException) {
        blockListener?.onFail(request, downloadBlockData, e)
        downloadData?.exception = downloadBlockData.exception
        downloadData?.code = downloadBlockData.code
        runnableSaveDownloadDb(DOWNLOAD_FAIL)
        launch {
            listener?.onFail(request, downloadData, e)
        }
    }

    private fun runnableSaveDownloadDb(status: Int) {
        downloadData?.url = url
        downloadData?.step = step
        downloadData?.ip = ip
        downloadData?.port = port
        downloadData?.status = status
        downloadData?.endTime = System.currentTimeMillis()
        downloadData?.rtt = endTime - startTime
        ThreadPoolUtil.addTask(DOWNLOAD_CHECK_RUNNABLE, downloadData)
    }
}
