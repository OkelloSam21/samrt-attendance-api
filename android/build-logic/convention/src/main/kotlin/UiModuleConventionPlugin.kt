import com.android.build.gradle.LibraryExtension
import extensions.configureAndroidCompose
import helpers.configureKotlinAndroid
import helpers.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class UiModuleConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.library")
        pluginManager.apply("org.jetbrains.kotlin.android")
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
        pluginManager.apply("com.google.devtools.ksp")
        pluginManager.apply("com.google.dagger.hilt.android")

        extensions.configure<LibraryExtension> {
            configureKotlinAndroid(this)
            defaultConfig.targetSdk = AndroidSdk.targetSdk
        }

        configureAndroidCompose(extensions.getByType(LibraryExtension::class.java))

        dependencies{
            "implementation"(libs.findLibrary("androidx-navigation").get())

            "implementation"(libs.findLibrary("hilt-android").get())
            "ksp"(libs.findLibrary("hilt-android-compiler").get())
        }
    }
}
