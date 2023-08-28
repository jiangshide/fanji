//package okhttp3.room
//
//import androidx.room.Room
//import com.fanji.android.util.AppUtil
//import okhttp3.data.EventData
//
///**
// * @author 蒋世德 @ fanji inc
// * @email jiangshide@fanji.com
// * @since 21-6-9 下午6:19
// */
//object EventManager {
//    private var mDb: EventRoom? = null
//
//    init {
//        mDb = Room.databaseBuilder(
//            AppUtil.getApplicationContext(),
//            EventRoom::class.java,
//            "event.db"
//        ).allowMainThreadQueries().build()
//    }
//
//    @Synchronized
//    fun add(eventData: EventData) {
//        mDb?.eventDao()?.add(eventData)
//    }
//
//    @Synchronized
//    fun getEvent(host: String): EventData? {
//        return mDb?.eventDao()?.getDownload(host)
//    }
//
//    @Synchronized
//    fun delete(eventData: EventData) {
//        mDb?.eventDao()?.delete(eventData)
//    }
//
//    @Synchronized
//    fun update(eventData: EventData) {
//        mDb?.eventDao()?.update(eventData)
//    }
//
//    @Synchronized
//    fun getEvents(): List<EventData?>? {
//        return mDb?.eventDao()?.getDownloads()
//    }
//}