plugins {
    id("io.papermc.paperweight.patcher")
}

subprojects {
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

paperweight {
    upstreams.paper {
        ref = providers.gradleProperty("paperCommit")

        patchFile {
            path = "paper-server/build.gradle.kts"
            outputFile = file("plazma-server/build.gradle.kts")
            patchFile = file("plazma-server/build.gradle.kts.patch")
        }
        patchFile {
            path = "paper-api/build.gradle.kts"
            outputFile = file("plazma-api/build.gradle.kts")
            patchFile = file("plazma-api/build.gradle.kts.patch")
        }
        patchFile {
            path = "paper-checkstyle/build.gradle.kts"
            outputFile = file("plazma-checkstyle/build.gradle.kts")
            patchFile = file("plazma-checkstyle/build.gradle.kts.patch")
        }

        patchDir("paperApi") {
            upstreamPath = "paper-api"
            outputDir = file("paper-api")
            patchesDir = file("plazma-api/paper-patches")
            excludes = setOf("build.gradle.kts")
        }
        patchDir("paperServer") {
            upstreamPath = "paper-server"
            outputDir = file("paper-server")
            patchesDir = file("plazma-server/paper-patches")
            excludes = setOf("build.gradle.kts", "patches")
        }
        patchDir("paperCheckstyle") {
            upstreamPath = "paper-checkstyle"
            outputDir = file("paper-checkstyle")
            patchesDir = file("plazma-checkstyle/paper-patches")
            excludes = setOf("build.gradle.kts")
        }
        patchDir("paperCheckstyleConfig") {
            upstreamPath = ".checkstyle"
            outputDir = file(".checkstyle")
            patchesDir = file("plazma-checkstyle/paper-config-patches")
        }
    }
}
