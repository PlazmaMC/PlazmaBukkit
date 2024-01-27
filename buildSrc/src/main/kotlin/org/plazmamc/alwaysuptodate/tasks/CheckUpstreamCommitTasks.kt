package org.plazmamc.alwaysuptodate.tasks

import io.papermc.paperweight.util.Git
import io.papermc.paperweight.util.fromJson
import io.papermc.paperweight.util.gson
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.get
import org.plazmamc.alwaysuptodate.AlwaysUpToDateException
import org.plazmamc.alwaysuptodate.AlwaysUpToDateExtension
import org.plazmamc.alwaysuptodate.utils.pathIO
import paper.libs.com.google.gson.JsonObject
import java.net.URI

abstract class CheckPaperCommitTask : Task() {

    private val property = project.extensions["alwaysUpToDate"] as AlwaysUpToDateExtension

    @TaskAction
    fun check() {
        println(project.checkCommit(property.paperRepository.get(), property.paperBranch.get(), "paperCommit"))
    }

}

abstract class CheckPurpurCommitTask : Task() {

    private val property = project.extensions["alwaysUpToDate"] as AlwaysUpToDateExtension

    @TaskAction
    fun check() {
        println(project.checkCommit(property.purpurRepository.get(), property.purpurBranch.get(), "purpurCommit"))
    }

}

fun Project.getLatest(repository: String, branch: String) : String {
    val regex = "[a-z0-9]{40}\trefs/heads/$branch".toRegex()

    return Git(project.pathIO)("ls-remote", repository).readText()?.lines()
        ?.filterNot { regex.matches(it) }?.first()?.split("\t")?.first()
        ?: throw AlwaysUpToDateException("Failed to get latest Purpur commit")
}

fun Project.checkCommit(repository: String, branch: String, propertyName: String) : Boolean {
    val latestCommit = project.getLatest(repository, branch)
    val currentCommit = project.properties[propertyName] as String

    return currentCommit == latestCommit
}

fun Project.createCompareComment(repository: String, branch: String, before: String, clear: Boolean = false) {
    val builder = StringBuilder()
    val rawRepo = URI.create(repository).path.substring(1)

    if (!clear) builder.append(project.file("compare.txt").readText())
    builder.append("\n\n[${rawRepo.split("/").last()} Changes]")

    gson.fromJson<JsonObject>(URI.create("https://api.github.com/repos/$rawRepo/compare/$before...$branch").toURL().readText())["commits"].asJsonArray.forEach {
        val commit = it.asJsonObject
        builder.append("$rawRepo@${commit["sha"].asString.subSequence(0, 7)}: ${commit["commit"].asJsonObject["message"].asString.split("\n")[0]}\n")
    }
    project.file("compare.txt").writeText(builder.toString())
}
