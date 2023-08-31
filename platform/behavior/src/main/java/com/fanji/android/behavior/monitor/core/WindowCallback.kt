package com.fanji.android.behavior.monitor.core

import android.os.Build
import android.view.*
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-17 上午11:46
 */
open class WindowCallback(private val callback: Window.Callback) : Window.Callback {

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            if (dispatchBackKeyEvent()) return true
            return callback.dispatchKeyEvent(event)
        }
        return callback.dispatchKeyEvent(event)
    }

    override fun dispatchKeyShortcutEvent(event: KeyEvent?): Boolean {
        return callback.dispatchKeyShortcutEvent(event)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        return touchEvent(event) || callback.dispatchTouchEvent(event)
    }

   open fun touchEvent(event: MotionEvent?): Boolean = false

    override fun dispatchTrackballEvent(event: MotionEvent?): Boolean {
        return callback.dispatchTrackballEvent(event)
    }

    override fun dispatchGenericMotionEvent(event: MotionEvent?): Boolean {
        return callback.dispatchGenericMotionEvent(event)
    }

    override fun dispatchPopulateAccessibilityEvent(event: AccessibilityEvent?): Boolean {
        return callback.dispatchPopulateAccessibilityEvent(event)
    }

    override fun onCreatePanelView(featureId: Int): View? {
        return callback.onCreatePanelView(featureId)
    }

    override fun onCreatePanelMenu(featureId: Int, menu: Menu): Boolean {
        return callback.onCreatePanelMenu(featureId, menu)
    }

    override fun onPreparePanel(featureId: Int, view: View?, menu: Menu): Boolean {
        return callback.onPreparePanel(featureId, view, menu)
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        return callback.onMenuOpened(featureId, menu)
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        return callback.onMenuItemSelected(featureId, item)
    }

    override fun onWindowAttributesChanged(attrs: WindowManager.LayoutParams?) {
        callback.onWindowAttributesChanged(attrs)
    }

    override fun onContentChanged() {
        callback.onContentChanged()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        callback.onWindowFocusChanged(hasFocus)
    }

    override fun onAttachedToWindow() {
        callback.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        callback.onDetachedFromWindow()
    }

    override fun onPanelClosed(featureId: Int, menu: Menu) {
        callback.onPanelClosed(featureId, menu)
    }

    override fun onSearchRequested(): Boolean {
        return callback.onSearchRequested()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onSearchRequested(searchEvent: SearchEvent?): Boolean {
        return callback.onSearchRequested(searchEvent)
    }

    override fun onWindowStartingActionMode(callback: ActionMode.Callback?): ActionMode? {
        return this.callback.onWindowStartingActionMode(callback)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onWindowStartingActionMode(
        callback: ActionMode.Callback?,
        type: Int
    ): ActionMode? {
        return this.callback.onWindowStartingActionMode(callback, type)
    }

    override fun onActionModeStarted(mode: ActionMode?) {
        callback.onActionModeStarted(mode)
    }

    override fun onActionModeFinished(mode: ActionMode?) {
        callback.onActionModeStarted(mode)
    }

    fun dispatchBackKeyEvent(): Boolean = false
}