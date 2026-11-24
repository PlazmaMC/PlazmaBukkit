import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.CompileClasspath
import org.gradle.process.CommandLineArgumentProvider

abstract class MockitoAgentProvider : CommandLineArgumentProvider {
    @get:CompileClasspath
    abstract val fileCollection: ConfigurableFileCollection

    override fun asArguments(): Iterable<String> {
        return listOf("-javaagent:" + fileCollection.files.single().absolutePath)
    }
}
