package extensions

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.PluginManager
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {

    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    commonExtension.apply {

        buildFeatures {
            compose = true
        }

        compilerOptions {
            freeCompilerArgs
        }

        dependencies {
            // compose
            val bom = libs.findLibrary("compose-bom").get()
            add("implementation", platform(bom))
            add("implementation", libs.findBundle("compose").get())
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