plugins {
    id("com.android.library")
    kotlin("multiplatform")
}

version = "1.0"

val KTOR_VERSION = "1.6.7"
val ktorCore = "io.ktor:ktor-client-core:${KTOR_VERSION}"
val ktorCIO = "io.ktor:ktor-client-cio:${KTOR_VERSION}"

kotlin {
    android()
    jvm("desktop")
    js(IR) {
        browser()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(ktorCore)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(ktorCIO)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(ktorCIO)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm("colors", "=1.4.0"))//temp vulnerability fix
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
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

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}
