package com.fanji.android.thread.ruler

import com.fanji.android.thread.RulerCounter
import com.fanji.android.thread.RulerLifecycleManager

/**
 *  > 用于编译插件替换 Kotlin 实现，请勿直接使用
 *
 * Creates a thread that runs the specified [block] of code.
 *
 * @param start if `true`, the thread is immediately started.
 * @param isDaemon if `true`, the thread is created as a daemon thread. The Java Virtual Machine exits when
 * the only threads running are all daemon threads.
 * @param contextClassLoader the class loader to use for loading classes and resources in this thread.
 * @param name the name of the thread.
 * @param priority the priority of the thread.
 *
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午2:33
 */
fun thread(
    start: Boolean = true,
    isDaemon: Boolean = false,
    contextClassLoader: ClassLoader? = null,
    name: String? = null,
    priority: Int = -1,
    block: () -> Unit
): Thread {
    // 这里触发的线程创建，名字定义成 ThreadKt
    val counter = RulerCounter.getCounter("ThreadKt")
    val thread = object : RulerThread(name, "ThreadKt") {
        // 覆盖 RulerThread.run() 的实现
        override fun run() {
            counter.incrementAndGet()
            RulerLifecycleManager.onThreadBeginInternal(this)
            try {
                block()
            } finally {
                counter.decrementAndGet()
                RulerLifecycleManager.onThreadEndInternal(this)
            }
        }
    }
    if (isDaemon)
        thread.isDaemon = true
    if (priority > 0)
        thread.priority = priority
    if (contextClassLoader != null)
        thread.contextClassLoader = contextClassLoader
    if (start)
        thread.start()
    return thread
}