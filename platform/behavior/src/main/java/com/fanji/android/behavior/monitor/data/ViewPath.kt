package com.fanji.android.behavior.monitor.data

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-17 上午11:50
 */
data class ViewPath(
    var path: String,
    var viewContainer: ViewContainer,
    var webUrl: String,
    var listInfo: String,
    var inScrollableContainer: Boolean
)