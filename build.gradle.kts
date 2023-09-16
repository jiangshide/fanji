// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id("com.android.library") version "8.1.0" apply false
}

buildscript {
    repositories {
        google()
        jcenter()
        maven { setUrl("https://repo1.maven.org/maven2/") }
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.4.0")
    }
}

//allprojects {
//    repositories {
//        google()
//        jcenter()
//        maven { setUrl("https://repo1.maven.org/maven2/") }
//    }
//}