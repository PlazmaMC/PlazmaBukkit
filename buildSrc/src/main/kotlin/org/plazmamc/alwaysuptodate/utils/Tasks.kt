package org.plazmamc.alwaysuptodate.utils

import io.papermc.paperweight.util.Git
import io.papermc.paperweight.util.configureTask
import io.papermc.paperweight.util.path
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.get
import org.plazmamc.alwaysuptodate.AlwaysUpToDateExtension

private var extensionAccessor: AlwaysUpToDateExtension? = null
    set(value) {
        if (field != null) throw IllegalAccessException("ExtensionAccessor already initialized")
        field = value
    }

val Project.extension: AlwaysUpToDateExtension
    get() {
        if (extensionAccessor == null) extensionAccessor = project.extensions["alwaysUpToDate"] as AlwaysUpToDateExtension
        return extensionAccessor!!
    }

val Project.git: Git get() = Git(layout.projectDirectory.path)

fun <T> Project.extension(block: AlwaysUpToDateExtension.() -> Property<T>): T =
    extension.block().get()

fun Project.property(block: AlwaysUpToDateExtension.() -> Property<String>) =
    this.property(extension(block)) as String

inline fun <reified T : Task> Task.dependsOn(name: String, description: String, noinline block: T.() -> Unit = {}): TaskProvider<T> =
    project.configureTask<T>(name, description, block).also { this.dependsOn(it) }

@JvmName("dependsOnDefaultTask")
fun Task.dependsOn(name: String, description: String, block: DefaultTask.() -> Unit = {}): TaskProvider<DefaultTask> =
    project.configureTask(name, description, block).also { this.dependsOn(it) }

inline fun <reified T : Task> Task.finalizedBy(name: String, description: String, noinline block: T.() -> Unit = {}): TaskProvider<T> =
    project.configureTask<T>(name, description, block).also { this.finalizedBy(it) }

@JvmName("finalizedByDefaultTask")
fun Task.finalizedBy(name: String, description: String, block: DefaultTask.() -> Unit = {}): TaskProvider<DefaultTask> =
    project.configureTask(name, description, block).also { this.finalizedBy(it) }

inline fun <reified T : Task> Task.mustRunAfter(name: String, description: String, noinline block: T.() -> Unit = {}): TaskProvider<T> =
    project.configureTask<T>(name, description, block).also { this.mustRunAfter(it) }

@JvmName("mustRunAfterDefaultTask")
fun Task.mustRunAfter(name: String, description: String, block: DefaultTask.() -> Unit = {}): TaskProvider<DefaultTask> =
    project.configureTask(name, description, block).also { this.mustRunAfter(it) }

inline fun <reified T: Task> Project.configureTask(name: String, description: String, noinline block: T.() -> Unit = {}): TaskProvider<T> =
    tasks.configureTask<T>(name) {
        this.group = "always up to date"
        this.description = description
        this.block()
    }

@JvmName("configureDefaultTask")
fun Project.configureTask(name: String, description: String, block: DefaultTask.() -> Unit = {}): TaskProvider<DefaultTask> =
    tasks.configureTask<DefaultTask>(name) {
        this.group = "always up to date"
        this.description = description
        this.block()
    }

inline fun <reified T: Task> Project.registerTask(name: String, description: String, crossinline block: T.() -> Unit = {}): TaskProvider<T> =
    tasks.register(name, T::class.java) {
        this.group = "always up to date"
        this.description = description
        this.block()
    }
