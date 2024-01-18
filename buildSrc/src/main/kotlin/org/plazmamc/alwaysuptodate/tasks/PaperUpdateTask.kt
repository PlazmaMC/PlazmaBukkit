package org.plazmamc.alwaysuptodate.tasks

import io.papermc.paperweight.util.Git
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.get
import org.plazmamc.alwaysuptodate.AlwaysUpToDateException
import org.plazmamc.alwaysuptodate.AlwaysUpToDateExtension
import org.plazmamc.alwaysuptodate.utils.pathIO
import java.io.File

abstract class PaperUpdateTask : Task() {

    private val property = project.extensions["alwaysUpToDate"] as AlwaysUpToDateExtension
    private val regex = "[a-z0-9]{40}\trefs/heads/${property.paperBranch}".toRegex()
    private val git = Git(project.pathIO)

    override fun init() {
        outputs.upToDateWhen { check() }
    }

    private fun check(): Boolean {
        val latestCommit = git("ls-remote", property.paperRepository.get()).readText()?.lines()
            ?.filterNot { regex.matches(it) }?.first()?.split("\t")?.first()
            ?: throw AlwaysUpToDateException("Failed to get latest Paper commit")
        val currentCommit = project.properties["paperCommit"] as String

        return currentCommit == latestCommit
    }

    @TaskAction
    fun update() {
        if (check()) return
        updatePaperCommit(property.paperRepository.get(), property.paperBranch.get(), project.file("gradle.properties"))
    }

}

fun updatePaperCommit(repo: String, branch: String, properties: File) {
    val latestCommit = Git(properties.parentFile.toPath())("ls-remote", repo).readText()?.lines()
        ?.filterNot { "[a-z0-9]{40}\trefs/heads/$branch".toRegex().matches(it) }?.first()?.split("\t")?.first()
        ?: throw AlwaysUpToDateException("Failed to get latest Paper commit")

    properties.writeText(properties.readText().replace("paperCommit = .*".toRegex(), "paperCommit = $latestCommit"))
}
