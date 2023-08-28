package com.fanji.android.thread.listener

/**
 * Thread 运行时的回调接口，业务方可以在每个回调中增加业务逻辑，目前支持如下生命周期：
 * 1. Thread 的构造函数被调用时
 * 2. Thread.start() 被调用时
 * 3. Thread.run() 调用开始时（意味着线程启动）
 * 4. Thread.isInterrupted() 调用结束时（意味着线程退出）
 * ## 注意代码中所有线程的生命周期都会触发回调，所以实现时注意性能影响和多线程安全
 *
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午2:30
 */
interface ThreadCallback {
    /**
     * Thread 构造函数被调用时触发回调，你可以利用这个接口打印调试信息，比如打印调用栈
     * ## 该接口是在构造函数中调用的，所以请不要将 this 或 Thread.currentThread() 传到栈外部，避免造成对象逃逸
     * ## 注意代码中所有线程的创建都触发回调，所以实现时注意性能影响
     */
    fun onInvokeConstructor(group: ThreadGroup?, name: String) {
        // 默认什么也不做
    }

    /**
     * Thread.start() 被调用时触发回调，你可以利用这个接口打印调试信息，比如打印调用栈
     * ## 注意代码中所有线程的 start() 都触发回调，所以实现时注意性能影响和多线程安全
     *
     * @param thread 正在 start 的 Thread 对象
     * @return 未使用，返回 0 即可
     */
    fun onInvokeStart(thread: Thread): Short {
        // 默认什么也不做
        return 0
    }

    /**
     * Thread 启动时，在 Thread 中回调。如果是通过 [RulerScheduler] 启动的话，获取到的线程名字会不准
     * ## 注意代码中所有线程的启动都触发回调，所以实现时注意性能影响和多线程安全
     *
     * @param thread 开始 run 的 Thread 对象
     */
    fun onThreadBegin(thread: Thread) {
        // 默认什么也不做
    }

    /**
     * Thread 结束时，在 Thread 中回调（即使有异常也会回调）。如果是通过 [RulerScheduler] 启动的话，获取到的线程名字会不准
     * ## 注意代码中所有线程的启动都触发回调，所以实现时注意性能影响和多线程安全
     *
     *  @param thread 结束 run 的 Thread 对象
     */
    fun onThreadEnd(thread: Thread) {
        // 默认什么也不做
    }

}