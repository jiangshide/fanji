plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.fanji.android.okhttp"
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

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(project(":util"))
    api("com.squareup.okio:okio:2.8.0")
    compileOnly("org.bouncycastle:bcprov-jdk15on:1.65")
    compileOnly("org.bouncycastle:bcpkix-jdk15on:1.65")
    compileOnly("org.bouncycastle:bctls-jdk15on:1.65")
    compileOnly("org.conscrypt:conscrypt-openjdk-uber:2.5.1")
    compileOnly("org.openjsse:openjsse:1.1.0")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    compileOnly("org.codehaus.mojo:animal-sniffer-annotations:1.18")

//    api("androidx.room:room-runtime:2.3.0")
//    annotationProcessor("androidx.room:room-compiler:2.3.0")
//
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.5")
}