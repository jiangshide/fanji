package com.fanji.android.thread.ruler

import android.os.HandlerThread
import android.os.Process

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午2:31
 */
object RulerFactory {
    /**
     * 获取一个 HandlerThread
     *
     * **不建议直接创建线程对象，如果没有特殊需求请优先使用 [RulerScheduler]**
     */
    @JvmStatic
    @JvmOverloads
    fun obtainHandlerThread(
        name: String,
        priority: Int = Process.THREAD_PRIORITY_DEFAULT
    ): HandlerThread {
        return RulerHandlerThread(name, priority)
    }

    /**
     * 获取一个 Thread
     *
     * **不建议直接创建线程对象，如果没有特殊需求请优先使用 [RulerScheduler]**
     */
    @JvmStatic
    @JvmOverloads
    fun obtainThread(
        name: String,
        group: ThreadGroup? = null,
        target: Runnable? = null,
        stackSize: Long = 0
    ): Thread {
        return RulerThread(group, target, name, stackSize, "RulerFactory")
    }
}