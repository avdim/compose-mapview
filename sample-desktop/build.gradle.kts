import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform") // kotlin("jvm") doesn't work well in IDEA/AndroidStudio (https://github.com/JetBrains/compose-jb/issues/22)
    id("org.jetbrains.compose")
}

kotlin {
    jvm {
        withJava()
    }
    sourceSets {
        named("jvmMain") {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("com.map:mapview:1.0-SNAPSHOT")
                implementation("com.map:secret:1.0-SNAPSHOT")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.map.MainKt"
    }
}
