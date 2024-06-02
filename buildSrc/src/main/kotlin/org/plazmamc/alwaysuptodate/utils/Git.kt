package org.plazmamc.alwaysuptodate.utils

import io.papermc.paperweight.util.Git
import org.gradle.api.tasks.TaskAction
import org.plazmamc.alwaysuptodate.tasks.Task
import java.nio.file.Path
import kotlin.io.path.exists

val Git.path: Path
    get() = Git::class.java.getDeclaredField("repo").apply { isAccessible = true }.get(this) as Path

abstract class CheckGitTask : Task() {

    @TaskAction
    fun checkGit() = Git.checkForGit()

}

fun Git.revParse(): String = this("rev-parse", "HEAD").captureOut(true).out.trim()

fun Git.addCommit(vararg args: String) {
    this("add", ".").executeSilently()
    this("commit", "-m", *args).executeSilently()
    this.wait()
}

fun Git.wait() {
    val lockFile = path.resolve(".git/gc.pid")
    while (lockFile.exists()) {
        println("detected lockfile, waiting for it to be removed")
        Thread.sleep(1000)
    }
}
