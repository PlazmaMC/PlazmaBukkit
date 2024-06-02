package org.plazmamc.alwaysuptodate

import org.gradle.api.provider.Property

interface AlwaysUpToDateExtension {

    val paperRepo: Property<String>
    val paperRef: Property<String>

    val purpurRepo: Property<String>
    val purpurRef: Property<String>

    val pufferfishRepo: Property<String>
    val pufferfishRef: Property<String>
    val usePufferfish: Property<Boolean>

    val paperCommitName: Property<String>
    val purpurCommitName: Property<String>

}
