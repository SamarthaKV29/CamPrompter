// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
}

buildscript {
    extra["minSdkVersion"] = 15
    extra["compileSdkVersion"] = 31
    extra["targetSdkVersion"] = 31
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("io.deepmedia.tools:publisher:0.6.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}