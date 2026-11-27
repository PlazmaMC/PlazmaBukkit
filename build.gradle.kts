plugins {
    id("io.papermc.paperweight.patcher")
}

subprojects {
    apply {
        plugin("java")
        plugin("io.papermc.paperweight.paper-checkstyle")
    }

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    dependencies {
        // "checkstyle"(project(":plazma-checkstyle"))
    }

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion = JavaLanguageVersion.of(25)
    }

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release = 25
        options.isFork = true
        options.compilerArgs.addAll(listOf(
            "-Xlint:-deprecation",
            "-Xlint:-removal",
            "--add-modules=jdk.incubator.vector"
        ))
    }
    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }
    tasks.withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }
    tasks.withType<Test> {
        testLogging {
            showStackTraces = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            events(org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT)
        }
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
