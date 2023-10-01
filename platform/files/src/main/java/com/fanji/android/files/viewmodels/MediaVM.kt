package com.fanji.android.files.viewmodels

import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fanji.android.files.R
import com.fanji.android.files.models.PhotoDirectory
import com.fanji.android.files.utils.FilePickerConst
import com.fanji.android.files.utils.PickerManager
import com.fanji.android.files.utils.registerObserver
import com.fanji.android.ui.vm.FJVM
import com.fanji.android.util.LogUtil
import com.fanji.android.util.data.FileData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @author: jiangshide
 * @date: 2023/9/27
 * @email: 18311271399@163.com
 * @description:
 */
class MediaVM : FJVM() {

    private val _lvMediaData = MutableLiveData<List<FileData>>()
    val lvMediaData: LiveData<List<FileData>>
        get() = _lvMediaData

    private val _lvPhotoDirsData = MutableLiveData<List<PhotoDirectory>>()
    val lvPhotoDirsData: LiveData<List<PhotoDirectory>>
        get() = _lvPhotoDirsData

    private val _lvDataChanged = MutableLiveData<Boolean>()
    val lvDataChanged: LiveData<Boolean>
        get() = _lvDataChanged

    private var contentObserver: ContentObserver? = null

    private fun registerContentObserver(context: Context) {
        if (contentObserver == null) {
            contentObserver = context.contentResolver.registerObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ) {
                _lvDataChanged.value = true
            }
        }
    }

    fun getMedia(
        context: Context,
        bucketId: String? = null,
        mediaType: Int = FilePickerConst.MEDIA_TYPE_IMAGE
    ): MediaVM {
        uiScope.launch {
            val medias = mutableListOf<FileData>()
            queryImages(context, bucketId, mediaType).map { dir ->
                medias.addAll(dir.medias)
            }
            medias.sortWith(Comparator { a, b -> (b.id - a.id).toInt() })

            _lvMediaData.postValue(medias)
            registerContentObserver(context)
        }
        return this
    }

    fun getPhotoDirs(
        context: Context,
        bucketId: String? = null,
        mediaType: Int = FilePickerConst.MEDIA_TYPE_IMAGE
    ) {
        uiScope.launch {
            val dirs = queryImages(context, bucketId, mediaType)
            val photoDirectory = PhotoDirectory()
            photoDirectory.bucketId = null

            when (mediaType) {
                FilePickerConst.MEDIA_TYPE_VIDEO -> {
                    photoDirectory.name =
                        context.getString(
                            R.string.all_videos
                        )
                }

                FilePickerConst.MEDIA_TYPE_AUDIO -> {
                    photoDirectory.name =
                        context.getString(R.string.all_audios)
                }

                FilePickerConst.MEDIA_TYPE_IMAGE -> {
                    photoDirectory.name =
                        context.getString(R.string.all_photos)
                }

                else -> {
                    photoDirectory.name =
                        context.getString(R.string.all_files)
                }
            }

            if (dirs.isNotEmpty() && dirs[0].medias.size > 0) {
                photoDirectory.dateAdded = dirs[0].dateAdded
                photoDirectory.setCoverPath(dirs[0].medias[0].uri)
            }

            for (i in dirs.indices) {
                photoDirectory.medias.addAll(dirs[i].medias)
            }

            dirs.add(0, photoDirectory)
            _lvPhotoDirsData.postValue(dirs)
            registerContentObserver(context)
        }
    }

    @WorkerThread
    suspend fun queryImages(
        context: Context,
        bucketId: String?,
        mediaType: Int
    ): MutableList<PhotoDirectory> {
        var data = mutableListOf<PhotoDirectory>()
        withContext(Dispatchers.IO) {
            val projection = null
            val uri = MediaStore.Files.getContentUri("external")
            val sortOrder = MediaStore.Images.Media._ID + " DESC"

            var selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)

            if (mediaType == FilePickerConst.MEDIA_TYPE_VIDEO) {
                selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
            } else if (mediaType == FilePickerConst.MEDIA_TYPE_AUDIO) {
                selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO)
            }

            if (!PickerManager.isShowGif) {
                selection += " AND " + MediaStore.Images.Media.MIME_TYPE + "!='" + MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension("gif") + "'"
            }

            if (bucketId != null)
                selection += " AND " + MediaStore.Images.Media.BUCKET_ID + "='" + bucketId + "'"

            LogUtil.e("-----jsds---", "-----selection:", selection, " | mediaType:", mediaType)
            val cursor = context.contentResolver.query(
                uri,
                projection,
                selection,
                null,
                sortOrder
            )

            if (cursor != null) {
                data = getPhotoDirectories(mediaType, cursor)
                cursor.close()
            }
        }
        return data
    }

    @WorkerThread
    private fun getPhotoDirectories(fileType: Int, data: Cursor): MutableList<PhotoDirectory> {
        val directories = mutableListOf<PhotoDirectory>()

        while (data.moveToNext()) {
            val size = data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE))
            if (size <= 200L) continue
            val imageId = data.getLong(data.getColumnIndexOrThrow(BaseColumns._ID))
            val bucketId =
                data.getString(data.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_ID))
            val name =
                data.getString(data.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))
            val fileName = data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE))
            val mediaType =
                data.getInt(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE))
            val date =
                data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED))

            var duration: Long = 0
            if (fileType == FilePickerConst.MEDIA_TYPE_VIDEO || fileType == FilePickerConst.MEDIA_TYPE_AUDIO) {
                duration =
                    data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DURATION))
            }

            val photoDirectory = PhotoDirectory()
            photoDirectory.id = imageId
            photoDirectory.bucketId = bucketId
            photoDirectory.name = name

            var contentUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imageId
            )
            if (fileType == FilePickerConst.MEDIA_TYPE_VIDEO) {
                contentUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    imageId
                )
            } else if (fileType == FilePickerConst.MEDIA_TYPE_AUDIO) {
                contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    imageId
                )
            }
            if (!directories.contains(photoDirectory)) {
                photoDirectory.addPhoto(
                    imageId,
                    fileName,
                    contentUri,
                    mediaType,
                    size,
                    date,
                    duration
                )
                photoDirectory.dateAdded =
                    data.getLong(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED))
                directories.add(photoDirectory)
            } else {
                directories[directories.indexOf(photoDirectory)]
                    .addPhoto(imageId, fileName, contentUri, mediaType, size, date, duration)
            }
        }

        return directories
    }
}