package com.fanji.android.permission

/**
 * @author 蒋世德
 * @email 18311271399@163.com
 * @since 21-5-26 上午11:50
 */
internal class ManifestRegisterException : RuntimeException {
    constructor() : super("No permissions are registered in the manifest file") {
        // 清单文件中没有注册任何权限
    }

    constructor(permission: String) : super("$permission: Permissions are not registered in the manifest file") {
        // 申请的危险权限没有在清单文件中注册
    }
}