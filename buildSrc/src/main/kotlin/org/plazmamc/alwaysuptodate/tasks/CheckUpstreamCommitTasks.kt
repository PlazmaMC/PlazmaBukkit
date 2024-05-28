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
import java.net.URI.create

abstract class CheckPaperCommitTask : Task() {

    private val property = project.extensions["alwaysUpToDate"] as AlwaysUpToDateExtension

    @TaskAction
    fun check() = println(project.checkCommit(
        project.property(property.paperRepoName.get()).toString(),
        project.property(property.paperBranchName.get()).toString(),
        property.paperCommitName.get()
    ))

}

abstract class CheckPurpurCommitTask : Task() {

    private val property = project.extensions["alwaysUpToDate"] as AlwaysUpToDateExtension

    @TaskAction
    fun check() = println(project.checkCommit(
        project.property(property.purpurRepoName.get()).toString(),
        project.property(property.purpurBranchName.get()).toString(),
        property.purpurCommitName.get()
    ))

}

fun Project.getLatest(repository: String, branch: String) =
    Git(project.pathIO)("ls-remote", repository).readText()
        ?.lines()?.first("[a-z0-9]{40}\trefs/heads/$branch".toRegex()::matches)?.split("\t")?.first()
        ?: throw AlwaysUpToDateException("Failed to get latest commit of $repository")

fun Project.checkCommit(repository: String, branch: String, propertyName: String) =
    project.getLatest(repository, branch) == project.properties[propertyName] as String

fun Project.createCompareComment(repository: String, branch: String, before: String, clear: Boolean = false) {
    val builder = StringBuilder()
    val rawRepo = create(repository).path.substring(1)

    if (clear) builder.append("\n\nUpstream has released updates that appear to apply and compile correctly.")
    else builder.append(project.file("compare.txt").readText())
    builder.append("\n\n[${rawRepo.split("/").last()} Changes]\n")

    gson.fromJson<JsonObject>(create("https://api.github.com/repos/$rawRepo/compare/$before...$branch").toURL().readText())["commits"].asJsonArray.forEach { obj ->
        obj.asJsonObject.let { builder.append("$rawRepo@${it["sha"].asString.subSequence(0, 7)}: ${it["commit"].asJsonObject["message"].asString.split("\n")[0]}\n") }
    }
    project.file("compare.txt").writeText(builder.toString())
}
