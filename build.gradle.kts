import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

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

kotlin.jvmToolchain {
    languageVersion = JavaLanguageVersion.of(jdkVersion)
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/") {
        content {
            onlyForConfigurations(configurations.paperclip.name)
        }
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
            options.compilerArgs.addAll(listOf("--add-modules=jdk.incubator.vector", "-Xmaxwarns", "1"))
            options.encoding = Charsets.UTF_8.name()
            options.release = jdkVersion
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
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}

paperweight {
    serverProject = project(":${projectName.lowercase()}-server")

    remapRepo = "https://repo.papermc.io/repository/maven-public/"
    decompileRepo = "https://repo.papermc.io/repository/maven-public/"

    usePaperUpstream(providers.gradleProperty("paperCommit")) {
        withPaperPatcher {
            apiPatchDir.set(layout.projectDirectory.dir("patches/api"))
            apiOutputDir.set(layout.projectDirectory.dir("$projectName-API"))

            serverPatchDir.set(layout.projectDirectory.dir("patches/server"))
            serverOutputDir.set(layout.projectDirectory.dir("$projectName-Server"))
        }

        patchTasks.register("generatedApi") {
            isBareDirectory = true
            upstreamDirPath = "paper-api-generator/generated"
            patchDir = layout.projectDirectory.dir("patches/generated-api")
            outputDir = layout.projectDirectory.dir("paper-api-generator/generated")
        }

        patchTasks.register("mojangApi") {
            isBareDirectory = true
            upstreamDirPath = "Paper-MojangAPI"
            patchDir = layout.projectDirectory.dir("patches/mojang-api")
            outputDir = layout.projectDirectory.dir("$projectName-MojangAPI")
        }
    }
}

alwaysUpToDate {

    paperRepoName.set("org.plazmamc.alwaysuptodate.paper.repository")
    paperBranchName.set("paperBranch")
    paperCommitName.set("paperCommit")

    purpurRepoName.set("org.plazmamc.alwaysuptodate.purpur.repository")
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
        apiCoordinates.set("${group}:${projectName.lowercase()}-api")
        mojangApiCoordinates.set("${group}:${projectName.lowercase()}-mojangapi")
        libraryRepositories.addAll(
                "https://repo.maven.apache.org/maven2/",
                "https://maven.pkg.github.com/$projectRepo",
                "https://papermc.io/repo/repository/maven-public/"
        )
    }

    clean {
        doLast {
            projectDir.resolve(".gradle/caches").deleteRecursively()
            listOf("$projectName-API", "$projectName-MojangAPI", "$projectName-Server", "paper-api-generator", "run").forEach {
                projectDir.resolve(it).deleteRecursively()
            }

            // remove dev environment files
            listOf("0001-fixup.patch", "compare.txt").forEach {
                projectDir.resolve(it).delete()
            }
        }
    }
}

publishing {
    publications.create<MavenPublication>("devBundle") {
        artifact(tasks.generateDevelopmentBundle) {
            artifactId = "dev-bundle"
        }
    }
}
