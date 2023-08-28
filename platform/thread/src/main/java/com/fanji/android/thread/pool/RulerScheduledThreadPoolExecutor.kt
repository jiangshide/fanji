package com.fanji.android.thread.pool

import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadFactory

/**
 * 包装后的 [ScheduledThreadPoolExecutor]，增加对线程池的管理功能，接口保持不变
 *
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午2:35
 */
open class RulerScheduledThreadPoolExecutor @JvmOverloads constructor(
    corePoolSize: Int,
    threadFactory: ThreadFactory? = null,
    handler: RejectedExecutionHandler = defaultHandler,
    poolNamePrefix: String = "RulerScheduledPool"
) : ScheduledThreadPoolExecutor(
    corePoolSize,
    RulerThreadFactory(poolNamePrefix, threadFactory),
    handler
) {

    /**
     * 要覆盖有无 poolNamePrefix 参数的总共八个构造方法
     */

    constructor(corePoolSize: Int, poolNamePrefix: String)
            : this(corePoolSize, null, defaultHandler, poolNamePrefix)

    constructor(corePoolSize: Int, threadFactory: ThreadFactory?, poolNamePrefix: String)
            : this(corePoolSize, threadFactory, defaultHandler, poolNamePrefix)

    @JvmOverloads
    constructor(
        corePoolSize: Int,
        handler: RejectedExecutionHandler,
        poolNamePrefix: String = "RulerScheduledPool"
    ) : this(corePoolSize, null, handler, poolNamePrefix)

    companion object {
        private val defaultHandler: RejectedExecutionHandler = AbortPolicy()
    }

}