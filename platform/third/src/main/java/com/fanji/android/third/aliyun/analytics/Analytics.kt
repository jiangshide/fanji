//package com.fanji.android.third.aliyun.analytics
//
//import android.app.Application
//import com.alibaba.sdk.android.man.MANAnalytics
//import com.alibaba.sdk.android.man.MANPageHitHelper
//import com.alibaba.sdk.android.man.MANService
//import com.alibaba.sdk.android.man.MANServiceProvider
//import com.fanji.android.third.BuildConfig
//import com.fanji.android.util.AppUtil
//
///**
// * created by jiangshide on 5/13/21.
// * email:18311271399@163.com
// */
//object Analytics {
//
//    @Volatile
//    private var manService: MANService? = null
//
//    @Volatile
//    private var manAnalytics: MANAnalytics? = null
//
//    @Volatile
//    private var manPageHitHelper: MANPageHitHelper? = null
//
//    fun initAnalytics(application: Application) {
//        // 获取MAN服务
//        val manService = MANServiceProvider.getService()
//        // 打开调试日志
////        manService.manAnalytics.turnOnDebug()
//        // 若需要关闭 SDK 的自动异常捕获功能可进行如下操作,详见文档5.4
//        //manService.getMANAnalytics().turnOffCrashReporter()
//        // 设置渠道（用以标记该app的分发渠道名称），如果不关心可以不设置即不调用该接口，渠道设置将影响控制台【渠道分析】栏目的报表展现。如果文档3.3章节更能满足您渠道配置的需求，就不要调用此方法，按照3.3进行配置即可
//        manService?.manAnalytics?.setChannel(
//            AppUtil.getMeta(
//                application.applicationContext,
//                BuildConfig.CHANNEL
//            )
//        )
//        // MAN初始化方法之一，通过插件接入后直接在下发json中获取appKey和appSecret初始化
////        manService?.manAnalytics?.init(application, application.applicationContext)
//        // MAN另一初始化方法，手动指定appKey和appSecret
//        // String appKey = "******";
//        // String appSecret = "******";
//        // manService.getMANAnalytics().init(this, getApplicationContext(), appKey, appSecret)
//        // 通过此接口关闭页面自动打点功能，详见文档4.2
////        manService.manAnalytics.turnOffAutoPageTrack()
//        manService?.manAnalytics?.setAppVersion(AppUtil.getAppVersionName())
//    }
//
//
//    private fun getManService(): MANService? {
//        if (manService == null) {
//            manService = MANServiceProvider.getService()
//        }
//        return manService
//    }
//
//    private fun getManAnalytics(): MANAnalytics? {
//        if (manAnalytics == null) {
//            manAnalytics = getManService()?.manAnalytics
//        }
//        return manAnalytics
//    }
//
//    private fun getManPageHitHelper(): MANPageHitHelper? {
//        if (manPageHitHelper == null) {
//            manPageHitHelper = getManService()?.manPageHitHelper
//        }
//        return manPageHitHelper
//    }
//
//    /**
//     * 注册用户埋点
//     */
//    fun userRegister(userNick: String): Analytics {
//        getManAnalytics()?.userRegister(userNick)
//        return this
//    }
//
//    /**
//     * 用户登录及注销埋点
//     */
//    fun updateUserAccount(userNick: String = "", userId: String = ""): Analytics {
//        getManAnalytics()?.updateUserAccount(userNick, userId)
//        return this
//    }
//
//    fun updatePageProperties(key: String, value: String): Analytics {
//        val map = HashMap<String, String>()
//        map[key] = value
//        getManPageHitHelper()?.updatePageProperties(map)
//        return this
//    }
//
//    fun updatePageProperties(map: HashMap<String, String>): Analytics {
//        getManPageHitHelper()?.updatePageProperties(map)
//        return this
//    }
//}