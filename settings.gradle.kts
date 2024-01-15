import java.util.Locale

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

rootProject.name = "plazma"
for (name in listOf("Plazma-API", "Plazma-Server")) {
    val projName = name.lowercase(Locale.ENGLISH)
    include(projName)
    findProject(":$projName")!!.projectDir = file(name)
}
