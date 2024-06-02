package org.plazmamc.alwaysuptodate.tasks

import io.papermc.paperweight.util.Git
import io.papermc.paperweight.util.path
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.plazmamc.alwaysuptodate.utils.addCommit
import java.io.File

abstract class GenerateMergedAPIPatch : Task() {

    @get:InputDirectory
    abstract val inputDir: DirectoryProperty

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

        inputDir.path.toFile().copyRecursively(dotGit, overwrite = true)
        Git(this).addCommit("${commitTitle.get()}\n\n${license.get()}", "--author=${author.get()}")
    }

}
