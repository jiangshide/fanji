package com.fanji.android.resource.bottomsheet

/**
 * created by jiangshide on 5/22/21.
 * email:18311271399@163.com
 */
interface BackPressedConcerned {
    /**
     * 返回 true 表示被中间消费掉，不必再传递了
     */
    fun onBackPressed(): Boolean
}