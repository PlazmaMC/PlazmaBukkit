package org.plazmamc.alwaysuptodate.tasks

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.plazmamc.alwaysuptodate.AlwaysUpToDateException
import org.plazmamc.alwaysuptodate.AlwaysUpToDateExtension
import org.plazmamc.alwaysuptodate.utils.*

abstract class CheckUpstreamCommit : Task() {

    @get:Input
    abstract val repo: Property<String>

    @get:Input
    abstract val ref: Property<String>

    @get:Input
    abstract val commitPropertyName: Property<String>

    @TaskAction
    fun check() = with(project) { println(checkCommitFor { repo to ref to commitPropertyName }) }

}

private fun Project.getLatest(repository: String, branch: String) =
    git("ls-remote", repository).readText()?.lines()
        ?.first("[a-z0-9]{40}\trefs/heads/$branch".toRegex()::matches)?.split("\t")?.first()
        ?: throw AlwaysUpToDateException("Failed to get latest commit of $repository")

fun Project.checkCommitFor(block: AlwaysUpToDateExtension.() -> Pair<Pair<Property<String>, Property<String>>, Property<String>>): Boolean =
    extension.block().flatten().let { getLatest(extension { it.first }, extension { it.second }) == property { it.third } }
