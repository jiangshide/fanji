package com.fanji.android.behavior.monitor.core

import android.view.View
import android.view.Window
import com.fanji.android.behavior.R
import java.lang.reflect.Field

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-17 上午11:46
 */
class WindowObserver : ArrayList<View>() {
    private lateinit var decorClass: Any
    private lateinit var windowField: Field
    private val listeners = ArrayList<WindowObserverListener>()

    init {
        try {
            decorClass = Class.forName("com.android.internal.policy.DecorView")
        } catch (e: ClassNotFoundException) {
            //
        }
    }

    override fun add(view: View): Boolean {
        if (decorClass == null) {
            decorClass = view.rootView.javaClass
        }
        if (view.javaClass != decorClass) {
            return super.add(view)
        }
        val window = getWindow(view) ?: return super.add(view)
        view.setTag(R.id.behavior_window, window)
        listeners?.forEach {
            it?.add(window)
        }
        return super.add(view)
    }

    override fun removeAt(index: Int): View {
        val view = get(index)
        val window = view.getTag(R.id.behavior_window) as Window
        listeners?.forEach {
            it?.remove(window)
        }
        return super.removeAt(index)
    }

    private fun getWindow(decorView: View): Window? {
        try {
            return windowField.get(decorView) as Window
        } catch (e: IllegalAccessException) {
            //
        }
        return null
    }

    private fun bindWindow(decorView: View) {
        if (decorClass == null) {
            decorClass = decorView.javaClass
        }
        if (decorView.javaClass != decorClass) {
            return
        }
        val window = getWindow(decorView)
        if (window != null) {
            decorView.setTag(R.id.behavior_window, window)
        }
    }

    fun addWindowObserverListener(listener: WindowObserverListener) {
        if (listener != null) {
            listeners.add(listener)
        }
    }

    fun removeWindowObserverListener(listener: WindowObserverListener) {
        listeners.remove(listener)
    }

    interface WindowObserverListener {
        fun add(window: Window)
        fun remove(window: Window)
    }
}