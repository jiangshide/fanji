package com.fanji.android.resource.task

import android.text.TextUtils
import com.fanji.android.third.aliyun.oss.OssClient
import com.fanji.android.third.aliyun.oss.OssClient.HttpFileListener
import com.fanji.android.util.FJEvent
import com.fanji.android.resource.Resource
import com.fanji.android.resource.vm.publish.data.File
import com.fanji.android.resource.vm.publish.data.Publish
import com.fanji.android.util.FileUtil
import com.fanji.android.util.ImgUtil
import com.fanji.android.util.LogUtil
import com.fanji.android.util.data.FileData
import com.fanji.android.util.data.OnCompressListener

/**
 * created by jiangshide on 2020/4/13.
 * email:18311271399@163.com
 */
class PublishTask : HttpFileListener {

    private var listener: PublishTaskListener? = null
    private var publish: Publish? = null
    private var data: MutableList<FileData>? = null
    private var isUpload = true
    private var size = 0
    private var currProgress = 0
    private var progress = 0

    private var mOnCompressListener: OnCompressListener? = null

    fun setCompressListener(listener: OnCompressListener): PublishTask {
        this.mOnCompressListener = listener
        return this
    }

    fun publish(): PublishTask {
        publish = Resource.publish
        LogUtil.e("---------publish:", publish)
        if (publish == null) return this
        if (publish?.files != null && publish?.files!!.size > 0) {
            data = publish?.files
            size = 100 / data!!.size
            progress = 0
            publishFile(0, null)
        } else {
//      Resource.clearPublish()
            Resource.publish = publish
            if (listener != null) {
                listener?.onSuccess(publish)
            }
            FJEvent.get()
                .with(PUBLISH_SUCCESS)
                .post(publish!!)
        }
        return this
    }

    private fun publishFile(
        position: Int,
        url: String?
    ) {
        LogUtil.e("---------position:", position, " | url:", url)
        if (!TextUtils.isEmpty(url)) {
            data!![position].url = url
            var path = data!![position].path
            if (!TextUtils.isEmpty(data!![position].outPath)) {
                path = data!![position].outPath
            }
            val file = File()
            file.duration = data!![position].duration
            if (FileUtil.isVideo(path)) {
                val bounds = ImgUtil.getVideoBounds(path)
                file.width = bounds[0]
                file.height = bounds[1]
                file.rotate = bounds[2]
                file.duration = bounds[3].toLong()
                file.bitrate = bounds[4]
            } else if (FileUtil.isImg(path)) {
                val bounds = ImgUtil.getBitmapBounds(path)
                file.width = bounds[0]
                file.height = bounds[1]
            } else if (FileUtil.isAudio(path)) {
                val bounds = ImgUtil.getAudioBounds(path)
                file.bitrate = bounds[0]
                file.sampleRate = bounds[1]
            }
            file.cover = data!![position].cover
            file.name = data!![position].name
            file.name = data!![position].sufix
            file.size = data!![position].size
//      file.duration = data!![position].duration
            file.format = data!![position].format
//      file.rotate = data!![position].rotate
            file.url = data!![position].url
            file.lrcZh = data!![position].lrcZh
            file.lrcEs = data!![position].lrcEs
            file.wave = data!![position].wave
            publish?.uploadFiles?.add(file)
        }
        data?.forEachIndexed { index, fileData ->
            if (!TextUtils.isEmpty(fileData.path) && TextUtils.isEmpty(fileData.url)) {
                fileData.compress(listener = object : OnCompressListener {
                    override fun onResult(dest: String?) {
                        upload(fileData, index)
                    }

                    override fun onProgress(percent: Float) {
                        mOnCompressListener?.onProgress(percent)
                    }
                })
                return
            }
        }
        if (isUpload) {
            isUpload = false
            publish?.toGson()
            Resource.publish = publish
            if (listener != null) {
                listener?.onSuccess(publish)
            }
            FJEvent.get()
                .with(PUBLISH_SUCCESS)
                .post(publish!!)
        }
    }

    private fun upload(
        fileData: FileData,
        index: Int
    ) {
        fileData.position = index
        progress += currProgress
        if (listener != null) {
            listener?.start(fileData)
        }
        FJEvent.get()
            .with(PUBLISH_START)
            .post(fileData)
        OssClient.instance
            .setListener(this)
            .setFileData(fileData)
            .start()
    }

    fun setListener(listener: PublishTaskListener): PublishTask {
        this.listener = listener
        return this
    }

    override fun onSuccess(
        position: Int,
        url: String
    ) {
        publishFile(position, url)
    }

    override fun onFailure(
        clientExcepion: Exception,
        serviceException: Exception
    ) {
        serviceException?.message
        var throwable = clientExcepion.cause
        if (throwable == null) {
            throwable = serviceException?.cause
        }
        if (listener != null) {
            listener?.onFail(throwable)
        }
        FJEvent.get()
            .with(PUBLISH_FAILE)
            .post(throwable!!)
    }

    override fun onProgress(
        currentSize: Long,
        totalSize: Long,
        p: Int
    ) {
        var curr = (currentSize / totalSize.toDouble()) * size
        currProgress = curr.toInt()
        listener?.progress(progress + currProgress, currProgress)
        FJEvent.get()
            .with(PUBLISH_PROGRESS)
            .post(currProgress)
    }

    public interface PublishTaskListener {
        fun start(fileData: FileData)
        fun progress(
            totalProgress: Int,
            progress: Int
        )

        fun onFail(throwable: Throwable?)
        fun onSuccess(publish: Publish?)
    }
}