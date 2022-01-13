import org.jetbrains.compose.compose

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

version = "1.0-SNAPSHOT"

kotlin {
    android()
    jvm("desktop")
    js(IR) {
        browser()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.map:model:1.0")
                implementation(compose.runtime)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("com.map:ui-android-desktop:1.0")
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation("com.map:ui-android-desktop:1.0")
                implementation(compose.desktop.common)
            }
        }
//        val jsMain by getting {
//            dependencies {
//                implementation(npm("colors", "=1.4.0"))//temp vulnerability fix, use strict version 1.4.0
//            }
//        }
    }
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            res.srcDirs("src/androidMain/res")
        }
    }
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}
