package org.plazmamc.alwaysuptodate

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.plazmamc.alwaysuptodate.tasks.*

class AlwaysUpToDate : Plugin<Project> {

    override fun apply(target: Project) {
        target.extensions.create("alwaysUpToDate", AlwaysUpToDateExtension::class.java)
        target.tasks.register("updatePaper", PaperUpdateTask::class.java)
        target.tasks.register("updatePurpur", PurpurUpdateTask::class.java)
        target.tasks.register("checkPaperCommit", CheckPaperCommitTask::class.java)
        target.tasks.register("checkPurpurCommit", CheckPurpurCommitTask::class.java)
    }

}
