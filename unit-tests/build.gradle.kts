
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
