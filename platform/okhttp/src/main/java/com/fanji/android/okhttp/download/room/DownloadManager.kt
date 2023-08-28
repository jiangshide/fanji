package com.fanji.android.okhttp.download.room

//import androidx.room.Room
import com.fanji.android.okhttp.download.model.DownloadData
import com.fanji.android.util.AppUtil

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-7 下午3:43
 */
object DownloadManager {
//    private var mDb: DownloadRoom? = null
//
//    init {
//        mDb = Room.databaseBuilder(
//            AppUtil.getApplicationContext(),
//            DownloadRoom::class.java,
//            "download.db"
//        ).allowMainThreadQueries().build()
//    }

    //新增
    @Synchronized
    fun add(downloadModel: DownloadData) {
//        mDb?.downloadDao()?.add(downloadModel)
    }

    //查询
//    fun getDownload(url: String): DownloadData? {
//        return mDb?.downloadDao()?.getDownload(
//            url
//        )
//    }

    //删除
    @Synchronized
    fun delete(downloadModel: DownloadData) {
//        mDb?.downloadDao()?.delete(downloadModel)
    }

    //修改
    @Synchronized
    fun update(downloadModel: DownloadData) {
//        mDb?.downloadDao()?.update(downloadModel)
    }

    //获取用户信息
//    fun getDownloads(): List<DownloadData>? {
//        return mDb?.downloadDao()?.getDownloads()
//    }

//    fun getDownloadFails(url: String, status: Int): List<DownloadData>? {
//        return mDb?.downloadDao()?.getDownloadFails(url, status)
//    }
}