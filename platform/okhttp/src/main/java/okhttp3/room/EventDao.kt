//package okhttp3.room
//
//import androidx.room.*
//import okhttp3.data.EventData
//
///**
// * @author 蒋世德 @ fanji inc
// * @email jiangshide@fanji.com
// * @since 21-6-9 下午6:13
// */
//@Dao
//interface EventDao {
//    @Insert
//    fun add(eventData: EventData)
//
//    @Insert
//    fun add(vararg eventData: EventData)
//
//    @Update
//    fun update(eventData: EventData)
//
//    @Delete
//    fun delete(eventData: EventData)
//
//    @Query("SELECT * FROM eventData WHERE host=:host")
//    fun getDownload(host: String): EventData
//
//    @Query("SELECT * FROM eventData")
//    fun getDownloads(): List<EventData>
//}