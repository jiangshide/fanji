package com.fanji.android.behavior.monitor.data

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-17 上午11:49
 */
data class ViewContent(
    var type: Int,
    var content: String,
    var fontSize: Float,
    var location: IntArray
)