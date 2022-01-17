plugins {
    kotlin("jvm")
}

version = "1.0"

val KTOR_VERSION = "1.6.7"
val ktorCore = "io.ktor:ktor-client-core:$KTOR_VERSION"

dependencies {
    implementation("com.map:model:1.0")
    implementation(ktorCore)
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}
