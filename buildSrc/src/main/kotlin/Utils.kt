import io.papermc.paperweight.checkstyle.PaperCheckstyleExt
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.newInstance
import org.gradle.plugins.ide.idea.model.IdeaModel

fun SourceSet.registerUpstreamSources(upstream: Directory) {
    java.srcDir(upstream.file("src/$name/java"))
    resources.srcDir(upstream.file("src/$name/resources"))
}

fun Provider<SourceSet>.registerUpstreamSources(upstream: Directory) =
    get().registerUpstreamSources(upstream)

fun Project.setup(upstream: Directory): Directory {
    tasks.named<io.papermc.paperweight.checkstyle.tasks.MergeCheckstyleConfigs>("mergeCheckstyleConfigs") {
        overrideConfigFile.set(upstream.file(".checkstyle/checkstyle.xml"))
    }

    extensions.configure<SourceSetContainer>("sourceSets") {
        named("main").registerUpstreamSources(upstream)
        named("test").registerUpstreamSources(upstream)
    }

    return upstream
}

fun Project.configurePaperweight(upstream: Directory) {
    extensions.configure<PaperCheckstyleExt>("paperCheckstyle") {
        directoriesToSkipFile.set(upstream.file(".checkstyle/ignored_directories.txt"))
    }

    val generatedDir: java.nio.file.Path = upstream.dir("src/generated/java").asFile.toPath()
    extensions.configure<IdeaModel>("idea") {
        module.generatedSourceDirs.add(generatedDir.toFile())
    }
    extensions.configure<SourceSetContainer>("sourceSets") {
        named("main") {
            java.srcDir(generatedDir)
        }
    }
}

fun Test.attachMockitoAgent() {
    // configure mockito agent that is needed in newer java versions
    val provider = project.objects.newInstance<MockitoAgentProvider>()
    provider.fileCollection.from(project.configurations.getByName("mockitoAgent"))
    jvmArgumentProviders.add(provider)
}
