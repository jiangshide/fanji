package com.fanji.android.behavior.monitor

import com.fanji.android.behavior.monitor.data.EventData

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-17 上午11:44
 */
object FJMonitor {

    var initialized = false
    var isMonitoring = false

    private var listeners: List<MonitorListener>? = null

    fun post(eventType: Int) {
        post(EventData(eventType = eventType))
    }

    fun post(eventData: EventData) {
        if (!initialized || !isMonitoring) return
        listeners?.forEach {
            it?.onEvent(eventData)
        }
    }

    const val TOUCH_SLOP = -1

    const val EVENT_TOUCH = 0
    const val EVENT_BACK = 1
    const val EVENT_BACKGROUND = 2
    const val EVENT_FOREGROUND = 3
    const val EVENT_DIALOG_SHOW = 4
    const val EVENT_DIALOG_CLOSE = 5
    const val EVENT_ACTIVITY_START = 6

    const val SYMBOL_DIVIDER = "_^_"
    const val SYMBOL_DIVIDER_INNER = "_&_"
    const val SYMBOL_WINDOW = "w"
    const val SYMBOL_VIEW_ID = "vi"
    const val SYMBOL_VIEW_REFERENCE = "vr"
    const val SYMBOL_VIEW_QUADRANT = "vq"
    const val SYMBOL_VIEW_LIST = "vl"
    const val SYMBOL_VIEW_PATH = "vp"
    const val SYMBOL_WEB_URL = "wu"
    const val SYMBOL_VIEW_TAG = "vf"
    const val SYMBOL_ACTIVITY_NAME = "an"

    interface MonitorListener {
        fun onEvent(eventData: EventData)
    }
}