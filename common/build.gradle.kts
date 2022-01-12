import org.jetbrains.compose.compose

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

version = "1.0-SNAPSHOT"

//val KTOR_VERSION = "1.4.1"
val KTOR_VERSION = "1.6.7"

kotlin {
    android()
    jvm("desktop")
    js(IR) {
        browser()
        binaries.executable()

    }
    sourceSets {
        val commonMain by sourceSets.getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
                api("io.ktor:ktor-client-core:${KTOR_VERSION}")
                api(compose.runtime)
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.3.1")
                api("androidx.core:core-ktx:1.3.1")
                implementation("io.ktor:ktor-client-cio:${KTOR_VERSION}")
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.desktop.common)
                implementation("io.ktor:ktor-client-cio:${KTOR_VERSION}")
            }
        }
        val shareAndroidDesktop by creating {
            dependsOn(commonMain)
            androidMain.dependsOn(this)
            desktopMain.dependsOn(this)
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(compose.web.widgets)
                implementation(compose.runtime)
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

// a temporary workaround for a bug in jsRun invocation - see https://youtrack.jetbrains.com/issue/KT-48273
afterEvaluate {
    rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
        versions.webpackDevServer.version = "4.0.0"
        versions.webpackCli.version = "4.9.0"
    }
}
