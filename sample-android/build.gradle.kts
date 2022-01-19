plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.compose")
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("com.map:mapview:1.0-SNAPSHOT")
    implementation("com.map:secret:1.0-SNAPSHOT")

    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.core:core-ktx:1.3.1")
    implementation("androidx.activity:activity-compose:1.3.1")
    implementation("org.jetbrains.compose.foundation:foundation:1.0.1")
}
