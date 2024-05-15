import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import kotlin.io.path.createSymbolicLinkPointingTo

plugins {
    java
    `maven-publish`
    `kotlin-dsl`
    `always-up-to-date`
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.paperweight)
}

val jdkVersion = property("jdkVersion").toString().toInt()
val projectName = property("projectName").toString()
val projectRepo = property("projectRepo").toString()

kotlin.jvmToolchain(jdkVersion)

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/") {
        content { onlyForConfigurations(configurations.paperclip.name) }
    }
}

dependencies {
    remapper(libs.remapper)
    decompiler(libs.decompiler)
    paperclip(libs.paperclip)
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    java.toolchain.languageVersion.set(JavaLanguageVersion.of(jdkVersion))

    publishing {
        repositories {
            maven {
                name = "githubPackage"
                url = uri("https://maven.pkg.github.com/$projectRepo")

                credentials {
                    username = System.getenv("GITHUB_USERNAME")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }

            publications.register<MavenPublication>("gpr") {
                from(components["java"])
            }
        }
    }
}

subprojects {
    tasks {
        withType<JavaCompile>().configureEach {
            options.encoding = Charsets.UTF_8.name()
            options.release = jdkVersion
            options.compilerArgs.addAll(listOf(
                "--add-modules=jdk.incubator.vector",
                "-Xmaxwarns", "1"
            ))
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

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

paperweight {
    serverProject = project(":${projectName.lowercase()}-server")

    remapRepo = "https://repo.papermc.io/repository/maven-public/"
    decompileRepo = "https://repo.papermc.io/repository/maven-public/"

    usePaperUpstream(providers.gradleProperty("paperCommit")) {
        withPaperPatcher {
            apiPatchDir.set(projectDir.resolve("patches/api"))
            apiOutputDir.set(projectDir.resolve("$projectName-API"))

            serverPatchDir.set(projectDir.resolve("patches/server"))
            serverOutputDir.set(projectDir.resolve("$projectName-Server"))
        }

        patchTasks.register("generatedApi") {
            isBareDirectory = true
            upstreamDirPath = "paper-api-generator/generated"
            patchDir = projectDir.resolve("patches/generated-api")
            outputDir = projectDir.resolve("paper-api-generator/generated")
        }
    }
}

alwaysUpToDate {

    paperRepoName.set("paperRepo")
    paperBranchName.set("paperBranch")
    paperCommitName.set("paperCommit")

    purpurRepoName.set("purpurRepo")
    purpurBranchName.set("purpurBranch")
    purpurCommitName.set("purpurCommit")

    pufferfishRepoName.set("pufferfishRepo")
    pufferfishBranchName.set("pufferfishBranch")
    pufferfishToggleName.set("usePufferfish")

}

tasks {
    applyPatches {
        dependsOn("applyGeneratedApiPatches")
    }

    rebuildPatches {
        dependsOn("rebuildGeneratedApiPatches")
    }

    generateDevelopmentBundle {
        apiCoordinates.set("${project.group}:${projectName.lowercase()}-api")
        libraryRepositories.addAll(
                "https://repo.maven.apache.org/maven2/",
                "https://maven.pkg.github.com/$projectRepo",
                "https://papermc.io/repo/repository/maven-public/"
        )
    }

    clean {
        doLast {
            listOf(
                ".gradle/caches",
                "$projectName-API",
                "$projectName-Server",
                "paper-api-generator",
                "run",

                // remove dev environment files
                "0001-fixup.patch",
                "compare.txt"
            ).forEach {
                projectDir.resolve(it).deleteRecursively()
            }
        }
    }
}

publishing {
    publications.create<MavenPublication>("devBundle") {
        artifact(tasks.generateDevelopmentBundle) {  artifactId = "dev-bundle" }
    }
}
