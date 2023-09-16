package com.fanji.android.pdf

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.fanji.android.util.LogUtil
import com.tencent.mmkv.MMKV
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback

/**
 * @author: jiangshide
 * @date: 2023/9/14
 * @email: 18311271399@163.com
 * @description:
 */
class PdfProvider : ContentProvider(), PreInitCallback {
    override fun onCreate(): Boolean {
        QbSdk.setDownloadWithoutWifi(true)
        QbSdk.initX5Environment(context, this)
        LogUtil.e("pdf--onCreate--", "initX5Environment...")
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }

    override fun onCoreInitFinished() {
        LogUtil.e("pdf--onCoreInitFinished--", "x5 onCoreInitFinished")
    }

    override fun onViewInitFinished(p0: Boolean) {
        LogUtil.e("pdf--onViewInitFinished--", "p0:", p0)
    }
}