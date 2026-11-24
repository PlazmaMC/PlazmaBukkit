plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(catalog(libs))

    implementation(kotlin("gradle-plugin", version = libs.versions.kotlin.get()))
    implementation(libraryOf(libs.plugins.paperweight.patcher))
}

// <editor-fold desc="Helper functions" defaultstate="collapsed"> // @formatter:off
@Suppress("detekt:all") private fun catalog(catalog: Any) = files(catalog.javaClass.superclass.protectionDomain.codeSource.location)
@Suppress("detekt:all") private fun libraryOf(provider: Provider<PluginDependency>): Provider<ExternalModuleDependency> = provider.map /* <editor-fold desc="{...}" defaultstate="collapsed"> */{ /* <editor-fold desc="DefaultExternalModuleDependency" defaultstate="collapsed"> */org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency/* </editor-fold> */(/* <editor-fold desc="DefaultModuleIdentifier" defaultstate="collapsed"> */org.gradle.api.internal.artifacts.DefaultModuleIdentifier/* </editor-fold> */.newId(it.pluginId, "${it.pluginId}.gradle.plugin"), /* <editor-fold desc="DefaultMutableVersionConstraint" defaultstate="collapsed"> */org.gradle.api.internal.artifacts.dependencies.DefaultMutableVersionConstraint/* </editor-fold> */(it.version), null) }/* </editor-fold> */
@Suppress("detekt:all") private fun kotlinKGP(module: String, version: Provider<String>): String = "org.jetbrains.kotlin.$module:org.jetbrains.kotlin.$module.gradle.plugin:${version.get()}"
// @formatter:on // </editor-fold>
