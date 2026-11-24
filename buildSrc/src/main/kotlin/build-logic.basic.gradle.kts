plugins {
    `java-library`
    `maven-publish`
    idea
    id("io.papermc.paperweight.paper-checkstyle")
}

configurations.register("mockitoAgent")

// Compile tests with -parameters for better junit parameterized test names
tasks.compileTestJava {
    options.compilerArgs.add("-parameters")
}

val scanJarForBadCalls = tasks.register<io.papermc.paperweight.tasks.ScanJarForBadCalls>("scanJarForBadCalls") {
    badAnnotations.add("Lio/papermc/paper/annotation/DoNotUse;")
    jarToScan.set(tasks.jar.flatMap { it.archiveFile })
    classpath.from(configurations.compileClasspath)
}
tasks.check {
    dependsOn(scanJarForBadCalls)
}
