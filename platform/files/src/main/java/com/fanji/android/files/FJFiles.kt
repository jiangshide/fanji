package com.fanji.android.files

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.storage.StorageManager
import android.provider.MediaStore
import android.text.TextUtils
import com.fanji.android.pdf.model.PDFFileInfo
import com.fanji.android.pdf.util.PDFUtil
import com.fanji.android.util.AppUtil
import com.fanji.android.util.FileUtil
import com.fanji.android.util.LogUtil
import com.fanji.android.util.data.AUDIO
import com.fanji.android.util.data.DOC
import com.fanji.android.util.data.DOWNLOADS
import com.fanji.android.util.data.FileData
import com.fanji.android.util.data.IMG
import com.fanji.android.util.data.VIDEO
import java.io.File
import java.lang.reflect.Method
import kotlin.concurrent.thread

/**
 * created by jiangshide on 4/19/21.
 * email:18311271399@163.com
 */
object FJFiles {

    var mTitle: String? = "图片"
    var mTopColor = 0
    var mTitleColor = 0
    var mRightTxt: String? = "完成"
    var mRightTxtColor = 0
    var mTabNormalColor = 0
    var mTabSelectedColor = 0
    var mTabLineColor = 0
    var mCrop = false
    var mFloat = false
    var mSelectedFiles: MutableList<FileData>? = null
    var mMax = 9
    var mSpaceCount = 3
    var fromFragment = true
    var mType = IMG

    //    arrayOf(".pdf",".txt",".apk",".word")
    private var mColumns: Array<String>? = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
        MediaStore.Files.FileColumns.TITLE,
        MediaStore.Files.FileColumns.DATA,
        MediaStore.Files.FileColumns.SIZE,
        MediaStore.Files.FileColumns.DATE_ADDED,
        MediaStore.Files.FileColumns.DATE_MODIFIED,
        MediaStore.Images.Media.LATITUDE,
        MediaStore.Images.Media.LONGITUDE,
        MediaStore.Images.Media.WIDTH,
        MediaStore.Images.Media.HEIGHT
    )

    var mSelectionArgs: Array<String>? = null
    var mSortOrder: String? =
        MediaStore.Files.FileColumns.DATE_ADDED + " desc"

    var mSelection: String? = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)

    var DIR = listOf("img", "audio", "video", "doc", "web", "vr", "downloads", "camera")

    //    var mDocType: Array<String>? =
//        arrayOf("text/plain", "application/pdf", "application/msword", "application/vnd.ms-excel")
    var mDocType: Array<String>? =
        arrayOf("application/pdf")

    fun selectionDoc(): String {
        val selection = StringBuilder()
        mDocType?.forEach {
            selection.append(
                "(" + MediaStore.Files.FileColumns.MIME_TYPE + "=='" + it + "') OR "
            )
        }
        return selection.substring(0, selection.lastIndexOf(")") + 1)
    }

    fun getUri(type: Int): Uri {
        if (type == IMG) {
            mSelection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
            return MediaStore.Files.getContentUri(
                "external"
            )
        }
        if (type == AUDIO) {
            mSelection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO)
//            return Audio.Media.getContentUri(
//                "external"
//            )
            return MediaStore.Files.getContentUri(
                "external"
            )
        }
