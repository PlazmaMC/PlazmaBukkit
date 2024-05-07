import java.util.Locale

val projectName = "Plazma"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {}
        create("api") {
            from(files("libs/api.versions.toml"))
        }
        create("server") {
            from(files("libs/server.versions.toml"))
        }
        create("common") {
            from(files("libs/common.versions.toml"))
        }
    }
}

rootProject.name = projectName.lowercase()
for (name in listOf("$projectName-API", "$projectName-Server", "$projectName-MojangAPI")) {
    val projName = name.lowercase(Locale.ENGLISH)
    include(projName)
    findProject(":$projName")!!.projectDir = file(name)
}
