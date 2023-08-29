package com.fanji.android

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import com.fanji.android.resource.base.BaseApplication
import com.fanji.android.util.LogUtil

/**
 * @Author:jiangshide
 * @Date:8/28/23
 * @Email:18311271399@163.com
 * @Description:
 */
class FJApp : BaseApplication(), ActivityLifecycleCallbacks {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        super.onActivityCreated(activity, savedInstanceState)
        LogUtil.e("--jsd--", "onActivityCreated~activity:", activity)
    }

    override fun onActivityDestroyed(activity: Activity) {
        super.onActivityDestroyed(activity)
        LogUtil.e("--jsd--", "onActivityDestroyed~activity:", activity)
    }
}