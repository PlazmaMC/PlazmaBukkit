import io.papermc.paperweight.util.*
import io.papermc.paperweight.util.constants.PAPERCLIP_CONFIG
import java.net.URI

group = "org.plazmamc.plazma"

plugins {
    java
    `maven-publish`
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.paperweight)
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/") {
        content {
            onlyForConfigurations(PAPERCLIP_CONFIG)
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

    java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

    publishing {
        repositories {
            maven {
                name = "githubPackage"
                url = uri("https://maven.pkg.github.com/PlazmaMC/Plazma")

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
    apply(plugin = "java")
    
    tasks {
        withType<JavaCompile>().configureEach {
            options.compilerArgs.addAll(listOf("--add-modules=jdk.incubator.vector", "-Xmaxwarns", "1"))
            options.encoding = Charsets.UTF_8.name()
            options.release.set(17)
        }
    
        withType<Javadoc> {
            options.encoding = Charsets.UTF_8.name()
        }
    
        withType<ProcessResources> {
            filteringCharset = Charsets.UTF_8.name()
        }
    
        withType<Test> {
            minHeapSize = "2g"
            maxHeapSize = "2g"
        }
    }

    repositories {
        mavenCentral()
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://ci.emc.gs/nexus/content/groups/aikar/")
        maven("https://repo.aikar.co/content/groups/aikar")
        maven("https://repo.md-5.net/content/repositories/releases/")
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")
        maven("https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }

}

paperweight {
    serverProject.set(project(":plazma-server"))

    remapRepo.set("https://maven.fabricmc.net/")
    decompileRepo.set("https://files.minecraftforge.net/maven/")

    usePaperUpstream(providers.gradleProperty("paperCommit")) {
        withPaperPatcher {
            apiPatchDir.set(layout.projectDirectory.dir("patches/api"))
            apiOutputDir.set(layout.projectDirectory.dir("Plazma-API"))

            serverPatchDir.set(layout.projectDirectory.dir("patches/server"))
            serverOutputDir.set(layout.projectDirectory.dir("Plazma-Server"))
        }
    }
}

tasks {
    generateDevelopmentBundle {
        apiCoordinates.set("org.plazmamc.plazma:plazma-api")
        mojangApiCoordinates.set("io.papermc.paper:paper-mojangapi")
        libraryRepositories.addAll(
                "https://repo.maven.apache.org/maven2/",
                "https://papermc.io/repo/repository/maven-public/"
        )
    }

    register("updateUpstream") {
        val tempDir = layout.cacheDir("updater")
        val file = "gradle.properties"
        val latestCommit = gson.fromJson<paper.libs.com.google.gson.JsonObject>(URI.create("https://api.github.com/repos/PaperMC/Paper/commits/master").toURL().readText())["sha"].asString

        outputs.upToDateWhen {
            val paperCommit: String by project
            paperCommit == latestCommit
        }

        doFirst {
            copy {
                from(file)
                into(tempDir)
                filter { line: String ->
                    line.replace("paperCommit = .*".toRegex(), "paperCommit = $latestCommit")
                }
            }
        }

        doLast {
            copy {
                from(tempDir.file("gradle.properties"))
                into(project.file(file).parent)
            }
        }
        finalizedBy(task("applyAndRebuildPatches"))
    }

    register("applyAndRebuildPatches") {
        dependsOn(applyPatches)
        finalizedBy(rebuildPatches)
    }

    register("getPaperCommit") {
        val paperCommit: String by project
        println(paperCommit)
    }

    register("getPufferfishCommit") {
        val paperCommit: String by project
        println(paperCommit)
    }

    register("getPurpurCommit") {
        val paperCommit: String by project
        println(paperCommit)
    }
}
