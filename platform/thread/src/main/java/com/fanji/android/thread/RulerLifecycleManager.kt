package com.fanji.android.thread

import com.fanji.android.thread.listener.ThreadCallback
import java.lang.Thread
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午2:29
 */
object RulerLifecycleManager {
    const val THREAD_START_MAGIC: Short = -42

    private val callbackList = ConcurrentLinkedQueue<ThreadCallback>()

    /**
     * 注册 [ThreadCallback]
     * @see ThreadCallback
     */
    fun registerCallback(callback: ThreadCallback) {
        callbackList.add(callback)
    }

    /**
     * 删除 [ThreadCallback]
     * @see ThreadCallback
     */
    fun removeCallback(callback: ThreadCallback) {
        callbackList.remove(callback)
    }

    @Deprecated("内部方法，请勿直接使用！")
    fun onInvokeConstructorInternal(group: ThreadGroup?, name: String) {
        for (callback in callbackList) {
            callback.onInvokeConstructor(group, name)
        }
    }

    @Deprecated("内部方法，请勿直接使用！")
    fun onInvokeStartInternal(thread: Thread): Short {
        var result: Short = 0
        for (callback in callbackList) {
            // 为了减少线程数量，这里设置个魔法常量，具体方案请参考使用 [THREAD_START_MAGIC] 的线程
            if (callback.onInvokeStart(thread) == THREAD_START_MAGIC) {
                result = THREAD_START_MAGIC
            }
        }
        return result
    }

    @Deprecated("内部方法，请勿直接使用！")
    fun onThreadBeginInternal(thread: Thread) {
        for (callback in callbackList) {
            callback.onThreadBegin(thread)
        }
    }

    @Deprecated("内部方法，请勿直接使用！")
    fun onThreadEndInternal(thread: Thread) {
        for (callback in callbackList) {
            callback.onThreadEnd(thread)
        }
    }
}