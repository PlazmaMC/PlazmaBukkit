import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    id("io.papermc.paperweight.patcher") version "2.0.0-beta.17"
}

val paperMavenPublicUrl = "https://repo.papermc.io/repository/maven-public/"

//val brandName: String by project
//val providerRepo: String by project

paperweight {
    upstreams.register("purpur") {
        repo = github("PurpurMC", "Purpur")
        ref = providers.gradleProperty("purpurCommit")

        patchFile {
            path = "purpur-server/build.gradle.kts"
            outputFile = file("plazma-server/build.gradle.kts")
            patchFile = file("plazma-server/build.gradle.kts.patch")
        }
        patchFile {
            path = "purpur-api/build.gradle.kts"
            outputFile = file("plazma-api/build.gradle.kts")
            patchFile = file("plazma-api/build.gradle.kts.patch")
        }
        patchRepo("paperApi") {
            upstreamPath = "paper-api"
            patchesDir = file("plazma-api/paper-patches")
            outputDir = file("paper-api")
        }
        patchDir("purpurApi") {
            upstreamPath = "purpur-api"
            excludes = listOf("build.gradle.kts", "build.gradle.kts.patch", "paper-patches")
            patchesDir = file("plazma-api/purpur-patches")
            outputDir = file("purpur-api")
        }
    }
}

//val mavenUsername: String? by project
//val mavenPassword: String? by project

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    repositories {
        mavenCentral()
        maven(paperMavenPublicUrl)
    }

    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
        options.isFork = true
        options.compilerArgs.addAll(listOf("-Xlint:-deprecation", "-Xlint:-removal"))
        options.forkOptions.memoryMaximumSize = "4g"
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
            exceptionFormat = TestExceptionFormat.FULL
            events(TestLogEvent.STANDARD_OUT)
        }
    }

//    extensions.configure<PublishingExtension> {
//        repositories {
//            maven("https://maven.pkg.github.com/$providerRepo") {
//                name = "github"
//                credentials {
//                    username = mavenUsername ?: System.getenv("GRADLE_PROPERTY_MAVEN_USERNAME")
//                            ?: System.getenv("MAVEN_USERNAME")
//                    password = mavenPassword ?: System.getenv("GRADLE_PROPERTY_MAVEN_PASSWORD")
//                            ?: System.getenv("MAVEN_PASSWORD")
//                }
//            }
//            maven("https://repo.codemc.io/repository/maven-snapshots/") {
//                name = "codemc"
//                credentials {
//                    username = mavenUsername ?: System.getenv("GRADLE_PROPERTY_MAVEN_USERNAME")
//                            ?: System.getenv("MAVEN_USERNAME")
//                    password = mavenPassword ?: System.getenv("GRADLE_PROPERTY_MAVEN_PASSWORD")
//                            ?: System.getenv("MAVEN_PASSWORD")
//                }
//            }
//        }
//    }
}

tasks.register("printMinecraftVersion") {
    doLast {
        println(providers.gradleProperty("mcVersion").get().trim())
    }
}

tasks.register("printPlazmaVersion") {
    doLast {
        println(project.version)
    }
}
