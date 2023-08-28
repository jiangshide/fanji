package com.fanji.android.ui.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.util.*

/**
 * @author 蒋世德
 * @email 18311271399@163.com
 * @since 21-5-26 上午11:45
 */
class FJPermission private constructor(context: Context) {
    /** Context 对象  */
    private val mContext: Context?

    /** 权限列表  */
    private var mPermissions: MutableList<String>? = null

    /**
     * 添加权限
     */
    fun permission(permission: String): FJPermission {
        if (mPermissions == null) {
            mPermissions = ArrayList(1)
        }
        mPermissions!!.add(permission)
        return this
    }

    /**
     * 添加权限组
     */
    fun permission(permissions: Array<String>): FJPermission {
        return permission(permissions.toMutableList())
    }

    fun permission(permissions: MutableList<String>): FJPermission {
        if (mPermissions == null) {
            mPermissions = permissions
        } else {
            mPermissions!!.addAll(permissions)
        }
        return this
    }

    /**
     * 请求权限
     */
    fun request(callback: OnPermissionCallback?) {
        if (mContext == null) {
            return
        }

        // 当前是否为调试模式
        val debugMode = isDebugMode(mContext)

        // 检查当前 Activity 状态是否是正常的，如果不是则不请求权限
        val fragmentActivity: FragmentActivity? = PermissionUtils.findFragmentActivity(mContext)
        if (!PermissionChecker.checkActivityStatus(fragmentActivity, debugMode)) {
            return
        }

        // 必须要传入正常的权限或者权限组才能申请权限
        if (!PermissionChecker.checkPermissionArgument(mPermissions, debugMode)) {
            return
        }
        if (debugMode) {
            // 检查申请的存储权限是否符合规范
            PermissionChecker.checkStoragePermission(mContext, mPermissions!!, isScopedStorage)
            // 检查申请的定位权限是否符合规范
            PermissionChecker.checkLocationPermission(mPermissions!!)
            // 检查申请的权限和 targetSdk 版本是否能吻合
            PermissionChecker.checkTargetSdkVersion(mContext, mPermissions!!)
        }

        // 优化所申请的权限列表
        PermissionChecker.optimizeDeprecatedPermission(mPermissions!!)
        if (debugMode) {
            // 检测权限有没有在清单文件中注册
            PermissionChecker.checkPermissionManifest(mContext, mPermissions!!)
        }
        if (PermissionUtils.isGrantedPermissions(mContext, mPermissions!!)) {
            // 证明这些权限已经全部授予过，直接回调成功
            callback?.onGranted(mPermissions, true)
            return
        }

        // 申请没有授予过的权限
        interceptor!!.requestPermissions(fragmentActivity!!, callback!!, mPermissions)
    }

