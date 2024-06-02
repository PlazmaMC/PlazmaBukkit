package org.plazmamc.alwaysuptodate

import io.papermc.paperweight.util.Git
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskProvider
import org.plazmamc.alwaysuptodate.tasks.*
import org.plazmamc.alwaysuptodate.utils.CheckGitTask
import org.plazmamc.alwaysuptodate.utils.configureTask
import org.plazmamc.alwaysuptodate.utils.flatten
import org.plazmamc.alwaysuptodate.utils.registerTask

class AlwaysUpToDate : Plugin<Project> {

    @Suppress("UNUSED_VARIABLE")
    override fun apply(target: Project): Unit = with(target) {

        Git.checkForGit()

        val extension = extensions.create("alwaysUpToDate", AlwaysUpToDateExtension::class.java)

        val git = configureTask<CheckGitTask>("checkGitStatus", "Verify that Git is available")

        fun generateTasks(
            upstream: String,
            provider: AlwaysUpToDateExtension.() -> Pair<Pair<Property<String>, Property<String>>, Property<String>>
        ): Triple<TaskProvider<CheckUpstreamCommit>, TaskProvider<CreateCompareComment>, TaskProvider<CreateCompareComment>> {
            val i = registerTask<CheckUpstreamCommit>("check$upstream", "Check if the $upstream commit is up to date") {
                dependsOn(git)
                val (repo, ref, commitProperty) = extension.provider().flatten()
                this.repo.set(repo)
                this.ref.set(ref)
                this.commitPropertyName.set(commitProperty)
            }

            val j = registerTask<CreateCompareComment>("compare$upstream", "Create a comment comparing the $upstream commit") {
                dependsOn(git)
                val (repo, ref, commitProperty) = extension.provider().flatten()
                this.repo.set(repo)
                this.ref.set(ref)
                this.commitPropertyName.set(commitProperty)
                this.clear.set(false)
            }

            val k = registerTask<CreateCompareComment>(
                "cleanCompare$upstream",
                "Create a comment comparing the $upstream commit"
            ) {
                dependsOn(git)
                val (repo, ref, commitProperty) = extension.provider().flatten()
                this.repo.set(repo)
                this.ref.set(ref)
                this.commitPropertyName.set(commitProperty)
                this.clear.set(true)
            }

            return (i to j to k).flatten()
        }

        val (checkPaper, comparePaper, cleanComparePaper) = generateTasks("Paper") { paperRepo to paperRef to paperCommitName }
        val (checkPurpur, comparePurpur, cleanComparePurpur) = generateTasks("Purpur") { purpurRepo to purpurRef to purpurCommitName }

        registerTask<SimpleUpstreamUpdateTask>("updateUpstream", "Update the upstream commit") {
            dependsOn(git)
            repo.convention(extension.paperRepo)
            ref.convention(extension.paperRef)
            workDir.set(layout.projectDirectory)
            regex.convention("paperCommit = ")
        }

        registerTask<PurpurUpdateTask>("updateImplementation", "Update the implementation") {
            dependsOn(git)
            workDir.set(layout.projectDirectory)
        }

    }

}
