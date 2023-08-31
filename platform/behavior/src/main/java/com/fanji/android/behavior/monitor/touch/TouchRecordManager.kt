package com.fanji.android.behavior.monitor.touch

import android.view.MotionEvent

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-17 下午4:34
 */
object TouchRecordManager {

    var touchRecord: TouchRecord? = null

    fun touchEvent(me: MotionEvent) {
        val pointIndex = me.actionIndex
        val pointerId = me.getPointerId(pointIndex)
        val action = me.actionMasked
        if (action == MotionEvent.ACTION_DOWN) {
            touchRecord = TouchRecord()
            touchRecord?.onActionDown(me)
        } else {
            if (touchRecord == null || touchRecord?.pointerId != pointerId) return
            if (action == MotionEvent.ACTION_UP) {
                touchRecord?.onActionUp(me)
            } else if (action == MotionEvent.ACTION_CANCEL) {
                touchRecord = null
            }
        }
    }
}