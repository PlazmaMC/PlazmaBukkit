package org.plazmamc.alwaysuptodate.utils

import io.papermc.paperweight.util.path
import org.gradle.api.Project
import org.gradle.api.provider.Property
import java.nio.file.Path

val Project.pathIO: Path get() = layout.projectDirectory.path

fun Project.propValue(name: Property<String>) =
    this.property(name.get()) as String
