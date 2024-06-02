package org.plazmamc.alwaysuptodate.tasks

import io.papermc.paperweight.util.Git
import io.papermc.paperweight.util.path
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.plazmamc.alwaysuptodate.utils.addCommit
import java.io.File
import java.nio.file.Path

abstract class GenerateMergedServerPatch : Task() {

    @get:Internal
    abstract val workDir: DirectoryProperty

    @get:Input
    abstract val commitTitle: Property<String>

    @get:Input
    abstract val author: Property<String>

    @get:Input
    abstract val license: Property<String>

    @TaskAction
    fun generate() = with(workDir.path) {
        val dotGit = resolve(".git").toFile().also(java.io.File::deleteRecursively)

        copySource(this)

        val paper = resolve("../.gradle/caches/paperweight/upstreams/paper/Paper-Server")
        copySource(paper)

        Git(paper).addCommit("Vanilla Sources", "--author=Automated <auto@mated.null>")

        paper.resolve(".git").toFile().copyRecursively(dotGit, overwrite = true)
        Git(this).addCommit("${commitTitle.get()}\n\n${license.get()}", "--author=${author.get()}")
    }

}

internal fun copySource(dir: Path) = with(dir.resolve(".gradle/caches/paperweight/mc-dev-sources")) {
    val target = dir.resolve("src/main")
    resolve("net").toFile().copyRecursively(target.resolve("java/net").toFile(), overwrite = true)
    resolve("com").toFile().copyRecursively(target.resolve("java/com").toFile(), overwrite = true)
    resolve("data").toFile().copyRecursively(target.resolve("resources/data").toFile(), overwrite = true)
    resolve("assets").toFile().copyRecursively(target.resolve("resources/assets").toFile(), overwrite = true)
}
