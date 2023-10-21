package com.fanji.android

import com.fanji.android.resource.base.BaseApplication
import com.fanji.android.util.ActivityUtils
import com.umeng.commonsdk.UMConfigure
import com.umeng.socialize.PlatformConfig

/**
 * @Author:jiangshide
 * @Date:8/28/23
 * @Email:18311271399@163.com
 * @Description:
 */
class FJApp : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        UMConfigure.preInit(this, "64f676418efadc41dcd47426", "Umeng")//todo the temp

        UMConfigure.init(
            this,
            "64f676418efadc41dcd47426",
            "fanji",
            UMConfigure.DEVICE_TYPE_PHONE,
            ""
        )//todo the temp
        PlatformConfig.setQQZone("101898921", "88c231e00cb4632ea4164ec3589bbae7")
        ActivityUtils.instance.init(this)
    }
}