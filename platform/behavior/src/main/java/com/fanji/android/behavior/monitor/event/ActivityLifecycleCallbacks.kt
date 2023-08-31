package com.fanji.android.behavior.monitor.event

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.fanji.android.behavior.monitor.FJMonitor
import com.fanji.android.behavior.monitor.data.EventData

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-17 上午11:47
 */
class ActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        val eventData = EventData(eventType = FJMonitor.EVENT_ACTIVITY_START)
        eventData.eventId =
            FJMonitor.SYMBOL_ACTIVITY_NAME + FJMonitor.SYMBOL_DIVIDER_INNER + activity.javaClass.name
        FJMonitor.post(eventData)
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}