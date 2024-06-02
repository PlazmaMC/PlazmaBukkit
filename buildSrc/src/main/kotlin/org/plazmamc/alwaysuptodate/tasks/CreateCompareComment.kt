package org.plazmamc.alwaysuptodate.tasks

import io.papermc.paperweight.util.fromJson
import io.papermc.paperweight.util.gson
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.plazmamc.alwaysuptodate.utils.property
import paper.libs.com.google.gson.JsonObject
import java.net.URI

abstract class CreateCompareComment : Task() {

    @get:Input
    abstract val clear: Property<Boolean>

    @get:Input
    abstract val repo: Property<String>

    @get:Input
    abstract val ref: Property<String>

    @get:Input
    abstract val commitPropertyName: Property<String>

    @TaskAction
    fun create() = with(project) {
        val builder = StringBuilder()
        val rawRepo = URI.create(repo.get()).path.substring(1)

        if (clear.get() || !file("compare.txt").exists())
            builder.append("\n\nUpstream has released updates that appear to apply and compile correctly.")
        else
            builder.append(file("compare.txt").readText())

        builder.append("\n\n[${rawRepo.split("/").last()} Changes]\n")

        gson.fromJson<JsonObject>(
            URI.create("https://api.github.com/repos/$rawRepo/compare/${property { commitPropertyName }}...${ref.get()}").toURL().readText()
        )["commits"].asJsonArray.forEach { obj ->
            obj.asJsonObject.let {
                builder.append("$rawRepo@${it["sha"].asString.subSequence(0, 7)}: ${it["commit"].asJsonObject["message"].asString.split("\n")[0]}\n")
            }
        }
        file("compare.txt").writeText(builder.toString())
    }

}
