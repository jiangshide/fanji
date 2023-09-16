package com.fanji.android.pdf

import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import com.fanji.android.pdf.model.PDFFileInfo
import com.fanji.android.pdf.util.PDFUtil
import java.io.File

/**
 * @author: jiangshide
 * @date: 2023/9/14
 * @email: 18311271399@163.com
 * @description:
 */
object PdfUtils {

    fun getDocumentData(context: Context): List<PDFFileInfo> {
        val pdfData = ArrayList<PDFFileInfo>()
        val columns = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.DATA
        )
        val select = "(_data LIKE '%.pdf')"
        val contentResolver: ContentResolver = context.contentResolver
        val cursor = contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            columns,
            select,
            null,
            null
        )
        var columnIndexOrThrow_DATA = 0
        if (cursor != null) {
            columnIndexOrThrow_DATA =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
        }
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val path = cursor.getString(columnIndexOrThrow_DATA)
                val document: PDFFileInfo = PDFUtil.getFileInfoFromFile(File(path))
                pdfData.add(document)
            }
        }
        cursor!!.close()

        return pdfData
    }
}