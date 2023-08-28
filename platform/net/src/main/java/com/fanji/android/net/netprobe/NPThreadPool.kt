package com.fanji.android.net.netprobe

import com.fanji.android.net.netprobe.cmd.AbstractCmd
import com.fanji.android.net.netprobe.cmd.CmdType
import com.fanji.android.net.netprobe.cmd.ExecutorType
import com.fanji.android.net.netprobe.cmd.ShutdownCmd
import com.fanji.android.thread.pool.RulerThreadPoolExecutor
import java.util.concurrent.DelayQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-2 上午11:24
 */
internal object NPThreadPool {

    @Volatile
    private var start = false

    private val computeExecutor =
        RulerThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, LinkedBlockingQueue<Runnable>(),
            ThreadFactory { r -> Thread(null, r, "NPComputeThread", 1) })

    private val ioExecutor =
        RulerThreadPoolExecutor(0, 10, 10, TimeUnit.SECONDS, LinkedBlockingQueue<Runnable>(),
            ThreadFactory { r -> Thread(null, r, "NPThread", 1) })

    private lateinit var cmdDispatcher: Thread

    private val cmdQueue = DelayQueue<AbstractCmd>()

    fun start() {
        if (start) return
        start = true
        cmdDispatcher = Thread(null, {
            while (true) {
                val cmd = cmdQueue.take()
                if (cmd.type == CmdType.SHUTDOWN) {
                    shutdown()
                    break
                } else {
                    when (cmd.executor) {
                        ExecutorType.COMPUTE -> computeExecutor.execute { cmd.run() }
                        ExecutorType.IO -> ioExecutor.execute { cmd.run() }
                    }
                }
            }
        })
        cmdDispatcher.start()
    }

    fun runCmd(cmd: AbstractCmd, replace: Boolean = false) {
        if (replace && cmdQueue.remove(cmd)) {
            //
        }
        cmdQueue.add(cmd)
    }

    fun removeCmd(cmd: AbstractCmd): Boolean {
        return cmdQueue.remove(cmd)
    }

    fun stop() {
        if (!start) return
        start = false
        runCmd(ShutdownCmd())
    }

    private fun shutdown() {
        computeExecutor.shutdownNow()
        computeExecutor.awaitTermination(10, TimeUnit.SECONDS)
        ioExecutor.shutdown()
        ioExecutor.awaitTermination(10, TimeUnit.SECONDS)
    }
}