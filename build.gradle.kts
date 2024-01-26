import io.papermc.paperweight.util.constants.PAPERCLIP_CONFIG

group = "org.plazmamc.plazma"

plugins {
    java
    `maven-publish`
    `kotlin-dsl`
    `always-up-to-date`
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.paperweight)
}

kotlin.jvmToolchain {
    languageVersion = JavaLanguageVersion.of(17)
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
                url = uri("https://maven.pkg.github.com/PlazmaMC/PlazmaBukkit")

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

alwaysUpToDate {
    paperRepository.set("https://github.com/PaperMC/Paper")
    paperBranch.set("master")
    purpurRepository.set("https://github.com/PurpurMC/Purpur")
    purpurBranch.set("ver/1.20.4")
    pufferfishRepository.set("https://github.com/pufferfish-gg/Pufferfish")
    pufferfishBranch.set("ver/1.20")
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
}
