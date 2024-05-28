package org.plazmamc.alwaysuptodate.tasks

import io.papermc.paperweight.util.Git
import io.papermc.paperweight.util.cache
import io.papermc.paperweight.util.path
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.get
import org.plazmamc.alwaysuptodate.AlwaysUpToDateExtension
import org.plazmamc.alwaysuptodate.utils.Gradle
import org.plazmamc.alwaysuptodate.utils.addCommit
import org.plazmamc.alwaysuptodate.utils.clone
import org.plazmamc.alwaysuptodate.utils.propValue
import java.io.File
import java.nio.file.Path
import java.util.Calendar
import kotlin.io.path.*

abstract class PurpurUpdateTask : Task() {

    private val property = project.extensions["alwaysUpToDate"] as AlwaysUpToDateExtension
    private val pufferfishHeader = """
        Pufferfish
        Copyright (C) ${Calendar.getInstance().get(Calendar.YEAR)} Pufferfish Studios LLC

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <http://www.gnu.org/licenses/>.
    """.trimIndent()
    private val purpurHeader = """
        PurpurMC
        Copyright (C) ${Calendar.getInstance().get(Calendar.YEAR)} PurpurMC
        
        Permission is hereby granted, free of charge, to any person obtaining a copy
        of this software and associated documentation files (the "Software"), to deal
        in the Software without restriction, including without limitation the rights
        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
        copies of the Software, and to permit persons to whom the Software is
        furnished to do so, subject to the following conditions:

        The above copyright notice and this permission notice shall be included in all
        copies or substantial portions of the Software.

        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
        SOFTWARE.
    """.trimIndent()

    override fun init() = outputs.upToDateWhen {
        project.checkCommit(
            project.propValue(property.purpurRepoName),
            project.propValue(property.purpurBranchName),
            property.purpurCommitName.get()
        )
    }

    @TaskAction
    fun update() = with(project) {
        Git.checkForGit()

        if (checkCommit(propValue(property.purpurRepoName), propValue(property.purpurBranchName), property.purpurCommitName.get()))
            return

        createCompareComment(
            propValue(property.purpurRepoName), propValue(property.purpurBranchName), propValue(property.purpurCommitName), true
        )

        val dir = layout.cache.resolve("AlwaysUpToDate/UpdatePurpur")

        if (dir.exists()) dir.toFile().deleteRecursively()
        dir.createDirectories()

        val git = Git(dir)
        val purpur = git.clone("Purpur", propValue(property.purpurRepoName), propValue(property.purpurBranchName), dir)
        val pufferfish = if (propValue(property.pufferfishToggleName).toBoolean()) git.clone("Pufferfish", propValue(property.pufferfishRepoName), propValue(property.pufferfishBranchName), dir) else null

        updateSourceBase(purpur)
        if (pufferfish != null) updateSourceBase(pufferfish, "paperRef=")

        val latest = getLatest(property.purpurRepoName.get(), property.purpurBranchName.get())

        val gradle = Gradle(purpur)
        val patches = purpur.resolve("patches").also { patch ->
            if (pufferfish == null) return@also

            val base = pufferfish.resolve("patches").also { it.toFile().deleteRecursively() }
            patch.resolve("server").copyPatch(base.resolve("server"), "Pufferfish-Server-Changes")
            patch.resolve("api").copyPatch(base.resolve("api"), "Pufferfish-API-Changes")
            Gradle(pufferfish)("applyPatches").executeOut()
        }
        gradle("applyPatches").executeOut()

        (pufferfish?.resolve("pufferfish-server") ?: purpur.resolve("Purpur-Server")).let {
            val dotGit = it.resolve(".git").toFile().also(File::deleteRecursively)
            copySource(it)

            val paper = it.resolve("../.gradle/caches/paperweight/upstreams/paper/Paper-Server")
            copySource(paper)

            Git(paper).addCommit("Vanilla Sources", "--author=Vanilla <auto@mated.null>")
            Thread.sleep(1_000)
            paper.resolve(".git").toFile().copyRecursively(dotGit, overwrite = true)

            if (pufferfish == null)
                return@let Git(it).addCommit("Purpur Server Changes\n\n$purpurHeader", "--author=granny <contact@granny.dev>")

            Git(it).addCommit("Pufferfish Server Changes\n\n$pufferfishHeader", "--author=Kevin Raneri <kevin.raneri@gmail.com>")

            purpur.resolve("Purpur-Server").let { that ->
                val purpurDotGit = that.resolve(".git").toFile().also(File::deleteRecursively)

                copySource(that)
                dotGit.copyRecursively(purpurDotGit, overwrite = true)
                Git(that).addCommit("Purpur Server Changes\n\n$purpurHeader", "--author=granny <contact@granny.dev>")
            }
        }

        with(purpur.resolve("Purpur-API")) {
            val dotGit = resolve(".git").toFile().also(File::deleteRecursively)
            
            (pufferfish?.resolve("pufferfish-api/.git")?.toFile()?.also {
                it.deleteRecursively()
                it.resolve("../.gradle/caches/paperweight/upstreams/paper/Paper-API/.git").copyRecursively(it, overwrite = true)
                Git(it).addCommit("Pufferfish API Changes\n\n$pufferfishHeader", "--author=Kevin Raneri <kevin.raneri@gmail.com>")
            } ?: resolve("../.gradle/caches/paperweight/upstreams/paper/Paper-API/.git").toFile())
                .copyRecursively(dotGit, overwrite = true)

            Git(this).addCommit("Purpur API Changes\n\n$purpurHeader", "--author=granny <contact@granny.dev>")
        }

        gradle("rebuildPatches").executeOut()
        with(layout.projectDirectory.path.resolve("patches")) {
            patches.resolve("server").copyPatch(resolve("server"),
                if (pufferfish == null) "" else "Pufferfish-Server-Changes",
                "Purpur-Server-Changes"
            )

            patches.resolve("api").copyPatch(resolve("api"),
                if (pufferfish == null) "" else "Pufferfish-API-Changes",
                "Purpur-API-Changes"
            )
        }

        file("gradle.properties").let {
            it.writeText(it.readText().replace("purpurCommit = .*".toRegex(), "purpurCommit = $latest"))
        }
    }

    private fun Project.updateSourceBase(source: Path, regex: String? = null) = properties
        .let { it[property.paperRepoName.get()].toString() to it[property.paperBranchName.get()].toString() }
        .let {
            updatePaperCommit(it.first, it.second, source.resolve("gradle.properties").toFile(), regex ?: "paperCommit = ")

            if (checkCommit(it.first, it.second, property.paperCommitName.get())) return@let

            createCompareComment(it.first, it.second, propValue(property.paperCommitName))
            updatePaperCommit(it.first, it.second, file("gradle.properties"))
        }

    private fun copySource(dir: Path) = with(dir.resolve(".gradle/caches/paperweight/mc-dev-sources")) {
        val target = dir.resolve("src/main")
        resolve("net").toFile().copyRecursively(target.resolve("java/net").toFile(), overwrite = true)
        resolve("data").toFile().copyRecursively(target.resolve("resources/data").toFile(), overwrite = true)
    }
    
    private fun Path.copyPatch(to: Path, vararg name: String) = listDirectoryEntries()
        .filter { entry -> name.any { it.endsWith(entry.name.substring(5) + ".patch") } }.map(Path::toFile)
        .forEachIndexed { count, patch ->
            patch.copyTo(
                to.resolve(count.toString().padStart(4, '0') + "-" + name.first { patch.name.substring(5) == "$it.patch" }).toFile(),
                overwrite = true
            )
        }

}
