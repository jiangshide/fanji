package com.fanji.android.behavior.monitor.data

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-17 上午11:49
 */
data class EventData(
    var eventId: String="",
    var eventTime: Long=0L,
    var eventType: Int=0,
    var data: HashMap<String, Any>? = null
)