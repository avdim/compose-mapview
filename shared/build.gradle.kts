plugins {
    kotlin("multiplatform")
//    id("com.android.library")
//    id("kotlin-android-extensions")
}

repositories {
    google()
    mavenCentral()
}

kotlin {
//    android()
    ios {
        binaries {
            framework {
                baseName = "shared"
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.map:model:1.0-SNAPSHOT")
                implementation("com.map:tile-image:1.0-SNAPSHOT")
                implementation("com.map:secret:1.0-SNAPSHOT")//todo bad
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
//        val androidMain by getting {
//            dependencies {
//                implementation("com.google.android.material:material:1.2.1")
//            }
//        }
//        val androidTest by getting {
//            dependencies {
//                implementation("junit:junit:4.13")
//            }
//        }
        val iosMain by getting {
            dependencies {
                implementation("com.soywiz.korlibs.korim:korim:2.2.2")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-native-mt")
            }
        }
        val iosTest by getting
    }
}

//android {
//    compileSdkVersion(29)
//    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
//    defaultConfig {
//        minSdkVersion(24)
//        targetSdkVersion(29)
//    }
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_1_8
//        targetCompatibility = JavaVersion.VERSION_1_8
//    }
//}