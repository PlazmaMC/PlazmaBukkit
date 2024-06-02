package org.plazmamc.alwaysuptodate

import io.papermc.paperweight.util.Git
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.plazmamc.alwaysuptodate.utils.*
import org.plazmamc.alwaysuptodate.tasks.CheckUpstreamCommit
import org.plazmamc.alwaysuptodate.tasks.SimpleUpstreamUpdateTask
import org.plazmamc.alwaysuptodate.tasks.CreateCompareComment
import org.plazmamc.alwaysuptodate.tasks.PurpurUpdateTask

class AlwaysUpToDate : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {

        Git.checkForGit()

        val extension = extensions.create("alwaysUpToDate", AlwaysUpToDateExtension::class.java)

        val git = configureTask<CheckGitTask>("checkGitStatus", "Verify that Git is available")

        fun generateTasks(
            upstream: String,
            provider: AlwaysUpToDateExtension.() -> Pair<Pair<Property<String>, Property<String>>, Property<String>>
        ) {
            registerTask<CheckUpstreamCommit>("check$upstream", "Check if the $upstream commit is up to date") {
                dependsOn(git)
                val (repo, ref, commitProperty) = extension.provider().flatten()
                this.repo.set(repo)
                this.ref.set(ref)
                this.commitPropertyName.set(commitProperty)
            }

            registerTask<CreateCompareComment>("compare$upstream", "Create a comment comparing the $upstream commit") {
                dependsOn(git)
                val (repo, ref, commitProperty) = extension.provider().flatten()
                this.repo.set(repo)
                this.ref.set(ref)
                this.commitPropertyName.set(commitProperty)
                this.clear.set(false)
            }

            registerTask<CreateCompareComment>("cleanCompare$upstream", "Create a comment comparing the $upstream commit") {
                dependsOn(git)
                val (repo, ref, commitProperty) = extension.provider().flatten()
                this.repo.set(repo)
                this.ref.set(ref)
                this.commitPropertyName.set(commitProperty)
                this.clear.set(true)
            }
        }

        generateTasks("Paper") { paperRepo to paperRef to paperCommitName }
        generateTasks("Purpur") { purpurRepo to purpurRef to purpurCommitName }

        registerTask<SimpleUpstreamUpdateTask>("updateUpstream", "Update the upstream commit") {
            dependsOn(git)
            repo.convention(extension.paperRepo)
            ref.convention(extension.paperRef)
            commitPropertyName.convention(extension.paperCommitName)
            workDir.set(layout.projectDirectory)
            regex.convention("paperCommit = ")
        }

        registerTask<PurpurUpdateTask>("updateImplementation", "Update the implementation commit") {
            dependsOn(git)
            workDir.set(layout.projectDirectory)
        }

    }

}
