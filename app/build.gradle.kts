plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.rightapps.camprompter"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.rightapps.camprompter"
        minSdk = 27
        targetSdk = 33
        versionCode = 2
        versionName = "1.1-alpha"
        base.archivesBaseName = "${applicationId}-v${versionName}"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

}
val keyPath = "../../../keys"
val signingJks = "${keyPath}/main-campropter.jks"
val passphrase = file("${keyPath}/camprompter.passphrase").readText().trim()

if (file(signingJks).exists()) run {
    android {
        signingConfigs {
            create("release") {
                keyAlias = "main"
                keyPassword = passphrase
                storeFile = file(signingJks)
                storePassword = passphrase
            }
        }
        buildTypes {
            getByName("release") {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
                signingConfig = signingConfigs.getByName("release")
                isDebuggable = false
            }
        }
    }
}


dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.preference:preference-ktx:1.2.1")

    implementation("com.google.android.material:material:1.9.0")

    implementation("io.github.tutorialsandroid:kalertdialog:20.5.8")
    implementation("io.github.tutorialsandroid:progressx:7.0.0")

    implementation("com.otaliastudios:cameraview:2.7.2")
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")
    implementation("androidx.preference:preference:1.2.1")
//    implementation(project(mapOf("path" to ":app:libs:cameraview")))

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}