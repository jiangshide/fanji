package com.fanji.android.resource.base

//import com.fanji.android.aliyun.Aliyun
//import com.sanskrit.tencent.TencentIm
import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.fanji.android.resource.interceptor.PermissionInterceptor
import com.fanji.android.third.Aliyun
import com.fanji.android.ui.permission.FJPermission

/**
 * created by jiangshide on 5/12/21.
 * email:18311271399@163.com
 */
open class BaseApplication : Application(), Application.ActivityLifecycleCallbacks {

    override fun onCreate() {
        super.onCreate()
//        TencentIm.init(this)
//        OssClient.instance
//            .init()
//        Aliyun.init(this)
        Aliyun.init(this)

        // 设置权限申请拦截器
        FJPermission.interceptor = (PermissionInterceptor())
        // 告诉框架，当前项目已适配分区存储特性
//        FJPermission.isScopedStorage = true
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}