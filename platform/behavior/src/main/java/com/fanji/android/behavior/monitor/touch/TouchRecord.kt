package com.fanji.android.behavior.monitor.touch

import android.view.MotionEvent
import com.fanji.android.behavior.monitor.FJMonitor.TOUCH_SLOP
import kotlin.math.abs

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-17 下午4:34
 */
data class TouchRecord(
    var pointerId: Int = 0,
    var downTime: Long = 0L,
    var downX: Float = 0F,
    var downY: Float = 0F,
    var upTime: Long = 0L,
    var upX: Float = 0F,
    var upY: Float = 0F,
    var isClick: Boolean=false
) {
    fun onActionDown(me: MotionEvent) {
        val pointIndex = me.actionIndex
        pointerId = me.getPointerId(pointIndex)
        downTime = me.downTime
        downX = me.getX(pointIndex)
        downY = me.getY(pointIndex)
    }

    fun onActionUp(me: MotionEvent) {
        upTime = me.eventTime
        val pointIndex = me.actionIndex
        upX = me.getX(pointIndex)
        upY = me.getY(pointIndex)

        isClick = abs(downX - upX) < TOUCH_SLOP && abs(downY - upY) < TOUCH_SLOP
    }
}