package com.fanji.android.behavior.monitor.core

import android.content.Context
import android.view.View

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-17 上午11:45
 */
object GlobalWindowManager {

    private val windowObserver = WindowObserver()

    private var initialized = false

    fun hookWindowManager(context: Context) {
        if (initialized) return
        initialized = true
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE)
        try {
            val windowManagerImplClass = windowManager.javaClass
            val windowManagerGlobalField = windowManagerImplClass.getDeclaredField("mGlobal")
            windowManagerGlobalField.isAccessible = true
            val windowManagerGlobal = windowManagerGlobalField.get(windowManager)
            val viewsField = windowManagerGlobal.javaClass.getDeclaredField("mViews")
            viewsField.isAccessible = true
            val value = viewsField.get(windowManagerGlobal)
            if (value is List<*>) {
                windowObserver.addAll(value as List<View>)
                viewsField.set(windowManagerGlobal, windowObserver)
            }
        } catch (e: Exception) {
            //
        }
    }
}