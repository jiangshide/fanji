package com.fanji.android.thread.pool

import com.fanji.android.thread.RulerCounter
import com.fanji.android.thread.RulerLifecycleManager
import com.fanji.android.thread.RulerScheduler
import com.fanji.android.thread.ruler.RulerThread
import com.fanji.android.util.LogUtil
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * 用于监控全局线程使用状况的统一 ThreadFactory
 * ### 当参数 [factory] 不为空时提供如下功能：
 * * 根据 Factory 名字监控全局线程数量
 * ### 当参数 [factory] 为空时，提供如下功能：
 * * 自定义线程名，格式为：poolName-poolNumber-thread-threadNumber
 * * 根据 Factory 名字监控全局线程数量
 *
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午2:36
 */
internal class RulerThreadFactory @JvmOverloads constructor(
    /**
     * Factory 名字，用于标识线程池的作用
     */
    factoryName: String,
    /**
     * 用户自定义的 factory
     */
    private val factory: ThreadFactory? = null
) : ThreadFactory {
    private var prefix: String? = null
    private var group: ThreadGroup? = null
    private val threadNumber = AtomicInteger(1)
    private val globalThreadCounter: AtomicInteger = RulerCounter.getCounter(factoryName)

    companion object {
        @JvmStatic
        private val poolNumber = AtomicInteger(1)
    }

    init {
        if (factory == null) {
            val s = System.getSecurityManager()
            group = if (s != null) s.threadGroup else Thread.currentThread().threadGroup
            prefix = "$factoryName-${poolNumber.getAndIncrement()}-thread-"
        }
    }

    override fun newThread(runnable: Runnable): Thread {
        // 不再需要 wrapRunnable，因为基本上所有地方都被替换成了 RulerThread 系列
//        val runnable = wrapRunnable(r)
        return if (factory == null) {
            //线程名字格式为 poolName-poolNumber-thread-threadNumber
            val name = "$prefix${threadNumber.getAndIncrement()}"
            RulerThread(group, runnable, name, 0, "").apply {
                if (isDaemon) isDaemon = false
                if (priority != Thread.NORM_PRIORITY) priority = Thread.NORM_PRIORITY
            }
        } else {
            factory.newThread(runnable)
        }
    }

    private fun wrapRunnable(r: Runnable): Runnable {
        return if (RulerScheduler.isDebug) {
            wrapRunnableForLog(r)
        } else {
            wrapRunnableForRelease(r)
        }
    }

    private fun wrapRunnableForRelease(r: Runnable): Runnable {
        return Runnable {
            globalThreadCounter.incrementAndGet()
            RulerLifecycleManager.onThreadBeginInternal(Thread.currentThread())
            try {
                r.run()
            } finally {
                globalThreadCounter.decrementAndGet()
                RulerLifecycleManager.onThreadEndInternal(Thread.currentThread())
            }
        }
    }

    private fun wrapRunnableForLog(r: Runnable): Runnable {
        return Runnable {
            var count = globalThreadCounter.incrementAndGet()
            RulerLifecycleManager.onThreadBeginInternal(Thread.currentThread())
            // threadName 和 count 打印 log 用
            val threadName = Thread.currentThread().name
            LogUtil.d("Start:$threadName, global count:$count")
            try {
                r.run()
            } finally {
                count = globalThreadCounter.decrementAndGet()
                RulerLifecycleManager.onThreadEndInternal(Thread.currentThread())
                LogUtil.d("Finished:$threadName, global count:$count")
            }
        }
    }

}