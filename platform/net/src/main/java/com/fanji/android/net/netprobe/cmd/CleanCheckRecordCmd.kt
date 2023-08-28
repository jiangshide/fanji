package com.fanji.android.net.netprobe.cmd

import com.fanji.android.net.netprobe.data.CheckData

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-2 上午11:53
 */
internal class CleanCheckRecordCmd<T : CheckData>(
    delay: Long,
    private val records: MutableCollection<T>
) : AbstractCmd(CmdType.CLEAN_CHECK_RECORD, ExecutorType.COMPUTE, delay) {
    override fun run() {
        val now = System.currentTimeMillis()
        val deletedRecords = records.filter { it.validUtil <= now }
        deletedRecords.forEach {
            records.remove(it)
        }
    }
}