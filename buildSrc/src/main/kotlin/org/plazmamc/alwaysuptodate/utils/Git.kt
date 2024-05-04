package org.plazmamc.alwaysuptodate.utils

import io.papermc.paperweight.util.Git
import org.plazmamc.alwaysuptodate.AlwaysUpToDateException
import java.nio.file.Path
import kotlin.io.path.notExists

fun Git.clone(name: String, uri: String, branch: String, dir: Path): Path {
    val target = dir.resolve(name)
    this("clone", "--depth", "1", "--branch", branch, uri, target.toString()).executeSilently(silenceErr = true)
    if (target.notExists()) throw AlwaysUpToDateException("Failed to clone repository")
    return target
}

fun Git.addCommit(vararg args: String) {
    this("add", ".").executeOut()
    this("commit", "-m", *args).executeOut()
}
