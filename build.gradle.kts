import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    `maven-publish`
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.paperweight)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") { name = "papermc"
        content { onlyForConfigurations(configurations.paperclip.name) }
    }
    maven("https://repo.codemc.io/repository/maven-public/") { name = "codemc" }
    maven("https://jitpack.io") { name = "jitpack" }
}

dependencies {
    remapper(libs.remapper)
    paperclip(libs.paperclip)
    decompiler(libs.decompiler)
}

val brandName: String by project
val providerRepo: String by project
paperweight {
    serverProject = project(":${brandName.lowercase()}-server")

    remapRepo = "https://repo.papermc.io/repository/maven-public/"
    decompileRepo = "https://repo.papermc.io/repository/maven-public/"

    usePaperUpstream(providers.gradleProperty("paperCommit")) {
        withPaperPatcher {
            apiPatchDir.set(projectDir.resolve("patches/api"))
            apiOutputDir.set(projectDir.resolve("$brandName-API"))

            serverPatchDir.set(projectDir.resolve("patches/server"))
            serverOutputDir.set(projectDir.resolve("$brandName-Server"))
        }

        patchTasks.register("generatedApi") {
            isBareDirectory = true
            upstreamDirPath = "paper-api-generator/generated"
            patchDir = projectDir.resolve("patches/generated-api")
            outputDir = projectDir.resolve("paper-api-generator/generated")
        }
    }
}

tasks {
    applyPatches {
        dependsOn("applyGeneratedApiPatches")
    }

    rebuildPatches {
        dependsOn("rebuildGeneratedApiPatches")
    }

    generateDevelopmentBundle {
        apiCoordinates.set("${project.group}:${brandName.lowercase()}-api")
        libraryRepositories.addAll(
            "https://repo1.maven.org/maven2/",
            "https://repo.papermc.io/repository/maven-public/",
            "https://repo.codemc.io/repository/maven-public/",
            "https://jitpack.io",
        )
    }
}

publishing.publications.create<MavenPublication>("devBundle") {
    artifact(tasks.generateDevelopmentBundle) {  artifactId = "dev-bundle" }
}

val mavenUsername: String? by project
val mavenPassword: String? by project
allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

    publishing.repositories.maven("https://maven.pkg.github.com/$providerRepo") {
        name = "github"

        credentials {
            username = mavenUsername ?: System.getenv("GRADLE_PROPERTY_MAVEN_USERNAME") ?: System.getenv("MAVEN_USERNAME")
            password = mavenPassword ?: System.getenv("GRADLE_PROPERTY_MAVEN_PASSWORD") ?: System.getenv("MAVEN_PASSWORD")
        }
    }

    publishing.repositories.maven("https://repo.codemc.io/repository/maven-snapshots/") {
        name = "codemc"

        credentials {
            username = mavenUsername ?: System.getenv("GRADLE_PROPERTY_MAVEN_USERNAME") ?: System.getenv("MAVEN_USERNAME")
            password = mavenPassword ?: System.getenv("GRADLE_PROPERTY_MAVEN_PASSWORD") ?: System.getenv("MAVEN_PASSWORD")
        }
    }
}

subprojects {
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/") { name = "papermc" }
        maven("https://repo.codemc.io/repository/maven-public/") { name = "codeme" }
        maven("https://jitpack.io") { name = "jitpack" }
    }

    tasks {
        withType<JavaCompile>().configureEach {
            options.encoding = Charsets.UTF_8.name()
            options.release = 21
        }

        withType<Javadoc> {
            options.encoding = Charsets.UTF_8.name()
        }

        withType<ProcessResources> {
            filteringCharset = Charsets.UTF_8.name()
        }

        withType<Test> {
            testLogging {
                showStackTraces = true
                exceptionFormat = TestExceptionFormat.FULL
                events(TestLogEvent.STANDARD_OUT)
            }
        }
    }
}
