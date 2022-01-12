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
//    js(IR) {
//        browser()
//        binaries.executable()
//    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("com.map:model:1.0")
            }
        }
        val commonModel by creating {
            dependencies {
                api(Deps.coroutinesCore)
                api(Deps.ktorCore)
                api(compose.runtime)
            }
        }
        val shareAndroidDesktop by creating {
            dependsOn(commonMain)
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
            }
        }
        val androidMain by getting {
            dependsOn(commonModel)
            dependsOn(shareAndroidDesktop)
            dependencies {
                api("androidx.appcompat:appcompat:1.3.1")
                api("androidx.core:core-ktx:1.3.1")
                implementation(Deps.ktorCIO)
            }
        }
        val desktopMain by getting {
            dependsOn(commonModel)
            dependsOn(shareAndroidDesktop)
            dependencies {
                api(compose.desktop.common)
                implementation(Deps.ktorCIO)
            }
        }
//        val jsMain by getting {
//            dependsOn(commonModel)
//            dependencies {
//                implementation(compose.web.core)
//                implementation(compose.runtime)
//            }
//        }
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

//// a temporary workaround for a bug in jsRun invocation - see https://youtrack.jetbrains.com/issue/KT-48273
//afterEvaluate {
//    rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
//        versions.webpackDevServer.version = "4.0.0"
//        versions.webpackCli.version = "4.9.0"
//    }
//}
