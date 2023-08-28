package com.fanji.android.okhttp.download.model

import okhttp3.data.BaseData

/**
 * @author 蒋世德
 * @email 18311271399@163.com
 * @since 21-5-25 下午3:30
 */
//@Entity
data class DownloadData(
    var filePath: String? = "",//本地文件保存路径
    var blockIndex: Int = -1,//-1:未核查range请求,>=0时为分块位置
    var isRanges: Boolean = false,//是否支持Accept-Ranges
    var etag: String? = "",//
    var chunked: String? = "",//是否分块
    var startSize: Long = 0L,//下载开始位置
    var offsetSize: Long = 0L,//下载结束位置
    var contentBlockLength: Long = 0L,//文件分块长度
    var downloadedBlockSize: Long = 0L,//文件分块已下载长度
    var downloadedSize: Long = 0L,//已下载长度
) : BaseData(), Runnable {

    override fun run() {
//                DownloadManager.add(this)
    }

}

const val DOWNLOAD_START = 10
const val DOWNLOADING = 11
const val DOWNLOAD_SUCCESS = 12
const val DOWNLOAD_FAIL = -10


