plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.fanji.android.umeng"
    compileSdk = 33

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(fileTree("libs").include("*.jar", "*.aar"))

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation(files("libs/open_sdk_3.5.14.4_r377deaa_lite.jar"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // 友盟统计SDK
    api("com.umeng.umsdk:common:9.6.5") // (必选)版本号
    api("com.umeng.umsdk:asms:1.8.0") // asms包依赖(必选)
    api("com.umeng.umsdk:uyumao:1.1.2") //高级运营分析功能依赖库

    api("com.umeng.umsdk:apm:1.2.0") // apm包依赖(可选)
    api("com.umeng.umsdk:link:1.2.0")
    api("com.umeng.umsdk:game:9.2.0+G") // 游戏统计SDK依赖(可选)

    api("com.umeng.umsdk:uyumao:1.1.2") // 高级运营分析功能依赖库(可选)
//    api("com.umeng.umsdk:common:9.3.8")// 必选
//    api("com.umeng.umsdk:asms:1.2.3")// 必选
//    api("com.umeng.umsdk:uverify:2.5.1")// 必选
//    api("com.umeng.umsdk:uverify-main:2.0.3")// 必选
//    api("com.umeng.umsdk:uverify-logger:2.0.3")// 必选
//    api("com.umeng.umsdk:uverify-crashshield:2.0.3")// 必选

    api("com.umeng.umsdk:uverify:2.5.9")// 必选
    api("com.umeng.umsdk:uverify-main:2.1.4")// 必选
    api("com.umeng.umsdk:uverify-logger:2.1.4")// 必选
    api("com.umeng.umsdk:uverify-crashshield:2.1.4")// 必选
//    api("com.android.support:appcompat-v7:27.1.1")//（最低版本）

    //友盟Push依赖（必须）
    api("com.umeng.umsdk:push:6.6.2")

    //友盟分享核心组件
    api("com.umeng.umsdk:share-core:7.3.2")

    //QQ
            api(files("libs/open_sdk_3.5.14.4_r377deaa_lite.jar"))
//            api("com.umeng.umsdk:share-qq:7.3.2")

    //微信
//            implementation("com.tencent.mm.opensdk:wechat-sdk-android-without-mta:6.8.0")
            api("com.umeng.umsdk:share-wx:7.3.2")
//
//    //新浪微博
//    api("io.github.sinaweibosdk:core:12.5.0@aar")
//    api("com.umeng.umsdk:share-sina:7.3.2")
//
//    //钉钉
//    api("com.alibaba.android:ddsharesdk:1.2.0@jar")
//    api("com.umeng.umsdk:share-dingding:7.3.2")
//
//    //抖音
//    api("com.bytedance.ies.ugc.aweme:opensdk-china-external:0.1.9.0")
//    api("com.bytedance.ies.ugc.aweme:opensdk-common:0.1.9.0")
////    api(files("libs/umeng-share-bytedance-7.3.2.jar"))
//
//    //荣耀账号授权
////    api(files("libs/Honor-openSDK-6.0.3.004.aar"))
////    api(files("libs/umeng-share-honor-7.3.2.jar"))
//
//    //支付宝
//    api("com.umeng.umsdk:share-alipay:7.3.2")

    //twitter官方sdk
//    api("com.twitter.sdk.android:twitter-core:3.1.1")
//    api("com.twitter.sdk.android:tweet-composer:3.1.1")
//    api(files('libs/umeng-share-twitter-7.3.2.jar"))
}