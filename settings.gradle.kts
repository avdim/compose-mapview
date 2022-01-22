pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
    }
    plugins {
        val kotlinVersion = extra["kotlin.version"] as String
        val agpVersion = extra["agp.version"] as String
        val composeVersion = extra["compose.version"] as String
        kotlin("jvm").version(kotlinVersion)
        kotlin("multiplatform").version(kotlinVersion)
        kotlin("android").version(kotlinVersion)
        id("com.android.application").version(agpVersion)
        id("com.android.library").version(agpVersion)
        id("org.jetbrains.compose").version(composeVersion)
    }
}

rootProject.name = "compose-mapview"
include(":shared")
include(":sample-ios")
include(":sample-android")
include(":sample-desktop")
include(":sample-browser")
include(":unit-tests")


includeBuild("include-config") {
    dependencySubstitution {
        substitute(module("com.map:config")).using(project(":"))
    }
}
includeBuild("include-tile-image") {
    dependencySubstitution {
        substitute(module("com.map:tile-image")).using(project(":"))
    }
}
includeBuild("include-model") {
    dependencySubstitution {
        substitute(module("com.map:model")).using(project(":"))
    }
}
includeBuild("include-io-android-desktop") {
    dependencySubstitution {
        substitute(module("com.map:io-android-desktop")).using(project(":"))
    }
}
includeBuild("include-ui-android-desktop") {
    dependencySubstitution {
        substitute(module("com.map:ui-android-desktop")).using(project(":"))
    }
}
includeBuild("include-ui-browser") {
    dependencySubstitution {
        substitute(module("com.map:ui-browser")).using(project(":"))
    }
}
includeBuild("include-mapview") {
    dependencySubstitution {
        substitute(module("com.map:mapview")).using(project(":"))
    }
}
includeBuild("include-secret") {
    dependencySubstitution {
        substitute(module("com.map:secret")).using(project(":"))
    }
}
