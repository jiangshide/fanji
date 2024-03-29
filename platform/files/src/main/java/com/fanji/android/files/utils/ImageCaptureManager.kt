package com.fanji.android.files.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import com.fanji.android.util.SPUtil
import java.io.IOException

/**
 * created by jiangshide on 5/7/21.
 * email:18311271399@163.com
 */
class ImageCaptureManager(private val mContext: Context) {

    var currentMediaPath: Uri? = null

    @Throws(IOException::class)
    private fun createImageFile(): Uri? {
        val imageFileName = "JPEG_" + System.currentTimeMillis() + ".jpg"
        val resolver = mContext.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }

        currentMediaPath = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        return currentMediaPath
    }

    @Throws(IOException::class)
    private fun createVideoFile(): Uri? {
        val videoFileName = "VIDEO_" + System.currentTimeMillis() + ".mp4"
        val resolver = mContext.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, videoFileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
        }

        currentMediaPath = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)

        return currentMediaPath
    }


    @WorkerThread
    @Throws(IOException::class)
    fun dispatchTakePictureIntent(): Intent? {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mContext.packageManager) != null) {
            // Create the File where the photo should go
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val photoURI = createImageFile()
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            } else {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, createImageFile())
            }
            return takePictureIntent
        }
        return null
    }

    @WorkerThread
    @Throws(IOException::class)
    fun dispatchTakeVideoIntent(): Intent? {
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takeVideoIntent.resolveActivity(mContext.packageManager) != null) {
            // Create the File where the photo should go
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val videoURI = createVideoFile()
                takeVideoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                takeVideoIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI)
                takeVideoIntent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, true)
                takeVideoIntent.putExtra(MediaStore.EXTRA_FULL_SCREEN, true)
                takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 2)
                takeVideoIntent.putExtra(
                    MediaStore.EXTRA_DURATION_LIMIT,
                    SPUtil.getInt(RECODER_VIDEO_DURATION, 15))//录制时间默认15秒
            } else {
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, createVideoFile())
            }
            return takeVideoIntent
        }
        return null
    }


    fun deleteContentUri(path: Uri?) {
        if(path != null){
            mContext.contentResolver.delete(path, null , null)
        }
    }

    companion object {

        private val CAPTURED_PHOTO_PATH_KEY = "mCurrentMediaPath"
        val REQUEST_TAKE_PHOTO = 0x101
    }
}

const val RECODER_VIDEO_DURATION = "recoderVideoDuration"