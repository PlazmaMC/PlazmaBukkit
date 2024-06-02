package org.plazmamc.alwaysuptodate.utils

import io.papermc.paperweight.util.Command
import io.papermc.paperweight.util.directory
import org.plazmamc.alwaysuptodate.AlwaysUpToDateException
import java.nio.file.Path
import kotlin.io.path.notExists

class Gradle(private val repo: Path) {

    init {
        if (repo.resolve("gradle").notExists())
            throw AlwaysUpToDateException("Git repository does not exist: $repo")
    }

    operator fun invoke(vararg args: String): Command {
        val builder = ProcessBuilder(
            "java",
            "-cp",
            "gradle/wrapper/gradle-wrapper.jar",
            "org.gradle.wrapper.GradleWrapperMain",
            *args,
            "--no-daemon",
            "--stacktrace"
        ).directory(repo)
        val command = builder.command()
            .joinToString(" ") { if (it.codePoints().anyMatch(Character::isWhitespace)) "\"$it\"" else it }

        return try {
            Command(builder, command)
        } catch (e: Exception) {
            throw AlwaysUpToDateException("Failed to execute command: $command", e)
        }
    }

}
