package extensions

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.LibraryExtension
import helpers.libs
import org.gradle.api.Project
import org.gradle.api.plugins.PluginManager
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {

    commonExtension.apply {

        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = libs.findVersion("compose-compiler").get().toString()
        }

        dependencies {
            // compose
            val bom = libs.findLibrary("compose-bom").get()
            add("implementation", platform(bom))
            add("implementation", libs.findBundle("compose").get())
            add("implementation", libs.findLibrary("compose-activity").get())
            add("androidTestImplementation", platform(bom))
            add("debugImplementation", libs.findLibrary("compose-ui-tooling").get())
            add("debugImplementation", libs.findLibrary("compose-ui-test-manifest").get())
            add("androidTestImplementation", libs.findLibrary("compose-ui-test-junit4").get())
        }
    }
}

internal fun Project.plugins(block: PluginManager.() -> Unit) {
    with(pluginManager) { block() }
}

internal fun Project.androidLibrary(block: LibraryExtension.() -> Unit) {
    extensions.configure<LibraryExtension> { block() }
}

internal fun Project.androidApplication(block: ApplicationExtension.() -> Unit) {
    extensions.configure<ApplicationExtension> { block() }
}