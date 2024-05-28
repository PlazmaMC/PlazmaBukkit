package org.plazmamc.alwaysuptodate.tasks

import io.papermc.paperweight.util.Git
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.get
import org.plazmamc.alwaysuptodate.AlwaysUpToDateException
import org.plazmamc.alwaysuptodate.AlwaysUpToDateExtension
import java.io.File

abstract class PaperUpdateTask : Task() {

    private val property = project.extensions["alwaysUpToDate"] as AlwaysUpToDateExtension

    override fun init() = outputs.upToDateWhen {
        project.checkCommit(
            project.property(property.paperRepoName.get()).toString(),
            project.property(property.paperBranchName.get()).toString(),
            property.paperCommitName.get()
        )
    }

    @TaskAction
    fun update() = with(project) {
        if (
            checkCommit(
                property(property.paperRepoName.get()).toString(),
                property(property.paperBranchName.get()).toString(),
                property.purpurCommitName.get()
            )
        ) return

        createCompareComment(
            property(property.paperRepoName.get()).toString(),
            property(property.paperBranchName.get()).toString(),
            property(property.paperCommitName.get()).toString(),
            true
        )

        updatePaperCommit(
            property(property.paperRepoName.get()).toString(),
            property(property.paperBranchName.get()).toString(),
            file("gradle.properties")
        )
    }

}

fun updatePaperCommit(repo: String, branch: String, properties: File, regexRule: String = "paperCommit = ") =
    (Git(properties.parentFile.toPath())("ls-remote", repo).readText()?.lines()
        ?.filterNot("[a-z0-9]{40}\trefs/heads/$branch".toRegex()::matches)?.first()?.split("\t")?.first()
        ?: throw AlwaysUpToDateException("Failed to get latest Paper commit")).let {
        properties.writeText(properties.readText().replace("$regexRule.*".toRegex(), "$regexRule$it"))
    }
