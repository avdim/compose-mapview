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

include(":android")
include(":desktop")
include(":browser")
includeBuild("include-model") {
    dependencySubstitution {
        substitute(module("com.map:model")).using(project(":"))
    }
}
includeBuild("include-ui-android-desktop") {
    dependencySubstitution {
        substitute(module("com.map:ui-android-desktop")).using(project(":"))
    }
}

rootProject.name = "map-view"
