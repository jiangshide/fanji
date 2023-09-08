plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.fanji.android.third"
    compileSdk = 33

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        minSdk = 24

        buildConfigField("String", "CHANNEL", "\"${providers.gradleProperty("CHANNEL").get()}\"")

        buildConfigField("String", "ENDPOINT", "\"${providers.gradleProperty("ENDPOINT").get()}\"")
        buildConfigField(
            "String",
            "ACCESSKEY_ID",
            "\"${providers.gradleProperty("ACCESSKEY_ID").get()}\""
        )
        buildConfigField(
            "String",
            "ACCESSKEY_SECRET",
            "\"${providers.gradleProperty("ACCESSKEY_SECRET").get()}\""
        )
        buildConfigField("String", "BUCKET", "\"${providers.gradleProperty("BUCKET").get()}\"")

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
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(project(":util"))
//    implementation("com.aliyun.ams:alicloud-android-httpdns:2.0.2")//httpDns
//    implementation("com.aliyun.ams:alicloud-android-man:1.2.0")//移动数据分析
    api("com.aliyun.dpa:oss-android-sdk:+")
}