    companion object {
        /** 权限设置页跳转请求码  */
        const val REQUEST_CODE = 1024 + 1

        /** 权限请求拦截器  */
        private var sPermissionInterceptor: IPermissionInterceptor? = null

        /** 调试模式  */
        private var sDebugMode: Boolean? = null
        /**
         * 是否已经适配了 Android 10 分区存储特性
         */
        /** 分区存储  */
        var isScopedStorage = false
            set

        /**
         * 设置请求的对象
         *
         * @param context          当前 Activity，可以传入栈顶的 Activity
         */
        fun with(context: Context): FJPermission {
            return FJPermission(context)
        }

        fun with(fragment: Fragment): FJPermission {
            val activity = fragment.activity
            return with(activity!!)
        }

        /**
         * 是否为调试模式
         */
        fun setDebugMode(debug: Boolean) {
            sDebugMode = debug
        }

        private fun isDebugMode(context: Context): Boolean {
            if (sDebugMode == null) {
                sDebugMode = context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
            }
            return sDebugMode!!
        }
        /**
         * 获取权限请求拦截器
         */
        /**
         * 设置权限请求拦截器
         */
        var interceptor: IPermissionInterceptor?
            get() {
                if (sPermissionInterceptor == null) {
                    sPermissionInterceptor = object : IPermissionInterceptor {}
                }
                return sPermissionInterceptor
            }
            set(interceptor) {
                sPermissionInterceptor = interceptor
            }

        /**
         * 判断一个或多个权限是否全部授予了
         */
        fun isGranted(context: Context, permission: String?): Boolean {
            return PermissionUtils.isGrantedPermission(context, permission)
        }

        fun isGranted(context: Context, permissions: Array<String>): Boolean {
            return isGranted(context, permissions.toMutableList())
        }

        fun isGranted(context: Context, permissions: List<String>): Boolean {
            return PermissionUtils.isGrantedPermissions(context, permissions)
        }

        /**
         * 获取没有授予的权限
         */
        fun getDenied(context: Context, permissions: Array<String>): List<String> {
            return getDenied(context, permissions.toMutableList())
        }

        fun getDenied(context: Context, permissions: List<String>): List<String> {
            return PermissionUtils.getDeniedPermissions(context, permissions)
        }

        /**
         * 判断某个权限是否是特殊权限
         */
        fun isSpecial(permission: String): Boolean {
            return PermissionUtils.isSpecialPermission(permission)
        }

        /**
         * 判断一个或多个权限是否被永久拒绝了（注意不能在请求权限之前调用，应该在 [OnPermissionCallback.onDenied] 方法中调用）
         */
        fun isPermanentDenied(activity: Activity, permission: String): Boolean {
            return PermissionUtils.isPermissionPermanentDenied(activity, permission)
        }

        fun isPermanentDenied(activity: Activity, permissions: Array<String>): Boolean {
            return isPermanentDenied(activity, permissions.toMutableList())
        }

        fun isPermanentDenied(activity: Activity, permissions: List<String>): Boolean {
            return PermissionUtils.isPermissionPermanentDenied(activity, permissions!!)
        }

        fun startPermissionActivity(context: Context, permission: String) {
            startPermissionActivity(context, PermissionUtils.asArrayList(permission))
        }

        fun startPermissionActivity(context: Context, permissions: Array<String>) {
            startPermissionActivity(context, permissions.toMutableList())
        }

        /**
         * 跳转到应用权限设置页
         *
         * @param permissions           没有授予或者被拒绝的权限组
         */
        @JvmOverloads
        fun startPermissionActivity(
            context: Context,
            permissions: List<String?>? = null as List<String?>?
        ) {
            val activity: Activity? = PermissionUtils.findFragmentActivity(context)
            if (activity != null) {
                startPermissionActivity(activity, permissions)
                return
            }
            val intent = PermissionSettingPage.getSmartPermissionIntent(context, permissions)
            intent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        fun startPermissionActivity(activity: Activity, permission: String) {
            startPermissionActivity(activity, PermissionUtils.asArrayList(permission))
        }

        fun startPermissionActivity(activity: Activity, permissions: Array<String>) {
            startPermissionActivity(activity, permissions.toMutableList())
        }

        fun startPermissionActivity(activity: Activity, permissions: List<String>) {
            activity.startActivityForResult(
                PermissionSettingPage.getSmartPermissionIntent(
                    activity,
                    permissions
                ), REQUEST_CODE
            )
        }

        fun startPermissionActivity(fragment: Fragment) {
            startPermissionActivity(fragment, null as List<String>)
        }

        fun startPermissionActivity(fragment: Fragment, permissions: String) {
            startPermissionActivity(fragment, arrayListOf(permissions))
        }

        fun startPermissionActivity(fragment: Fragment, permissions: Array<String>) {
            startPermissionActivity(fragment, permissions.toMutableList())
        }

        /**
         * 跳转到应用权限设置页
         *
         * @param permissions           没有授予或者被拒绝的权限组
         */
        fun startPermissionActivity(fragment: Fragment, permissions: List<String>) {
            val activity: FragmentActivity = fragment.activity ?: return
            fragment.startActivityForResult(
                PermissionSettingPage.getSmartPermissionIntent(
                    activity,
                    permissions
                ), REQUEST_CODE
            )
        }
    }

    /**
     * 私有化构造函数
     */
    init {
        mContext = context
    }
}