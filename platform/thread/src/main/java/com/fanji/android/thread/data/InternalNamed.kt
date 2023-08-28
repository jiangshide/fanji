package com.fanji.android.thread.data

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午2:38
 */
data class InternalNamed(val name: String) {

    /**
     * 任务开始执行的时间戳，初始值为-1
     */
    @Volatile
    internal var startTimestamp: Long = -1

    /**
     * 是否是常驻任务，如果任务执行时间超过阈值则认为是常驻任务。
     *
     * 请参考 [AutoCoreFlexRunnable.residentThreshold]
     */
    internal var isResident: Boolean = false

}