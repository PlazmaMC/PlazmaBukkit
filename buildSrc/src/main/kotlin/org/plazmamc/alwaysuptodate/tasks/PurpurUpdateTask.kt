package org.plazmamc.alwaysuptodate.tasks

import io.papermc.paperweight.patcher.tasks.CheckoutRepo
import io.papermc.paperweight.util.Git
import io.papermc.paperweight.util.cache
import io.papermc.paperweight.util.path
import io.papermc.paperweight.util.set
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.plazmamc.alwaysuptodate.utils.*
import java.io.File
import java.nio.file.Path
import java.util.*
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.name
import kotlin.io.path.walk

@Deprecated("It will soon be changed to be available for other upstreams.")
abstract class PurpurUpdateTask : Task() {

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
    private val pufferfishAuthor = "Kevin Raneri <kevin.raneri@gmail.com>"
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
    private val purpurAuthor = "granny <contact@granny.dev>"

    @get:Internal
    abstract val workDir: DirectoryProperty

    @get:OutputDirectory
    abstract val purpurDir: DirectoryProperty

    @get:OutputDirectory
    abstract val pufferfishDir: DirectoryProperty

    override fun init(): Unit = with(project) {
        val wd = layout.cache.resolve("alwaysUpToDate/update/purpur").also { it.toFile().deleteRecursively() }
        workDir.set(wd)

        val compare =
            configureTask<CreateCompareComment>("createPurpurCompareComment", "Create Purpur Compare Comment") {
                clear.convention(true)
                repo.convention(extension.purpurRepo)
                ref.convention(extension.purpurRef)
                commitPropertyName.convention(extension.purpurCommitName)
            }

        val paper = dependsOn<SimpleUpstreamUpdateTask>("updatePaper", "Update Paper") {
            outputs.upToDateWhen { checkCommitFor { repo to ref to paperCommitName } }
            dependsOn(compare)
            repo.convention(extension.paperRepo)
            ref.convention(extension.paperRef)
            regex.convention("paperCommit = ")
            workDir.set(layout.projectDirectory)
        }

        fun checkout(
            name: String, repo: Provider<String>, ref: Provider<String>, regex: String, block: CheckoutRepo.() -> Unit
        ): Pair<TaskProvider<CheckoutRepo>, Directory> {
            val updatePaper = configureTask<SimpleUpstreamUpdateTask>("update${name}Paper", "Update $name's Paper") {
                this.repo.convention(extension.paperRepo)
                this.ref.convention(extension.paperRef)
                this.regex.convention(regex)
                this.workDir.set(wd.resolve(name))
            }

            val checkout = dependsOn<CheckoutRepo>("checkout$name", "Checkout $name") {
                this.dependsOn(paper)
                this.repoName.convention(name)
                this.url.convention(repo)
                this.ref.convention(ref)
                this.workDir.set(wd)

                this.block()
                // this.finalizedBy(updatePaper)
            }

            return checkout to checkout.flatMap { it.outputDir }.get()
        }

        val (checkoutPufferfish, pufferfish) =
            checkout("Pufferfish", extension.pufferfishRepo, extension.pufferfishRef, "paperRef=") {
                onlyIf { extension { usePufferfish } }
            }
        val (checkoutPurpur, purpur) =
            checkout("Purpur", extension.purpurRepo, extension.purpurRef, "paperCommit = ") {}

        pufferfishDir.set(pufferfish)
        purpurDir.set(purpur)

        val preparePurpur = configureTask("preparePurpur", "Prepare Purpur Sources") {
            mustRunAfter(paper)
            dependsOn(checkoutPurpur)
            doLast { Gradle(purpur.path)("applyPatches").executeOut() }
        }
        val preparePufferfish = configureTask("preparePufferfish", "Prepare Pufferfish Sources") {
            onlyIf { extension { usePufferfish } }
            mustRunAfter(paper)
            dependsOn(checkoutPurpur, checkoutPufferfish)
            doLast {
                val base = pufferfish.path.resolve("patches").also { it.toFile().deleteRecursively() }
                val source = purpur.path.resolve("patches")
                source.resolve("server").copyPatch(base.resolve("server"), "Pufferfish-Server-Changes")
                source.resolve("api").copyPatch(base.resolve("api"), "Pufferfish-API-Changes")
                Gradle(pufferfish.path)("applyPatches").executeOut()
            }
        }

        dependsOn(preparePurpur, preparePufferfish)

        val serverPatch =
            configureTask<GenerateMergedServerPatch>("generateMergedServerPatches", "Generate Merged Server Patch") {
                dependsOn(preparePurpur)
                if (!extension { usePufferfish }) {
                    workDir.convention(purpur.dir("Purpur-Server"))
                    commitTitle.convention("Purpur Server Changes")
                    license.convention(purpurHeader)
                    author.convention(purpurAuthor)
                    return@configureTask
                }

                dependsOn(preparePufferfish)
                workDir.convention(pufferfish.dir("pufferfish-server"))
                commitTitle.convention("Pufferfish Server Changes")
                license.convention(pufferfishHeader)
                author.convention(pufferfishAuthor)

                doLast {
                    val dotGit = pufferfish.dir("pufferfish-server/.git").path.toFile()

                    purpur.path.resolve("Purpur-Server").let {
                        val purpurDotGit = it.resolve(".git").toFile().also(File::deleteRecursively)

                        copySource(it)
                        dotGit.copyRecursively(purpurDotGit, overwrite = true)
                        Git(it).addCommit("Purpur Server Changes\n\n$purpurHeader", "--author=$purpurAuthor")
                    }
                }
            }

        val pufferfishAPIChanges = configureTask<GenerateMergedAPIPatch>(
            "generateMergedPufferfishAPIPatch",
            "Generate Merged Pufferfish API Patch"
        ) {
            dependsOn(preparePufferfish)
            inputDir.convention(pufferfish.dir(".gradle/caches/paperweight/upstreams/paper/Paper-API/.git"))
            workDir.convention(pufferfish.dir("pufferfish-api"))
            commitTitle.convention("Pufferfish API Changes")
            license.convention(pufferfishHeader)
            author.convention(pufferfishAuthor)
        }

        val apiPatch = configureTask<GenerateMergedAPIPatch>(
            "generateMergedAPIPatches",
            "Generate Merged API Patches"
        ) {
            dependsOn(preparePurpur)
            if (extension { usePufferfish }) dependsOn(pufferfishAPIChanges)

            workDir.convention(purpur.dir("Purpur-API"))
            commitTitle.convention("Purpur API Changes")
            license.convention(purpurHeader)
            author.convention(purpurAuthor)
            inputDir.convention(
                if (extension { usePufferfish }) pufferfish.dir("pufferfish-api/.git")
                else purpur.dir(".gradle/caches/paperweight/upstreams/paper/Paper-API/.git")
            )
        }

        dependsOn("buildPatches", "Build Merged Patches") {
            dependsOn(serverPatch, apiPatch)
            doLast { Gradle(purpur.path)("rebuildPatches").executeOut() }
        }
    }

