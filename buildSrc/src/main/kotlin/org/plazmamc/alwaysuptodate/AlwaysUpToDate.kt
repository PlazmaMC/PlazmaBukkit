package org.plazmamc.alwaysuptodate

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.plazmamc.alwaysuptodate.tasks.CheckPaperCommitTask
import org.plazmamc.alwaysuptodate.tasks.CheckPurpurCommitTask
import org.plazmamc.alwaysuptodate.tasks.PaperUpdateTask
import org.plazmamc.alwaysuptodate.tasks.PurpurUpdateTask

class AlwaysUpToDate : Plugin<Project> {

    override fun apply(target: Project) = with(target) {

        extensions.create("alwaysUpToDate", AlwaysUpToDateExtension::class.java)

        arrayOf(
            "updatePaper" to PaperUpdateTask::class.java,
            "updatePurpur" to PurpurUpdateTask::class.java,
            "checkPaperCommit" to CheckPaperCommitTask::class.java,
            "checkPurpurCommit" to CheckPurpurCommitTask::class.java,
        ).forEach { tasks.register(it.first, it.second) }

    }

}
