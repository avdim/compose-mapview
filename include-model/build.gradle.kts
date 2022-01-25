import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
//    id("com.android.library")
    kotlin("multiplatform")
}

version = "1.0-SNAPSHOT"

kotlin {
//    android()
    jvm("desktop")
    js(IR) {
        browser()
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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
            }
        }
        val uikitMain by creating {
            dependsOn(commonMain)
        }
        val uikitX64Main by getting {
            dependsOn(uikitMain)
        }
        val uikitArm64Main by getting {
            dependsOn(uikitMain)
        }
        val jsMain by getting {
            dependencies {
                implementation(npm("colors", "=1.4.0"))//temp vulnerability fix, use strict version 1.4.0
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

//android {
//    compileSdk = 31
//
//    defaultConfig {
//        minSdk = 21
//        targetSdk = 31
//    }
//
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_1_8
//        targetCompatibility = JavaVersion.VERSION_1_8
//    }
//
//    sourceSets {
//        named("main") {
//            manifest.srcFile("src/androidMain/AndroidManifest.xml")
//            res.srcDirs("src/androidMain/res")
//        }
//    }
//}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    mavenLocal()
}
kotlin.targets.withType(KotlinNativeTarget::class.java) {
    binaries.all {
        binaryOptions["memoryModel"] = "experimental"
    }
}
