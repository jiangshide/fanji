package com.fanji.android.files.viewmodels

import android.app.Application
import android.content.ContentUris
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import android.text.TextUtils
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fanji.android.files.models.Document
import com.fanji.android.files.models.FileType
import com.fanji.android.files.utils.FilePickerUtils
import com.fanji.android.files.utils.PickerManager
import com.fanji.android.util.LogUtil
import com.fanji.android.util.data.FileData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class VMDocPicker(application: Application) : BaseViewModel(application) {
    private val _lvDocData = MutableLiveData<HashMap<FileType, List<FileData>>>()
    val lvDocData: LiveData<HashMap<FileType, List<FileData>>>
        get() = _lvDocData


    fun getDocs(fileTypes: List<FileType>, comparator: Comparator<FileData>?) {
        launchDataLoad {
            val dirs = queryDocs(fileTypes, comparator)
            _lvDocData.postValue(dirs)
        }
    }

    @WorkerThread
    suspend fun queryDocs(
        fileTypes: List<FileType>,
        comparator: Comparator<FileData>?
    ): HashMap<FileType, List<FileData>> {
        var data = HashMap<FileType, List<FileData>>()
        withContext(Dispatchers.IO) {

            val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE
                    + "!="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " AND "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE
                    + "!="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)

            val DOC_PROJECTION = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.TITLE
            )

            val cursor = getApplication<Application>().contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                DOC_PROJECTION,
                selection,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
            )
            if (cursor != null) {
                data = createDocumentType(fileTypes, comparator, getDocumentFromCursor(cursor))
                cursor.close()
            }
        }
        return data
    }

    @WorkerThread
    private fun createDocumentType(
        fileTypes: List<FileType>,
        comparator: Comparator<FileData>?,
        documents: MutableList<FileData>
    ): HashMap<FileType, List<FileData>> {
        val documentMap = HashMap<FileType, List<FileData>>()

        for (fileType in fileTypes) {
            val documentListFilteredByType = documents.filter { document ->
                FilePickerUtils.contains(
                    fileType.extensions,
                    document.mimeType
                )
            }

            comparator?.let {
                documentListFilteredByType.sortedWith(comparator)
            }

            documentMap[fileType] = documentListFilteredByType
        }

        return documentMap
    }

    @WorkerThread
    private fun getDocumentFromCursor(data: Cursor): MutableList<FileData> {
        val documents = mutableListOf<FileData>()
        while (data.moveToNext()) {

            val imageId = data.getLong(data.getColumnIndexOrThrow(BaseColumns._ID))
            val path = data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
            val title =
                data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE))
            if (path != null) {
                val fileType = getFileType(PickerManager.getFileTypes(), path)
                val file = File(path)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
                    imageId
                )
                if (fileType != null && !file.isDirectory && file.exists()) {

//                    val document = Document(imageId, title, contentUri)
                    val fileData = FileData()
                    fileData.id = imageId
                    fileData.name = title
                    fileData.path = path
//                    fileData.fileType = fileType

                    val mimeType =
                        data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE))
                    if (mimeType != null && !TextUtils.isEmpty(mimeType)) {
                        fileData.mimeType = mimeType
                    } else {
                        fileData.mimeType = ""
                    }

                    fileData.size =
                        data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)).toLong()
                    if (!documents.contains(fileData)) documents.add(fileData)
                }
            }
        }

        return documents
    }

    private fun getFileType(types: ArrayList<FileType>, path: String): FileType? {
        for (index in types.indices) {
            for (string in types[index].extensions) {
                if (path.endsWith(string)) return types[index]
            }
        }
        return null
    }
}