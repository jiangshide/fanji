plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {

    namespace = "com.fanji.android"
    compileSdk = 33

//    signingConfigs {
//        create("sign"){
//            storeFile = file("$rootDir/${providers.gradleProperty("STORE_FILE").get()}")
//            storePassword = providers.gradleProperty("STORE_FILE").get()
//            keyAlias = providers.gradleProperty("KEY_ALIAS").get()
//            keyPassword = providers.gradleProperty("KEY_PSW").get()
//            enableV1Signing = true
//            enableV2Signing = true
//            enableV3Signing = true
//            enableV4Signing = true
//        }
//        getByName("debug"){
//            storeFile = file("$rootDir/${providers.gradleProperty("STORE_FILE").get()}")
//            storePassword = providers.gradleProperty("STORE_FILE").get()
//            keyAlias = providers.gradleProperty("KEY_ALIAS").get()
//            keyPassword = providers.gradleProperty("KEY_PSW").get()
//            enableV1Signing = true
//            enableV2Signing = true
//            enableV3Signing = true
//            enableV4Signing = true
//        }
//    }

    defaultConfig {
        applicationId = "com.fanji.android"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        multiDexEnabled = providers.gradleProperty("MULTIDEXENABLED").get().toBoolean()
//        signingConfig = signingConfigs.getByName("sign")

        resConfig("zh")
        ndk{
            abiFilters += listOf("armeabi","armeabi-v7a","arm64-v8a")
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    //指定aapt不做图片压缩:图片已压缩不需要做，否则可能回增加图片大小
//    aaptOptions {
//        cruncherEnabled = false
//    }

    //关闭lint检查
//    lintOptions {
//        disable("ResourceType")
//    }

    //jniLibs目录指向libs目录
    sourceSets {
        getByName("main"){
            jniLibs.srcDirs("libs")
        }
    }

    //优化transformClassDexBuilderForDebug的时间
    dexOptions{
        preDexLibraries = true
        maxProcessCount = 8
    }

    //禁止生成依赖元素:只针对play商店
//    dependenciesInfo {
//        includeInApk = false
//    }

    buildTypes {
        release {
            isMinifyEnabled = true
//            isZipAlignEnabled = true
//            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true

        viewBinding = true
//        // Determines whether to generate a BuildConfig class.
//        buildConfig = true
//        // Determines whether to support Data Binding.
//        dataBinding = false
//        // Note that the dataBinding.enabled property is now deprecated.
//        aidl = true
//        // Determines whether to generate binder classes for your AIDL files.
//        renderScript = true
//        // Determines whether to support RenderScript.
//        resValues = true
//        // Determines whether to support injecting custom variables into the module’s R class.
//        shaders = true
//        // Determines whether to support shader AOT compilation.
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    android.applicationVariants.all {
        outputs.all {
            if(this is com.android.build.gradle.internal.api.ApkVariantOutputImpl){
                this.outputFileName = "fanji.apk"
            }
        }
    }
}

//获取git提交次数
fun getGitCommitCount(): String{
    val os = org.apache.commons.io.output.ByteArrayOutputStream()
    project.exec {
        commandLine = "git rev-list --count HEAD".split(" ")
        standardOutput = os
    }
    return String(os.toByteArray()).trim()
}

dependencies {

//    implementation(fileTree(mapOf("dir" to "libs","include" to listOf("*.jar"))))
//    implementation(fileTree("libs").include("*.jar", "*.aar"))

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(project(":resource"))

    // 版本号带有 @aar 形式的依赖比较特殊，需要按如下方法写
//    api(bizLibs.***.library) {
//        artifact {
//            classifier = "release"
//            type = "aar"
//        }
//    }
}