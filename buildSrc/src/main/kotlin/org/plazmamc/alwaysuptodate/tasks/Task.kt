package org.plazmamc.alwaysuptodate.tasks

import org.gradle.api.DefaultTask

abstract class Task : DefaultTask() {

    protected open fun init() {}

    init { this.init() }

}
