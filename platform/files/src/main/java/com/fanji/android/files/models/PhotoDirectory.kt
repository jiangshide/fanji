package com.fanji.android.files.models

import android.net.Uri
import android.os.Parcelable
import com.fanji.android.util.AppUtil
import com.fanji.android.util.LogUtil
import com.fanji.android.util.Uri2PathUtil
import com.fanji.android.util.data.FileData
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Parcelize
class PhotoDirectory(
                var id: Long = 0,
                var bucketId: String? = null,
                private var coverPath: Uri? = null,
                var name: String? = null,
                var dateAdded: Long = 0,
                val medias: MutableList<FileData> = mutableListOf()
) : Parcelable {

    fun getCoverPath(): Uri? {
        return when {
            medias.size > 0 -> medias[0].uri
            coverPath != null -> coverPath
            else -> null
        };
    }

    fun setCoverPath(coverPath: Uri?) {
        this.coverPath = coverPath
    }
    val uiScope = CoroutineScope(Dispatchers.IO)
    fun addPhoto(imageId: Long, fileName: String, path: Uri, mediaType: Int,size:Long,date:Long,duration:Long=0) {
        val fileData = FileData()
        fileData.id = imageId
        fileData.name = fileName
        fileData.uri = path
        fileData.format = mediaType
        fileData.size = size
        fileData.dateAdded = date
        fileData.duration = duration
        uiScope.async {
            fileData.path = Uri2PathUtil.getRealPathFromUri(AppUtil.getApplicationContext(), path)
        }
//        medias.add(Media(imageId, fileName, path, mediaType,size,date=date,duration=duration,width=width,high=high))
        medias.add(fileData)
    }

    override fun equals(other: Any?): Boolean {
        return this.bucketId == (other as? PhotoDirectory)?.bucketId
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (bucketId?.hashCode() ?: 0)
        result = 31 * result + (coverPath?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + dateAdded.hashCode()
        result = 31 * result + medias.hashCode()
        return result
    }
}