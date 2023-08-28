//package com.fanji.android.okhttp.download.room
//
//import androidx.room.*
//import com.fanji.android.okhttp.download.model.DownloadData
//
///**
// * @author 蒋世德 @ fanji inc
// * @email jiangshide@fanji.com
// * @since 21-6-7 上午10:53
// */
//@Dao
//interface DownloadDao {
//    @Insert
//    fun add(downloadModel: DownloadData)
//
//    @Insert
//    fun add(vararg downloadModel: DownloadData)
//
//    @Update
//    fun update(downloadModel: DownloadData)
//
//    @Delete
//    fun delete(downloadModel: DownloadData)
//
//    @Query("SELECT * FROM DownloadData WHERE url=:url")
//    fun getDownload(url: String): DownloadData
//
//    @Query("SELECT * FROM DownloadData")
//    fun getDownloads(): List<DownloadData>
//
//    @Query("SELECT * FROM DownloadData WHERE url=:url and status=:status and blockIndex != -1")
//    fun getDownloadFails(url: String, status: Int): List<DownloadData>
//}