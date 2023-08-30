plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.fanji.android.resource"
    compileSdk = 33

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        minSdk = 24

        resValue("string", "AMAP_KEY", "\"${providers.gradleProperty("AMAP_KEY").get()}\"")
        buildConfigField(
            "String",
            "WECHAT_APPID",
            "\"${providers.gradleProperty("WECHAT_APPID").get()}\""
        )
        buildConfigField(
            "String",
            "WECHAT_APPSECRET",
            "\"${providers.gradleProperty("WECHAT_APPSECRET").get()}\""
        )

        buildConfigField(
            "String",
            "USE_AGREEMENT",
            "\"${providers.gradleProperty("USE_AGREEMENT").get()}\""
        )
        buildConfigField(
            "String",
            "PRIVACY_AGREEMENT",
            "\"${providers.gradleProperty("PRIVACY_AGREEMENT").get()}\""
        )
        buildConfigField(
            "String",
            "FUNCTION_INTRODUCE",
            "\"${providers.gradleProperty("FUNCTION_INTRODUCE").get()}\""
        )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    sourceSets {
        getByName("main"){
            jniLibs.srcDirs("libs")
        }
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
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    api(project(":ui"))
    api(project(":play"))
    api("com.tencent.mm.opensdk:wechat-sdk-android:+")

    api("com.amap.api:location:latest.integration")
    api("com.amap.api:search:latest.integration")
//    api("com.amap.api:3dmap:latest.integration")

    api(project(":third"))
}