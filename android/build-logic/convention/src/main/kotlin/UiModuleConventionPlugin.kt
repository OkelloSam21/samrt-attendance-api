import AndroidSdk
import com.android.build.gradle.LibraryExtension
import extensions.configureAndroidCompose
import helpers.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class UiModuleConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.library")
        pluginManager.apply("org.jetbrains.kotlin.android")
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

        extensions.configure<LibraryExtension> {
            configureKotlinAndroid(this)
            defaultConfig.targetSdk = AndroidSdk.targetSdk
        }

        configureAndroidCompose(extensions.getByType(LibraryExtension::class.java))
    }
}