    @TaskAction
    fun update() = with(project) {
        val purpur = purpurDir.path
        val pufferfish = if (extension { usePufferfish }) pufferfishDir.path else null

        val patches = purpur.resolve("patches")
        with(layout.projectDirectory.path.resolve("patches")) {
            patches.resolve("server").copyPatch( resolve("server"),
                if (pufferfish == null) "" else "Pufferfish-Server-Changes",
                "Purpur-Server-Changes"
            )

            patches.resolve("api").copyPatch( resolve("api"),
                if (pufferfish == null) "" else "Pufferfish-API-Changes",
                "Purpur-API-Changes"
            )
        }

        file("gradle.properties").let {
            it.writeText(
                it.readText().replace("purpurCommit = .*".toRegex(), "purpurCommit = ${Git(purpur).revParse()}")
            )
        }
    }

}

@OptIn(ExperimentalPathApi::class)
private fun Path.copyPatch(to: Path, vararg name: String) = walk().sorted()
    .filter { entry -> name.filter { it != "" }.any { entry.name.endsWith("$it.patch") } }.map(Path::toFile)
    .forEachIndexed { count, patch ->
        patch.copyTo(
            to.resolve("${count + 1}".padStart(4, '0') + "-" + name.first { patch.name.substring(5) == "$it.patch" } + ".patch").toFile(),
            overwrite = true
        )
    }
