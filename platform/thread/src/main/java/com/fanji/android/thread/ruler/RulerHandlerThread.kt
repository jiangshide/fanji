package com.fanji.android.thread.ruler

import android.os.HandlerThread
import android.os.Process
import com.fanji.android.thread.RulerCounter
import com.fanji.android.thread.RulerLifecycleManager
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午2:31
 */
open class RulerHandlerThread @JvmOverloads constructor(
    name: String?,
    priority: Int = Process.THREAD_PRIORITY_DEFAULT
) : HandlerThread(name, priority) {

    init {
        RulerLifecycleManager.onInvokeConstructorInternal(threadGroup, getName())
    }

    // HandlerThread 的计数器统一归类为 RulerHandler
    private val threadCounter: AtomicInteger = RulerCounter.getCounter("RulerHandler")

    override fun start() {
        val result = RulerLifecycleManager.onInvokeStartInternal(this)
        if (result != RulerLifecycleManager.THREAD_START_MAGIC) {
            // 当回调的结果是隐藏魔法时，不去执行 Thread.start()！
            // 目的是减少线程数量，但该方案存在风险，谨慎使用
            super.start()
        }
    }

    override fun run() {
        threadCounter.incrementAndGet()
        RulerLifecycleManager.onThreadBeginInternal(this)
        try {
            super.run()
        } finally {
            threadCounter.decrementAndGet()
            RulerLifecycleManager.onThreadEndInternal(this)
        }
    }

}