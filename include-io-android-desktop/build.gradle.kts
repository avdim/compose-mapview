plugins {
    kotlin("jvm")
}

version = "1.0-SNAPSHOT"

val KTOR_VERSION = "1.6.8"
//val KTOR_VERSION = "1.6.2-native-mm-eap-196"
val ktorCore = "io.ktor:ktor-client-core:$KTOR_VERSION"

dependencies {
    implementation("com.map:config:1.0-SNAPSHOT")
    implementation("com.map:model:1.0-SNAPSHOT")
    implementation(ktorCore)
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    mavenLocal()
    maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
}
