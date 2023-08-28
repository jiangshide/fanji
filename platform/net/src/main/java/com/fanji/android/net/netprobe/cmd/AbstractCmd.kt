package com.fanji.android.net.netprobe.cmd

import java.util.concurrent.Delayed
import java.util.concurrent.TimeUnit

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午3:42
 */
internal abstract class AbstractCmd(
    val type: CmdType,
    val executor: ExecutorType,
    private val delay: Long
) : Delayed {
    private val validUntil = System.currentTimeMillis() + delay

    override fun compareTo(other: Delayed?): Int {
        return (this.getDelay(TimeUnit.MILLISECONDS) - (other?.getDelay(TimeUnit.MILLISECONDS)
            ?: 0L)).toInt()
    }

    override fun getDelay(unit: TimeUnit): Long {
        return unit.convert(this.validUntil - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
    }

    abstract fun run()
    override fun toString(): String {
        return "AbstractCmd(type=$type, executor=$executor, delay=$delay, validUntil=$validUntil)"
    }
}