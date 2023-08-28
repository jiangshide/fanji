package com.fanji.android.thread

import com.fanji.android.thread.data.InternalNamed
import java.lang.Thread

/**
 * 能够拥有名字的 Runnable~
 *
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午2:40
 */
abstract class NamedRunnable(val name: String) : Runnable {

    internal val internal = InternalNamed(name)

    final override fun run() {
        if (internal.startTimestamp == -1L) {
            internal.startTimestamp = System.currentTimeMillis()
        }
        val currentThread = Thread.currentThread()
        val oldName = currentThread.name
        currentThread.name = name
        try {
            execute()
        } catch (e: Throwable) {
            // 主动抛出异常，会导致 App 崩溃，但总好过被线程池吞掉
            val thread = Thread.currentThread()
            thread.uncaughtExceptionHandler?.uncaughtException(thread, e)
        } finally {
            currentThread.name = oldName
        }
    }

    /**
     * 实现具体业务逻辑
     */
    protected abstract fun execute()

    companion object {
        /**
         * 创建 [NamedRunnable] 的工具方法，使用传入的 [block] lambda 替换匿名类语法 `object : NameRunnable`.
         *
         * 使用方法:
         *
         * ```kotlin
         * val runnable = NamedRunnable("name") {
         *
         * }
         * ```
         */
        inline operator fun invoke(name: String, crossinline block: () -> Unit): NamedRunnable =
            object : NamedRunnable(name) {
                override fun execute() {
                    block()
                }
            }
    }
}