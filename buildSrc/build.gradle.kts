plugins {
    java
    `kotlin-dsl`
    id("com.gradleup.shadow") version "8.3.5"
}

kotlin.jvmToolchain(21)

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    shadow("io.papermc.paperweight:paperweight-patcher:1.6.2-SNAPSHOT")
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
