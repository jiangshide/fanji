plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

val HOST = mapOf(
    "test" to providers.gradleProperty("API_QA_HOST").get(),
    "pre" to providers.gradleProperty("API_PRE_HOST").get(),
    "production" to providers.gradleProperty("API_PRODUCTION_HOST").get()
)


android {
    namespace = "com.fanji.android.net"
    compileSdk = 33

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        minSdk = 24

        buildConfigField(
            "String",
            "API_HOST",
            "\"${HOST[providers.gradleProperty("ENVIRONMENT").get()]}\""
        )
        buildConfigField(
            "String",
            "API_HOST_PRODUCTION",
            "\"${providers.gradleProperty("API_PRODUCTION_HOST").get()}\""
        )
        buildConfigField(
            "String",
            "API_HOST_TEST",
            "\"${providers.gradleProperty("API_QA_HOST").get()}\""
        )

        buildConfigField(
            "String",
            "RESP_CODE",
            "\"${providers.gradleProperty("RESP_CODE").get()}\""
        )
        buildConfigField(
            "Integer",
            "RESP_CODE_VALUE",
            providers.gradleProperty("RESP_CODE_VALUE").get()
        )
        buildConfigField(
            "String",
            "RESP_DATE",
            "\"${providers.gradleProperty("RESP_DATE").get()}\""
        )
        buildConfigField("String", "RESP_RES", "\"${providers.gradleProperty("RESP_RES").get()}\"")
        buildConfigField("String", "RESP_MSG", "\"${providers.gradleProperty("RESP_MSG").get()}\"")
        buildConfigField(
            "String",
            "RESP_VERSION",
            "\"${providers.gradleProperty("RESP_VERSION").get()}\""
        )

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

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    api("com.squareup.retrofit2:retrofit:2.5.0")
    api("com.squareup.retrofit2:converter-gson:2.5.0")
    api("io.reactivex.rxjava2:rxjava:2.1.14")
    api("io.reactivex.rxjava2:rxandroid:2.1.0")
    api("com.jakewharton.retrofit:retrofit2-rxjava2-adapter:1.0.0")

//    api(project(":okhttp"))
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation(project(":util"))
    api(project(":thread"))
//    implementation(project(":scrurity"))
}