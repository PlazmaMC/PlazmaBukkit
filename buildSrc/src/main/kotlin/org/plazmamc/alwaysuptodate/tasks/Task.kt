package org.plazmamc.alwaysuptodate.tasks

import org.gradle.api.DefaultTask

abstract class Task : DefaultTask() {

    protected abstract fun init()

    init { this.init() }

}
