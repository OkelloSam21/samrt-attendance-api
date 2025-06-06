import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.LibraryExtension
import extensions.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class ComposeApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

        val extension = extensions.getByType<ApplicationExtension>()
        configureAndroidCompose(extension)
        dependencies {
            "implementation"(project(":modules-ui:presentation"))
        }
    }
}

class ComposeLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
    }
}
