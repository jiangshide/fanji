package com.fanji.android.behavior.monitor.event

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.fanji.android.behavior.monitor.FJMonitor

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-17 上午11:48
 */
class AppLifecycleObserver : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onAppBackground() {
        if (FJMonitor.isMonitoring) {
            FJMonitor.post(FJMonitor.EVENT_BACKGROUND)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onAppForeground() {
        if (FJMonitor.isMonitoring) {
            FJMonitor.post(FJMonitor.EVENT_FOREGROUND)
        }
    }
}