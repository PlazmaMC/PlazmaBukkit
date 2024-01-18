package org.plazmamc.alwaysuptodate

import org.gradle.api.provider.Property

interface AlwaysUpToDateExtension {

    val paperRepository: Property<String>
    val paperBranch: Property<String>

    val pufferfishRepository: Property<String>
    val pufferfishBranch: Property<String>

    val purpurRepository: Property<String>
    val purpurBranch: Property<String>

}