//        if (type == VIDEO) return Video.Media.getContentUri(
//            "external"
//        )
        if (type == VIDEO) {
            mSelection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
            return MediaStore.Files.getContentUri(
                "external"
            )
        }
        if (type == DOC) {
            mSelection = selectionDoc()
            return MediaStore.Files.getContentUri(
                "external"
            )
        }
        if (type == DOWNLOADS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return MediaStore.Downloads.getContentUri("external")
            }
        }
        return MediaStore.Images.Media.getContentUri("external")
    }

    fun fileList(): List<List<FileData>> {
        return fileList(mType, mColumns, mSelectionArgs, mSortOrder)
    }

    fun fileList(type: Int): List<List<FileData>> {
        return fileList(type, mColumns, mSelectionArgs, mSortOrder)
    }

    private val WHAT = 100
    private val handle = Handler(Looper.getMainLooper()) {
        when (it.what) {
            WHAT -> mFileListener?.onFiles(it.obj as ArrayList<FileData>)
        }
        false
    }

    private var mFileListener: FileListener? = null
    fun fileListSync(suffix: String, fileListener: FileListener) {
        this.mFileListener = fileListener
        thread {
            val list = getDocs(AppUtil.getApplicationContext(),suffix)
//            fileList(type, mColumns, mSelectionArgs, mSortOrder).forEach {
//                it.forEach { it ->
//                    list.add(it)
//                }
//            }

//            PdfUtils.getDocumentData(AppUtil.getApplicationContext()).forEach {
//                val fileData = FileData()
//                fileData.name = it.fileName
//                fileData.path = it.filePath
//                fileData.size = it.fileSize
//                list.add(fileData)
//            }
            handle.sendMessage(Message.obtain().apply {
                what = WHAT
                obj = list
            })
        }
    }

    fun fileList(
        type: Int,
        selection: String?
    ): List<List<FileData>> {
        this.mSelection = selection
        return fileList(type, mColumns, mSelectionArgs, mSortOrder)
    }

    fun fileList(
        type: Int,
        selection: String?,
        sortOrder: String?
    ): List<List<FileData>> {
        this.mSelection = mSelection
        return fileList(type, mColumns, mSelectionArgs, sortOrder)
    }

    fun fileList(
        type: Int,
        columns: Array<String>?,
        mSectionArgs: Array<String>?,
        sortOrder: String?
    ): List<List<FileData>> {

        val hashMap =
            HashMap<String, MutableList<FileData>>()
        var cursor: Cursor? = null
        try {
            val uri = getUri(type)
            LogUtil.e(
                "----------jsd~uri:",
                uri,
                " | columns:",
                columns,
                " | selection:",
                mSelection,
                " | mSectionArgs:",
                mSectionArgs,
                " | sortOrder:",
                sortOrder
            )
            cursor = AppUtil.getApplicationContext()
                .contentResolver
                .query(
                    uri, null, mSelection, mSectionArgs, sortOrder
                )
            LogUtil.e("----jsd--", "cursor:", cursor)
            while (cursor!!.moveToNext()) {
                val fileData = FileData()
                fileData.format = type
                fileData.id = cursor.getLong(
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                )
                fileData.path = cursor.getString(
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                )
                fileData.sufix = FileUtil.getFileName(fileData.path)
                fileData.size = cursor.getLong(
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                )
                fileData.name = cursor.getString(
                    cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE)
                )
                fileData.dateModified = cursor.getLong(
                    cursor.getColumnIndexOrThrow(
                        MediaStore.Files.FileColumns.DATE_MODIFIED
                    )
                )
                fileData.dateAdded = cursor.getLong(
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
                )

                fileData.dir = DIR[type]
                LogUtil.e("----jsd----", "-----fileData:", fileData)
                when (type) {
                    IMG -> {
                        fileData.bucketName = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
                            )
                        )
                        fileData.width = cursor.getInt(
                            cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.WIDTH)
                        )
                        fileData.height = cursor.getInt(
                            cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.HEIGHT)
                        )
                    }

                    VIDEO -> {
                        fileData.bucketName = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
                            )
                        )
                        fileData.duration = cursor.getLong(
                            cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DURATION)
                        )
                    }

                    AUDIO -> {
                        fileData.duration = cursor.getLong(
                            cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DURATION)
                        )
                    }
                }
                if (TextUtils.isEmpty(fileData.bucketName)) {
                    fileData.bucketName = "jsd"
                }
                if (hashMap.containsKey(fileData.bucketName)) {
                    val data =
                        hashMap[fileData.bucketName]!!
                    data.add(fileData)
                    hashMap[fileData.bucketName!!] = data
                } else {
                    val data: MutableList<FileData> =
                        ArrayList()
                    hashMap[fileData.bucketName!!] = data
                }
            }
        } catch (e: Exception) {
            LogUtil.e(e)
        } finally {
            cursor?.close()
        }
        val data: MutableList<List<FileData>> =
            ArrayList()
        for ((_, value) in hashMap) {
            if (value.size > 0) {
                data.add(value)
            }
        }
        return data
    }

    fun openFile(context: Context, fileListener: FileListener) {
        openFile(context, 0, fileListener)
    }

    fun openFile(context: Context, type: Int, fileListener: FileListener) {
        FJFilesActivity.openFile(context, type, fileListener)
    }

    fun getDocs(context: Context, suffix: String = "pdf"): ArrayList<FileData> {
        val columns = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.DATA
        )
        val select = "(_data LIKE '%.$suffix' AND LIKE '%.png')"
        val cursor = context.contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            columns, select, null, null
        )
        var columnIndexOrThrow_DATA = 0
        if (cursor != null) {
            columnIndexOrThrow_DATA =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
        }
        val list = ArrayList<FileData>()
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val path = cursor.getString(columnIndexOrThrow_DATA)
                val document: PDFFileInfo = PDFUtil.getFileInfoFromFile(File(path))
                val fileData = FileData()
                fileData.path = document.filePath
                fileData.name = document.fileName
                fileData.size = document.fileSize
                list.add(fileData)
            }
        }
        return list
    }

    fun getAllSdPaths(context: Context): List<String> {
        var mMethodGetPaths: Method? = null
        var paths: Array<String>? = null
        //通过调用类的实例mStorageManager的getClass()获取StorageManager类对应的Class对象
        //getMethod("getVolumePaths")返回StorageManager类对应的Class对象的getVolumePaths方法，这里不带参数
        val mStorageManager = context
            .getSystemService(Context.STORAGE_SERVICE) as StorageManager //storage
        try {
            mMethodGetPaths = mStorageManager.javaClass.getMethod("getVolumePaths")
            paths = mMethodGetPaths.invoke(mStorageManager) as Array<String>
        } catch (e: java.lang.Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return if (paths != null) {
            paths.toList()
        } else java.util.ArrayList()
    }
}


interface FileListener {
    fun onFiles(files: List<FileData>)
}