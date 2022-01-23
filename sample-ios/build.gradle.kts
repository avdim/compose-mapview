plugins {
    id("org.jetbrains.gradle.apple.applePlugin") version "212.4638.14-0.14"
}

apple {
    iosApp {
        productName = "mapviewios"

        sceneDelegateClass = "SceneDelegate"
        launchStoryboard = "LaunchScreen"

        //productInfo["NSAppTransportSecurity"] = mapOf("NSAllowsArbitraryLoads" to true)
        //buildSettings.OTHER_LDFLAGS("")

        dependencies {
            implementation("com.map:config:1.0-SNAPSHOT")
            implementation("com.map:model:1.0-SNAPSHOT")
            implementation("com.map:tile-image:1.0-SNAPSHOT")
            implementation(project(":ioshelper"))
        }
    }
}
