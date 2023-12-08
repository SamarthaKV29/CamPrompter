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
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = false
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    val fragment_version = "1.6.2"

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.fragment:fragment-ktx:$fragment_version")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("com.google.android.material:material:1.9.0")

    implementation("io.github.tutorialsandroid:kalertdialog:20.5.8")
    implementation("io.github.tutorialsandroid:progressx:7.0.0")
    
    implementation("com.otaliastudios:cameraview:2.7.2")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}