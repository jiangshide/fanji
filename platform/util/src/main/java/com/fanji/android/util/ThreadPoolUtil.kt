package com.fanji.android.util

import java.util.concurrent.*

/**
 * 线程池管理：内部调用
 * @author 蒋世德
 * @email 18311271399@163.com
 * @since 21-3-24 下午4:17
 *
 * the temp
 */
object ThreadPoolUtil {

    @Volatile
    private var threadPoolExecutors = ConcurrentHashMap<String, ThreadPoolExecutor>()

    private fun getThreadPoolExecutor(taskName: String): ThreadPoolExecutor? {
        var threadPoolExecutor = threadPoolExecutors[taskName]
        if (threadPoolExecutor == null) {
            threadPoolExecutor = ThreadPoolExecutor(
                5, 60,
                60,
                TimeUnit.SECONDS,
                ArrayBlockingQueue<Runnable>(300),
                threadFactory(taskName, true), ThreadPoolExecutor.AbortPolicy()
            )
            threadPoolExecutors[taskName] = threadPoolExecutor
        }
        return threadPoolExecutor
    }

    fun getString(taskName: String): String? {
        return "corePoolSize:${getCorePoolSize(taskName)} | activeCount:${getActiveCount(taskName)} | maximumPoolSize:${
            getMaximumPoolSize(
                taskName
            )
        } | largestPoolSize:${getLargestPoolSize(taskName)} | poolSize:${getPoolSize(taskName)} | taskCount:${
            getTaskCount(
                taskName
            )
        }"
    }

    fun getCorePoolSize(taskName: String): Int? {
        return getThreadPoolExecutor(taskName)?.corePoolSize
    }

    fun setCorePoolSize(taskName: String, corePoolSize: Int) {
        getThreadPoolExecutor(taskName)?.corePoolSize = corePoolSize
    }

    fun getActiveCount(taskName: String): Int? {
        return getThreadPoolExecutor(taskName)?.activeCount
    }

    fun getMaximumPoolSize(taskName: String): Int? {
        return getThreadPoolExecutor(taskName)?.maximumPoolSize
    }

    fun getLargestPoolSize(taskName: String): Int? {
        return getThreadPoolExecutor(taskName)?.largestPoolSize
    }

    fun getPoolSize(taskName: String): Int? {
        return getThreadPoolExecutor(taskName)?.poolSize
    }

    fun getTaskCount(taskName: String): Long? {
        return getThreadPoolExecutor(taskName)?.taskCount
    }


    /**
     * 添加执行任务
     */
    fun addTask(taskName: String = "net", runnable: Runnable?) {
        if (runnable == null) return
        getThreadPoolExecutor(taskName)?.execute(runnable)
    }

    fun threadFactory(name: String, daemon: Boolean): ThreadFactory {
        return ThreadFactory { runnable ->
            val result = Thread(runnable, "Net $name")
            result.isDaemon = daemon
            result
        }
    }
}