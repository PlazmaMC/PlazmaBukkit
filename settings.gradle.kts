rootProject.name = "plazma"

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include("plazma-api")
include("plazma-server")
include("plazma-checkstyle")

/*
gradle.lifecycle.beforeProject {
    val mcVersion = providers.gradleProperty("mcVersion").get().trim()
    val releaseChannel = providers.gradleProperty("channel").get().trim()
    val buildNumber = providers.environmentVariable("BUILD_NUMBER").orNull?.trim()?.toInt()

    version = if (buildNumber == null) {
        "$mcVersion.local-SNAPSHOT"
    } else {
        "$mcVersion.build.$buildNumber-${releaseChannel.lowercase()}"
    }
}
 */

// <editor-fold defaultstate="collapsed" desc="fun optionalInclude(...)">
fun optionalInclude(name: String, block: (ProjectDescriptor.() -> Unit)? = null) {
    val settingsFile = file("$name.settings.gradle.kts")
    if (settingsFile.exists()) {
        apply(from = settingsFile)
        findProject(":$name")?.let { block?.invoke(it) }
    } else {
        settingsFile.writeText(
            """
            // Uncomment to enable the '$name' project
            // include(":$name")

            """.trimIndent()
        )
    }
}
// </editor-fold>
