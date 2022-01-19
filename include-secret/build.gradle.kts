plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("com.github.gmazzo.buildconfig") version "3.0.3"
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

            }
        }
        val androidMain by getting {
            dependencies {

            }
        }
        val desktopMain by getting {
            dependencies {

            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm("colors", "=1.4.0"))//temp vulnerability fix, use strict version 1.4.0
            }
        }
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

/**
 * In file: local.properties, set key:    mapTilerSecretKey=xXxXxXxXxXxXx
 * Here you can get this key: https://cloud.maptiler.com/maps/streets/  (register and look at url field ?key=...#)
 */
val MAPTILER_SECRET_KEY = project.getLocalProperty("mapTilerSecretKey", "please_set_secret_key_in_local.properties")

buildConfig {
    className("GeneratedSecretConfig")   // forces the class name. Defaults to 'BuildConfig'
    packageName("com.map")
    buildConfigField("String", "GENERATED_MAPTILER_SECRET_KEY", """ "$MAPTILER_SECRET_KEY" """)

//    sourceSets.named<BuildConfigSourceSet>("jvmMain") {
//        buildConfigField("String", "PLATFORM", "\"jvm\"")
//        buildConfigField("String", "JVM_VALUE", "\"aJvmValue\"")
//    }

//    sourceSets.named<BuildConfigSourceSet>("jsMain") {
//        buildConfigField("String", "PLATFORM", "\"js\"")
//        buildConfigField("String", "JS_VALUE", "\"aJsValue\"")
//    }
}

repositories {
    google()
    mavenCentral()
}
