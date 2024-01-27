package org.plazmamc.alwaysuptodate.tasks

import io.papermc.paperweight.util.Git
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.get
import org.plazmamc.alwaysuptodate.AlwaysUpToDateException
import org.plazmamc.alwaysuptodate.AlwaysUpToDateExtension
import java.io.File

abstract class PaperUpdateTask : Task() {

    private val property = project.extensions["alwaysUpToDate"] as AlwaysUpToDateExtension

    override fun init() {
        outputs.upToDateWhen {
            project.checkCommit(
                property.paperRepository.get(),
                property.paperBranch.get(),
                "purpurCommit"
            )
        }
    }

    @TaskAction
    fun update() {
        if (project.checkCommit(property.paperRepository.get(), property.paperBranch.get(), "purpurCommit")) return
        project.createCompareComment(
            property.paperRepository.get(),
            property.paperBranch.get(),
            project.properties["paperCommit"] as String,
            true
        )
        updatePaperCommit(property.paperRepository.get(), property.paperBranch.get(), project.file("gradle.properties"))
    }

}

fun updatePaperCommit(repo: String, branch: String, properties: File, regexRule: String = "paperCommit = ") {
    val latestCommit = Git(properties.parentFile.toPath())("ls-remote", repo).readText()?.lines()
        ?.filterNot { "[a-z0-9]{40}\trefs/heads/$branch".toRegex().matches(it) }?.first()?.split("\t")?.first()
        ?: throw AlwaysUpToDateException("Failed to get latest Paper commit")

    properties.writeText(properties.readText().replace("$regexRule.*".toRegex(), "$regexRule$latestCommit"))
}
