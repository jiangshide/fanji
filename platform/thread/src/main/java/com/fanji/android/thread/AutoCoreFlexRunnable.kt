package com.fanji.android.thread

import com.fanji.android.thread.data.InternalNamed
import com.fanji.android.util.LogUtil
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import kotlin.math.max

/**
 * 根据任务执行时间，自动调整 ScheduledThreadPool.coreSize 的任务
 *
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午2:39
 */
internal class AutoCoreFlexRunnable(
    private val scheduledPool: ScheduledThreadPoolExecutor,
    private val iteratorFactory: IteratorFactory
) : NamedRunnable("AutoCoreFlex") {

    /**
     * 获取正在执行的 Future与 Runnable 对应关系的 Factory，可能会修改原数据（例如删除已完成的任务）
     */
    interface IteratorFactory {
        fun create(): MutableIterator<MutableMap.MutableEntry<ScheduledFuture<*>, InternalNamed>>
    }

    /**
     * 常驻任务的判断阈值，单位毫秒
     *
     * 默认 45秒
     */
    var residentThreshold = 45000L

    private val originCoreSize = scheduledPool.corePoolSize

    private val residentList = mutableListOf<InternalNamed>()

    override fun execute() {
        val taskIterator = iteratorFactory.create()
        var delta = 0
        for (entry in taskIterator) {
            val internalNamed = entry.value
            if (entry.key.isDone) {
                taskIterator.remove()
                if (internalNamed.isResident) {
                    // 常驻任务竟然执行完了，那就缩减 core size 吧
                    delta--
                    residentList.remove(internalNamed)
                    LogUtil.d("Resident task finished! name:${internalNamed.name}, delta=$delta")
                }
            } else if (isFreshResidentTask(internalNamed)) {
                // 发现新的常驻线程，做好标记、core size +1、准备上报
                internalNamed.isResident = true
                delta++
                residentList.add(internalNamed)
                LogUtil.d("Found resident task! name:${internalNamed.name}, delta=$delta")
            }
        }
        val newSize = scheduledPool.corePoolSize + delta
        scheduledPool.corePoolSize = max(originCoreSize, newSize)
        LogUtil.d("New SchedulePool core size = $newSize")
        report()
    }

    private fun isFreshResidentTask(internal: InternalNamed): Boolean {
        return !internal.isResident
                && internal.startTimestamp > 0
                && System.currentTimeMillis() - internal.startTimestamp > residentThreshold
    }

    private var flexCount = 0

    private fun report() {
        // 每调整10次，上报一次常驻任务信息
        flexCount++
        if (flexCount < 10) {
            return
        }
        flexCount = 0
        if (residentList.size > 0) {
//            val currentTime = System.currentTimeMillis()
//            val json = JsonLog()
//            json.logType = "RulerResidentLog"
//            json.put("total", residentList.size)
//            for (runnable in residentList) {
//                val duration = (currentTime - runnable.startTimestamp) / 1000
//                json.put(runnable.name, duration)
//            }
//            if (RulerScheduler.isDebug) {
//                RulerLog.d(json.toString())
//            }
//            DroidAPM.getInstance().recordJson(json)
            residentList.clear()
        }
    }

}