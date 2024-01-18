package org.plazmamc.alwaysuptodate.tasks

import io.papermc.paperweight.util.*
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.get
import org.plazmamc.alwaysuptodate.AlwaysUpToDateException
import org.plazmamc.alwaysuptodate.AlwaysUpToDateExtension
import org.plazmamc.alwaysuptodate.utils.Gradle
import org.plazmamc.alwaysuptodate.utils.addCommit
import org.plazmamc.alwaysuptodate.utils.clone
import org.plazmamc.alwaysuptodate.utils.pathIO
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

abstract class PurpurUpdateTask : Task() {

    private val property = project.extensions["alwaysUpToDate"] as AlwaysUpToDateExtension
    private val pufferfishCommit = """
        Pufferfish
        Copyright (C) 2024 Pufferfish Studios LLC

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
    private val purpurCommit = """
        PurpurMC
        Copyright (C) 2024 PurpurMC
        
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

    override fun init() {
        outputs.upToDateWhen { check() }
    }

    private fun check(): Boolean {
        val regex = "[a-z0-9]{40}\trefs/heads/${property.purpurBranch}".toRegex()
        val latestCommit = Git(project.pathIO)("ls-remote", property.purpurRepository.get()).readText()?.lines()
            ?.filterNot { regex.matches(it) }?.first()?.split("\t")?.first()
            ?: throw AlwaysUpToDateException("Failed to get latest Purpur commit")
        val currentCommit = project.properties["purpurCommit"] as String

        return currentCommit == latestCommit
    }

    @TaskAction
    fun update() {
        if (check()) return
        Git.checkForGit()

        val dir = project.layout.cache.resolve("AlwaysUpToDate/UpdatePurpur")
        if (dir.exists()) dir.toFile().deleteRecursively()

        dir.createDirectories()

        val git = Git(dir)
        val pufferfish = git.clone("Pufferfish", property.pufferfishRepository.get(), property.pufferfishBranch.get(), dir)
        val purpur = git.clone("Purpur", property.purpurRepository.get(), property.purpurBranch.get(), dir)

        updatePaperCommit(property.paperRepository.get(), property.paperBranch.get(), pufferfish.resolve("gradle.properties").toFile())
        updatePaperCommit(property.paperRepository.get(), property.paperBranch.get(), purpur.resolve("gradle.properties").toFile())
        updatePaperCommit(property.paperRepository.get(), property.paperBranch.get(), project.file("gradle.properties"))

        val purpurGradle = Gradle(purpur)
        val purpurPatches = purpur.resolve("patches").also {
            val puffefishPatches = pufferfish.resolve("patches").also { that -> that.toFile().deleteRecursively() }
            copyPatch(it.resolve("server"), puffefishPatches.resolve("server"), "0001-Pufferfish-Server-Changes.patch")
            copyPatch(it.resolve("api"), puffefishPatches.resolve("api"), "0001-Pufferfish-API-Changes.patch")
        }

        Gradle(pufferfish)("applyPatches").executeOut()
        purpurGradle("applyPatches").executeOut()

        pufferfish.resolve("pufferfish-server").also {
            val dotGit = it.resolve(".git").toFile()
            dotGit.deleteRecursively()
            copySource(it)

            val paper = pufferfish.resolve(".gradle/caches/paperweight/upstreams/paper/Paper-Server")
            copySource(paper)
            Git(paper)("add", ".").executeOut()
            Git(paper)("commit", "-m", "Vanilla Sources", "--author=Vanilla <auto@mated.null>").executeOut()
            Thread.sleep(1_000)
            paper.resolve(".git").toFile().copyRecursively(dotGit, overwrite = true)

            Git(it).addCommit("Pufferfish Server Changes\n\n$pufferfishCommit", "--author=Kevin Raneri <kevin.raneri@gmail.com>")

            val server = purpur.resolve("Purpur-Server")
            copySource(server)
            dotGit.copyRecursively(server.resolve(".git").toFile().also { that -> that.deleteRecursively() }, overwrite = true)
            Git(server).addCommit("Purpur Server Changes\n\n$purpurCommit", "--author=granny <contact@granny.dev>")
        }

        with(purpur.resolve("Purpur-API")) {
            pufferfish.resolve("pufferfish-api/.git").toFile()
                .copyRecursively(resolve(".git").toFile().also { it.deleteRecursively() }, overwrite = true)
            Git(this).addCommit("Purpur API Changes\n\n$purpurCommit", "--author=granny <contact@granny.dev>")
        }

        purpurGradle("rebuildPatches").executeOut()
        project.layout.projectDirectory.path.resolve("patches").also {
            with(purpurPatches.resolve("server")) {
                val target = it.resolve("server")
                copyPatch(this, target, "0001-Pufferfish-Server-Changes.patch")
                copyPatch(this, target, "0002-Purpur-Server-Changes.patch")
            }

            with(purpurPatches.resolve("api")) {
                val target = it.resolve("api")
                copyPatch(this, target, "0001-Pufferfish-API-Changes.patch")
                copyPatch(this, target, "0002-Purpur-API-Changes.patch")
            }
        }
    }

    private fun copySource(dir: Path) {
        with(dir.resolve(".gradle/caches/paperweight/mc-dev-sources")) {
            val target = dir.resolve("src/main")
            resolve("net").toFile().copyRecursively(target.resolve("java/net").toFile(), overwrite = true)
            resolve("data").toFile().copyRecursively(target.resolve("resources/data").toFile(), overwrite = true)
        }
    }

    private fun copyPatch(from: Path, to: Path, name: String) {
        with(from.resolve(name)) {
            if (exists()) toFile().copyTo(to.resolve(name).toFile(), overwrite = true)
            else from.toFile().walk().filter { it.name.endsWith(name.substring(4)) }.first().copyTo(to.resolve(name).toFile(), overwrite = true)
        }
    }

    abstract class PurpurBuildTask : Task() {

        private val property = project.extensions["alwaysUpToDate"] as AlwaysUpToDateExtension

        @TaskAction
        fun build() {

        }

    }

}
