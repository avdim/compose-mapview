import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

version = "1.0-SNAPSHOT"

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation("com.map:mapview:1.0-SNAPSHOT")
                implementation("com.map:secret:1.0-SNAPSHOT")
                implementation(compose.web.core)
                implementation(compose.runtime)
                implementation(npm("colors", "=1.4.0"))//temp vulnerability fix, use strict version 1.4.0
            }
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
