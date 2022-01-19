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

//Workaround for task ":include-mapview:jsTestPackageJson"
includeBuild("../include-model") {
    dependencySubstitution {
        substitute(module("com.map:model")).using(project(":"))
    }
}
includeBuild("../include-ui-browser") {
    dependencySubstitution {
        substitute(module("com.map:ui-browser")).using(project(":"))
    }
}
includeBuild("../include-config") {
    dependencySubstitution {
        substitute(module("com.map:config")).using(project(":"))
    }
}
includeBuild("../include-tile-image") {
    dependencySubstitution {
        substitute(module("com.map:tile-image")).using(project(":"))
    }
}
