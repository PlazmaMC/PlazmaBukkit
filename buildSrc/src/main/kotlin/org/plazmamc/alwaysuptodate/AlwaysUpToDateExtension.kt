package org.plazmamc.alwaysuptodate

import org.gradle.api.provider.Property

interface AlwaysUpToDateExtension {

    val paperBranchName: Property<String>
    val paperRepoName: Property<String>
    val paperCommitName: Property<String>

    val pufferfishToggleName: Property<String>
    val pufferfishBranchName: Property<String>
    val pufferfishRepoName: Property<String>

    val purpurBranchName: Property<String>
    val purpurRepoName: Property<String>
    val purpurCommitName: Property<String>

}
