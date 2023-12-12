pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven (url="https://plugins.gradle.org/m2/")
        gradlePluginPortal()
    }
}

rootProject.name = "CamPrompter"
include(":app")
//include(":app:libs:cameraview")