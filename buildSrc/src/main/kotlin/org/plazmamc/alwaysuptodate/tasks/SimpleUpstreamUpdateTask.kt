package org.plazmamc.alwaysuptodate.tasks

import io.papermc.paperweight.util.Git
import io.papermc.paperweight.util.path
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.plazmamc.alwaysuptodate.AlwaysUpToDateException
import org.plazmamc.alwaysuptodate.utils.dependsOn
import org.plazmamc.alwaysuptodate.utils.extension

abstract class SimpleUpstreamUpdateTask : Task() {

    @get:Input
    abstract val repo: Property<String>

    @get:Input
    abstract val ref: Property<String>

    @get:InputDirectory
    abstract val workDir: DirectoryProperty

    @get:Input
    abstract val regex: Property<String>

    override fun init(): Unit = with(project) {
        dependsOn<CreateCompareComment>("createCompareComment", "Create Paper Compare Comment") {
            onlyIf { !this@SimpleUpstreamUpdateTask.state.upToDate }
            clear.convention(false)
            repo.convention(extension.paperRepo)
            ref.convention(extension.paperRef)
            commitPropertyName.convention(extension.paperCommitName)
        }
    }

    @TaskAction
    fun update() = (Git(workDir.path)("ls-remote", repo.get()).readText()?.lines()
        ?.filterNot("[a-z0-9]{40}\trefs/heads/${ref.get()}".toRegex()::matches)?.first()?.split("\t")?.first()
        ?: throw AlwaysUpToDateException("Failed to get latest commit")).let { commit ->
        workDir.file("gradle.properties").path.toFile().let {
            it.writeText(it.readText().replace("${regex.get()}.*".toRegex(), "${regex.get()}$commit"))
        }
    }

}
