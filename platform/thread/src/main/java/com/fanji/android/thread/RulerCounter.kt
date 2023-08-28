package com.fanji.android.thread

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午2:27
 */
object RulerCounter {
    /**
     * 获取当前所有线程的数量信息
     * @return 一个只读的 list
     */
    fun getPoolCount(): List<Pair<String, Int>> {
        return poolCounter.map {
            val count = it.value.get()
            Pair(it.key, count)
        }
    }

    //TODO 要注意下，数量会不会爆炸
    internal val poolCounter: ConcurrentHashMap<String, AtomicInteger> = ConcurrentHashMap()

    fun getCounter(name: String): AtomicInteger {
        var counter = poolCounter[name]
        if (counter == null) {
            counter = AtomicInteger(0)
            poolCounter[name] = counter
        }
        return counter
    }
}