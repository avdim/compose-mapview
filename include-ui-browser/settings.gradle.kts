pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        val kotlinVersion = extra["kotlin.version"] as String
        val agpVersion = extra["agp.version"] as String
        val composeVersion = extra["compose.version"] as String

        kotlin("multiplatform").version(kotlinVersion)
        id("org.jetbrains.compose").version(composeVersion)
    }
}

//Workaround for task ":include-mapview:jsTestPackageJson"
includeBuild("../include-model") {
    dependencySubstitution {
        substitute(module("com.map:model")).using(project(":"))
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
