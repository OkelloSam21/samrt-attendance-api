import org.gradle.api.Plugin
import org.gradle.api.Project
import helpers.libs
import org.gradle.kotlin.dsl.dependencies
import com.android.build.gradle.LibraryExtension
import helpers.configureKotlinAndroid
import org.gradle.kotlin.dsl.configure

class CoreModuleConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("com.google.devtools.ksp")
                apply("org.jetbrains.kotlin.plugin.serialization")
                apply("com.google.dagger.hilt.android")

            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = AndroidSdk.targetSdk

            }

            dependencies {
                // Room
                add("implementation", libs.findLibrary("androidx-room-runtime").get())
                add("implementation", libs.findLibrary("androidx-room-ktx").get())
                add("ksp", libs.findLibrary("androidx-room-compiler").get())

                // Ktor
                add("implementation", libs.findLibrary("ktor-client-core").get())
                add("implementation", libs.findLibrary("ktor-client-android").get())
                add("implementation", libs.findLibrary("ktor-client-content-negotiation").get())
                add("implementation", libs.findLibrary("ktor-serialization-kotlinx-json").get())
                add("implementation", libs.findLibrary("ktor-client-logging").get())

                // Kotlinx Serialization
                add("implementation", libs.findLibrary("kotlinx-serialization").get())

                // Coroutines
                add("implementation", libs.findLibrary("kotlinx-coroutines-core").get())
                add("implementation", libs.findLibrary("kotlinx-coroutines-android").get())

                // Core dependencies
//                add("implementation", project(":core:common"))
//                add("implementation", project(":core:domain"))

                // DataStore
                add("implementation", libs.findLibrary("androidx-datastore").get())

                //hilt
                add("implementation", libs.findLibrary("hilt-android").get())
                add("ksp", libs.findLibrary("hilt-android-compiler").get())

                // Testing
                add("testImplementation", libs.findLibrary("junit").get())
                add("androidTestImplementation", libs.findLibrary("androidx-test-ext-junit").get())
            }
        }
    }
}