package com.fanji.android.util.data

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.text.TextUtils
import android.widget.ImageView
import com.fanji.android.util.*
import com.fanji.android.util.compress.img.ImgCompress
import com.fanji.android.util.compress.img.listener.CompressImage
import java.io.File
import java.util.*

/**
 * created by jiangshide on 2020-01-09.
 * email:18311271399@163.com
 */
@Suppress("INACCESSIBLE_TYPE")
class FileData() : Parcelable {
    var id: Long = 0
    var name: String? = ""
    var sufix: String? = ""//文件名后缀
    var format = 0
    var width = 0
    var height = 0
    var duration: Long = 0
    var bitrate: Int = 0
    var size: Long = 0
    var rotate: Int = 0
    var path: String? = null
    var cover: String? = null
    var dir: String? = null
    var bucketId: String? = null
    var bucketName: String? = ""
    var dateAdded: Long = 0
    var dateModified: Long = 0
    var selectedDir = false
    var selectedDirSize = 0

    var uri: Uri? = null

    var selectedDirList: MutableList<FileData>? = null
    var source: Int = 0
    var lrcEs: String? = ""
    var lrcZh: String? = ""

    var wave: String? = null
    var position = 0
    var clientException: Exception? = null
    var serviceException: Exception? = null
    var currentSize: Long = 0
    var totalSize: Long = 0
    var progress = 0
    var selected = false
    var url: String? = null

    var compress: Boolean = false
    var dest: String? = null
    var outPath: String? = ""

    var recordState:Int=0

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        name = parcel.readString()
        sufix = parcel.readString()
        format = parcel.readInt()
        width = parcel.readInt()
        height = parcel.readInt()
        duration = parcel.readLong()
        bitrate = parcel.readInt()
        size = parcel.readLong()
        rotate = parcel.readInt()
        path = parcel.readString()
        cover = parcel.readString()
        dir = parcel.readString()
        bucketName = parcel.readString()
        dateAdded = parcel.readLong()
        dateModified = parcel.readLong()
        selectedDir = parcel.readByte() != 0.toByte()
        selectedDirSize = parcel.readInt()
        source = parcel.readInt()
        lrcEs = parcel.readString()
        lrcZh = parcel.readString()
        wave = parcel.readString()
        position = parcel.readInt()
        currentSize = parcel.readLong()
        totalSize = parcel.readLong()
        progress = parcel.readInt()
        selected = parcel.readByte() != 0.toByte()
        url = parcel.readString()
        compress = parcel.readByte() != 0.toByte()
        dest = parcel.readString()
        outPath = parcel.readString()
    }

    fun showAudioImg(imageView: ImageView, defaultImg: Int) {
        val bitmap = ImgUtil.loadCover(path)
        if (bitmap != null) {
            imageView?.setImageBitmap(bitmap)
        } else {
            imageView?.setImageResource(defaultImg)
        }
    }

    fun getCoverUrl(): String? {
        var urlStr = cover
        if (!TextUtils.isEmpty(path) && FileUtil.isImg(path)) {
            urlStr = path
        } else if (!TextUtils.isEmpty(url) && FileUtil.isImg(url)) {
            urlStr = url
        }
        return urlStr
    }

    fun setWave(waves: List<Int>): String? {
        val array = ByteArray(waves!!.size) { it ->
            (waves!![it]).toByte()
        }
        this.wave = EncryptUtil.base64Encode2String(array)
        return this.wave
    }

    fun compress(listener: OnCompressListener? = null) {
        if (format == IMG) {
            ImgCompress.build(AppUtil.getApplicationContext(), object :
                CompressImage.CompressListener {
                override fun onCompressSuccess(images: ArrayList<FileData>?) {
                    LogUtil.e("--------images:", images, " | dest:", dest)
                    if (!TextUtils.isEmpty(dest)) {
                        size = File(dest).length()
                        path = dest
                    }
                    listener?.onResult(dest)
                }

                override fun onCompressFailed(
                    images: ArrayList<FileData>?,
                    error: String?
                ) {
                    LogUtil.e("compress~error:", error, " | images:", images)
                    listener?.onResult()
                }
            }, this)
                .compress()
        } else if (format == VIDEO) {
            if (!TextUtils.isEmpty(outPath)) {
                path = outPath
            }
            if (TextUtils.isEmpty(sufix)) {
                sufix = FileUtil.getFileSufix(path)
            }
            listener?.onResult(path)
//            dest = FileUtil.getVideoPath(FileUtil.getFileName(outVideoPath))
//            VideoCompress.compressVideoLow(
//                outVideoPath,
//                dest,
//                object : VideoCompress.CompressListener {
//                    override fun onSuccess() {
//                        path = dest
//                        size = File(dest).length()
//                      listener?.onResult(dest)
//                    }
//
//                    override fun onFail() {
//                        listener?.onResult()
//                    }
//
//                    override fun onProgress(percent: Float) {
//                        listener?.onProgress(percent)
//                    }
//
//                    override fun onStart() {
//                    }
//
//                })
        } else {
            listener?.onResult(path)
        }
    }

    override fun writeToParcel(
        parcel: Parcel,
        flags: Int
    ) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(sufix)
        parcel.writeInt(format)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeLong(duration)
        parcel.writeInt(bitrate)
        parcel.writeLong(size)
        parcel.writeInt(rotate)
        parcel.writeString(path)
        parcel.writeString(cover)
        parcel.writeString(dir)
        parcel.writeString(bucketName)
        parcel.writeLong(dateAdded)
        parcel.writeLong(dateModified)
        parcel.writeByte(if (selectedDir) 1 else 0)
        parcel.writeInt(selectedDirSize)
        parcel.writeInt(source)
        parcel.writeString(lrcEs)
        parcel.writeString(lrcZh)
        parcel.writeString(wave)
        parcel.writeInt(position)
        parcel.writeLong(currentSize)
        parcel.writeLong(totalSize)
        parcel.writeInt(progress)
        parcel.writeByte(if (selected) 1 else 0)
        parcel.writeString(url)
        parcel.writeByte(if (compress) 1 else 0)
        parcel.writeString(dest)
        parcel.writeString(outPath)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "FileData(id=$id, name=$name, sufix=$sufix, format=$format, width=$width, height=$height, duration=$duration, bitrate=$bitrate, size=$size, rotate=$rotate, path=$path, cover=$cover, dir=$dir, bucketName=$bucketName, dateAdded=$dateAdded, dateModified=$dateModified, selectedDir=$selectedDir, selectedDirSize=$selectedDirSize, uri=$uri, selectedDirList=$selectedDirList, source=$source, lrcEs=$lrcEs, lrcZh=$lrcZh, wave=$wave, position=$position, clientException=$clientException, serviceException=$serviceException, currentSize=$currentSize, totalSize=$totalSize, progress=$progress, selected=$selected, url=$url, compress=$compress, dest=$dest, outPath=$outPath)"
    }


    companion object CREATOR : Creator<FileData> {
        override fun createFromParcel(parcel: Parcel): FileData {
            return FileData(parcel)
        }

        override fun newArray(size: Int): Array<FileData?> {
            return arrayOfNulls(size)
        }
    }
}

interface OnCompressListener {
    fun onResult(dest: String? = null)
    fun onProgress(percent: Float)
}

const val IMG = 0//图片
const val AUDIO = 1//音频
const val VIDEO = 2//视频
const val DOC = 3//文档
const val WEB = 4//网页
const val VR = 5//VR
const val DOWNLOADS = 4//下载
const val CAMERA = 5//相机