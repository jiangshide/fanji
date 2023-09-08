package com.fanji.android

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import com.fanji.android.resource.base.BaseApplication
import com.fanji.android.util.LogUtil
import com.umeng.commonsdk.UMConfigure

/**
 * @Author:jiangshide
 * @Date:8/28/23
 * @Email:18311271399@163.com
 * @Description:
 */
class FJApp : BaseApplication(), ActivityLifecycleCallbacks {

    override fun onCreate() {
        super.onCreate()
        UMConfigure.preInit(this,"64f676418efadc41dcd47426","Umeng")//todo the temp
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