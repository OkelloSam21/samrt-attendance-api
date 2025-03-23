import com.android.build.gradle.LibraryExtension
import extensions.configureAndroidCompose
import helpers.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class FeatureModuleConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.library")

        extensions.configure<LibraryExtension> {
            configureKotlinAndroid(this)
            defaultConfig.targetSdk = AndroidSdk.targetSdk

        }

        configureAndroidCompose(extensions.getByType(LibraryExtension::class.java))

        dependencies {
            "implementation"(project(":modules-ui:common"))
        }
    }
}
