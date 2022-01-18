
plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm {
        withJava()
    }
    sourceSets {
        named("commonTest") {
            dependencies {
                implementation("com.map:model:1.0")
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
            }
        }
        named("jvmMain") {
            dependencies {

            }
        }
        named("jvmTest") {
            dependencies {

            }
        }
    }
}
