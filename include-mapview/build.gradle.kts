import org.jetbrains.compose.compose

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

version = "1.0-SNAPSHOT"
val KTOR_VERSION = "1.6.7"
val ktorCore = "io.ktor:ktor-client-core:$KTOR_VERSION"
val ktorCIO = "io.ktor:ktor-client-cio:$KTOR_VERSION"
val ktorIos = "io.ktor:ktor-client-ios:$KTOR_VERSION"
val ktorOkHttp = "io.ktor:ktor-client-okhttp:$KTOR_VERSION"

kotlin {
    android()
    jvm("desktop")
    js(IR) {
        browser {
            testTask {
                enabled = false
            }
        }
    }
    iosX64("uikitX64") {
        binaries {
            executable() {
                entryPoint = "main"
                freeCompilerArgs += listOf(
                    "-linker-option", "-framework", "-linker-option", "Metal",
                    "-linker-option", "-framework", "-linker-option", "CoreText",
                    "-linker-option", "-framework", "-linker-option", "CoreGraphics"
                )
            }
        }
    }
    iosArm64("uikitArm64") {
        binaries {
            executable() {
                entryPoint = "main"
                freeCompilerArgs += listOf(
                    "-linker-option", "-framework", "-linker-option", "Metal",
                    "-linker-option", "-framework", "-linker-option", "CoreText",
                    "-linker-option", "-framework", "-linker-option", "CoreGraphics"
                )
                // TODO: the current compose binary surprises LLVM, so disable checks for now.
                freeCompilerArgs += "-Xdisable-phases=VerifyBitcode"
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.map:config:1.0-SNAPSHOT")
                implementation("com.map:model:1.0-SNAPSHOT")
                implementation("com.map:tile-image:1.0-SNAPSHOT")
                implementation(compose.runtime)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-native-mt")
            }
        }
        val nativeMain by creating {
            dependsOn(commonMain)
        }
        val uikitMain by creating {
            dependsOn(nativeMain)
            dependencies {
                implementation(ktorIos)
            }
        }
        val uikitX64Main by getting {
            dependsOn(uikitMain)
        }
        val uikitArm64Main by getting {
            dependsOn(uikitMain)
        }
        val androidMain by getting {
            dependencies {
                implementation("com.map:io-android-desktop:1.0-SNAPSHOT")
                implementation("com.map:ui-android-desktop:1.0-SNAPSHOT")
                implementation(ktorOkHttp)
                implementation(ktorCIO)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation("com.map:io-android-desktop:1.0-SNAPSHOT")
                implementation("com.map:ui-android-desktop:1.0-SNAPSHOT")
                implementation(ktorCIO)
                implementation(compose.desktop.common)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("com.map:ui-browser:1.0-SNAPSHOT")
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

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    mavenLocal()
}

// a temporary workaround for a bug in jsRun invocation - see https://youtrack.jetbrains.com/issue/KT-48273
afterEvaluate {
    rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
        versions.webpackDevServer.version = "4.0.0"
        versions.webpackCli.version = "4.9.0"
    }
}
