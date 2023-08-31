package com.fanji.android.behavior.monitor.event

import android.view.MotionEvent
import android.view.Window
import com.fanji.android.behavior.monitor.FJMonitor
import com.fanji.android.behavior.monitor.core.WindowCallback

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-17 上午11:49
 */
class MonitorWindowCallbacks(private val window: Window) : WindowCallback(window.callback) {

    override fun touchEvent(event: MotionEvent?): Boolean {
        if(FJMonitor.isMonitoring){

        }
        return false
    }
}