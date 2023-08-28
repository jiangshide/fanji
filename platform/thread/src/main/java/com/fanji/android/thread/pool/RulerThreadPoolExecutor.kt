package com.fanji.android.thread.pool

import java.util.concurrent.*

/**
 * ThreadPoolExecutor 包装类，增加监控相关功能
 *
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午2:37
 */
open class RulerThreadPoolExecutor @JvmOverloads constructor(
    corePoolSize: Int,
    maximumPoolSize: Int,
    keepAliveTime: Long,
    unit: TimeUnit,
    workQueue: BlockingQueue<Runnable>,
    threadFactory: ThreadFactory? = null,
    handler: RejectedExecutionHandler = defaultHandler,
    poolNamePrefix: String = "RulerPool"
) : ThreadPoolExecutor(
    corePoolSize,
    maximumPoolSize,
    keepAliveTime,
    unit,
    workQueue,
    RulerThreadFactory(poolNamePrefix, threadFactory),
    handler
) {

    /**
     * 要覆盖有无 poolNamePrefix 参数的总共八个构造方法
     */
    constructor(
        corePoolSize: Int,
        maximumPoolSize: Int,
        keepAliveTime: Long,
        unit: TimeUnit,
        workQueue: BlockingQueue<Runnable>,
        poolNamePrefix: String
    ) : this(
        corePoolSize,
        maximumPoolSize,
        keepAliveTime,
        unit,
        workQueue,
        null,
        defaultHandler,
        poolNamePrefix
    )

    constructor(
        corePoolSize: Int,
        maximumPoolSize: Int,
        keepAliveTime: Long,
        unit: TimeUnit,
        workQueue: BlockingQueue<Runnable>,
        threadFactory: ThreadFactory?,
        poolNamePrefix: String
    ) : this(
        corePoolSize,
        maximumPoolSize,
        keepAliveTime,
        unit,
        workQueue,
        threadFactory,
        defaultHandler,
        poolNamePrefix
    )

    @JvmOverloads
    constructor(
        corePoolSize: Int,
        maximumPoolSize: Int,
        keepAliveTime: Long,
        unit: TimeUnit,
        workQueue: BlockingQueue<Runnable>,
        handler: RejectedExecutionHandler,
        poolNamePrefix: String = "RulerPool"
    ) : this(
        corePoolSize,
        maximumPoolSize,
        keepAliveTime,
        unit,
        workQueue,
        null,
        handler,
        poolNamePrefix
    )

    companion object {
        private val defaultHandler: RejectedExecutionHandler = AbortPolicy()
    }

}