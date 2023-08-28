package com.fanji.android.ui.permission

/**
 * @author 蒋世德
 * @email 18311271399@163.com
 * @since 21-5-26 上午11:52
 */
open interface OnPermissionCallback {
    /**
     * 有权限被同意授予时回调
     *
     * @param permissions           请求成功的权限组
     * @param all                   是否全部授予了
     */
    fun onGranted(permissions: List<String?>?, all: Boolean)

    /**
     * 有权限被拒绝授予时回调
     *
     * @param permissions            请求失败的权限组
     * @param never                  是否有某个权限被永久拒绝了
     */
    fun onDenied(permissions: List<String?>?, never: Boolean) {}
}