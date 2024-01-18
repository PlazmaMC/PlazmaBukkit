package org.plazmamc.alwaysuptodate.utils

import io.papermc.paperweight.util.path
import org.gradle.api.Project
import java.nio.file.Path

val Project.pathIO: Path get() = layout.projectDirectory.path
