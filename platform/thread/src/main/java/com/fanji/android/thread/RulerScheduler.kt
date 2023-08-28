package com.fanji.android.thread

import android.os.Handler
import android.os.Looper
import com.fanji.android.thread.data.InternalNamed
import com.fanji.android.thread.pool.RulerExecutors
import java.util.concurrent.*

/**
 * 统一调度管理类。
 *
 * **此调度类会尽可能及时的响应任务调度，在此基础上避免过多的创建线程**
 *
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午2:38
 */
object RulerScheduler {
    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    private lateinit var scheduledPool: ScheduledExecutorService
    private lateinit var cachedPool: ExecutorService

    private val taskMap = ConcurrentHashMap<ScheduledFuture<*>, InternalNamed>()

    private val factory = object : AutoCoreFlexRunnable.IteratorFactory {
        override fun create(): MutableIterator<MutableMap.MutableEntry<ScheduledFuture<*>, InternalNamed>> {
            return taskMap.iterator()
        }
    }

    @JvmStatic
    @Volatile
    var isDebug: Boolean = false
        set(value) {
            field = value
        }

    /**
     * 启动自动调整 ScheduledThreadPool.coreSize 的任务
     *
     * @param interval 轮询时间间隔
     * @param residentThreshold 常驻线程的时长阈值
     * @param unit 以上两个时长参数的单位
     */
    @JvmStatic
    fun startAutoCoreSizeFlex(interval: Long, residentThreshold: Long, unit: TimeUnit) {
        initScheduledPool()
        val task = AutoCoreFlexRunnable(scheduledPool as ScheduledThreadPoolExecutor, factory)
        task.residentThreshold = unit.toMillis(residentThreshold)
        runWithFixedDelay(task, interval, interval, unit)
    }

    /**
     * 在子线程执行 [runnable]，适用于大多数任务。需要设置任务名字
     *
     * @param runnable 任务对象，[NamedRunnable] 的子类，需要设置任务名字
     * @return 返回一个 Future，用于控制已经启动的任务
     */
    @JvmStatic
    fun run(runnable: NamedRunnable): Future<*> {
        initScheduledPool()
        val future = scheduledPool.schedule(runnable, 0, TimeUnit.MILLISECONDS)
        taskMap[future] = runnable.internal
        return future
    }

    /**
     * 在子线程延迟执行 [runnable]，需要设置任务名字
     *
     * @param runnable 任务对象，[NamedRunnable] 的子类，需要设置任务名字
     * @param delayMillis 延迟时间，单位毫秒
     * @return 返回一个 ScheduledFuture，用于控制已经启动的任务
     */
    @JvmStatic
    fun runDelay(runnable: NamedRunnable, delayMillis: Long): ScheduledFuture<*> {
        initScheduledPool()
        val future = scheduledPool.schedule(runnable, delayMillis, TimeUnit.MILLISECONDS)
        taskMap[future] = runnable.internal
        return future
    }

    /**
     * 在子线程立即执行 [runnable]，适用于紧急任务。需要设置任务名字
     *
     * @param runnable 任务对象，[NamedRunnable] 的子类，需要设置任务名字
     * @return 返回一个 Future，用于控制已经启动的任务
     */
    @JvmStatic
    fun runUrgent(runnable: NamedRunnable): Future<*> {
        // 保证任务的及时执行，使用无 core 的缓存线程池，线程生存时间为60秒
        initCachedPool()
        return cachedPool.submit(runnable)
    }

    /**
     * 周期执行任务，首先延迟 [initialDelay] 时间执行 [runnable]，之后循环执行 [runnable]，不过每次执行前都会延迟 [delay] 时间
     * ### 效果如下（注意和 [runAtFixedRate] 的区别）：
     * ```
     * start: -----{-----}+++++{-----}+++++{-----}+++++{---
     *      : |init| run |delay| run |delay| run |delay|run
     * ```
     *
     * @param runnable 要执行的任务，需要设置任务名字
     * @param initialDelay 首次执行时延迟的时间
     * @param delay 循环执行的间隔时间
     * @param unit [initialDelay] 和 [delay] 的单位
     * @return 返回一个 ScheduledFuture，用于控制已经启动的任务
     */
    @JvmStatic
    fun runWithFixedDelay(
        runnable: NamedRunnable,
        initialDelay: Long,
        delay: Long,
        unit: TimeUnit
    ): ScheduledFuture<*> {
        initScheduledPool()
        return scheduledPool.scheduleWithFixedDelay(runnable, initialDelay, delay, unit)
    }

    /**
     * 周期执行任务，首先延迟 [initialDelay] 时间执行 [runnable]，之后循环执行 [runnable]，不过每次执行的间隔时间不小于 [period]
     * ### 效果如下（注意和 [runWithFixedDelay] 的区别）：
     * ```
     * start: -----{-----}++++{--------------}{----}++++
     *      : |init| run |idle|     run      | run |idle|
     *      :      |  period  |  period  |...|  period  |
     * ```
     *
     * @param runnable 要执行的任务，需要设置任务名字
     * @param initialDelay 首次执行时延迟的时间
     * @param period 循环执行的周期时间
     * @param unit [initialDelay] 和 [period] 的单位
     * @return 返回一个 ScheduledFuture，用于控制已经启动的任务
     */
    @JvmStatic
    fun runAtFixedRate(
        runnable: NamedRunnable,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit
    ): ScheduledFuture<*> {
        initScheduledPool()
        return scheduledPool.scheduleAtFixedRate(runnable, initialDelay, period, unit)
    }

    /**
     * 使用主线程的 Handler.post() 调度一个 [runnable] 任务
     * @param runnable 任务对象
     */
    @JvmStatic
    fun runOnMain(runnable: Runnable): Boolean {
        return mainHandler.post(runnable)
    }

    /**
     * 使用主线程的 Handler.post Delayed()  延迟调度一个 [runnable] 任务
     *
     * @param runnable 任务对象
     * @param delayMillis 延迟时间，单位毫秒
     */
    @JvmStatic
    fun runDelayOnMain(runnable: Runnable, delayMillis: Long): Boolean {
        return mainHandler.postDelayed(runnable, delayMillis)
    }

    private fun initScheduledPool() {
        if (!this::scheduledPool.isInitialized) {
            val processorCount = Runtime.getRuntime().availableProcessors()
            scheduledPool =
                RulerExecutors.newScheduledThreadPool(processorCount, "RulerScheduledPool")
        }
    }

    private fun initCachedPool() {
        if (!this::cachedPool.isInitialized) {
            cachedPool = RulerExecutors.newCachedThreadPool("RulerCachedPool")
        }
    }
}