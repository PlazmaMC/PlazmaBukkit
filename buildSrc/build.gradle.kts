plugins {
    java
    `kotlin-dsl`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

kotlin.jvmToolchain {
    languageVersion = JavaLanguageVersion.of(17)
}

dependencies {
    shadow("io.papermc.paperweight:paperweight-patcher:1.5.11-SNAPSHOT")
}

tasks {
    jar {
        finalizedBy(shadowJar)
    }

    shadowJar {
        archiveFileName.set("buildSrc.jar")
        configurations = listOf(project.configurations["shadow"])

        exclude("META-INF/gradle-plugins/io.papermc.paperweight.patcher.properties")
        relocate("io.papermc.paperweight", "org.plazmamc.alwaysuptodate.internal.paperweight")
        minimize()
    }
}
