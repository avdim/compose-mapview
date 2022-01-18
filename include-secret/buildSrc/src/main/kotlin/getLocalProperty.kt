import org.gradle.api.Project
import java.io.File
import java.util.*

fun Project.getLocalProperty(key: String, default: String): String {
    fun printError() {
        val message = "ERROR! Please create local.properties with key $key"
        println(message)
        System.err.println(message)
    }

    val propertiesFile: File? = listOf(
        project.file("local.properties"),
        rootProject.file("local.properties"),
        rootProject.file("../local.properties"),
    ).firstOrNull { it.exists() }

    val properties = Properties()
    if (propertiesFile != null) {
        properties.load(propertiesFile.inputStream())
        val value: String? = properties.getProperty(key)
        if (value != null) {
            return value
        } else {
            printError()
            return "error"
        }
    } else {
        printError()
        return default
    }
}
