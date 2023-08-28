package okhttp3.room

import okhttp3.data.EventData

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-11 下午1:39
 */
class SaveEventRunnable(private val eventData: EventData) : Runnable {
    override fun run() {
//        EventManager.add(eventData = eventData)
    }
}