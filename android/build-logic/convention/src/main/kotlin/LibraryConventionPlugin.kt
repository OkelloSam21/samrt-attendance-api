import com.android.build.gradle.LibraryExtension
import extensions.androidTestImplementation
import extensions.implementation
import extensions.testImplementation
import helpers.configureKotlinAndroid
import helpers.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class LibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("com.google.devtools.ksp")
            }
            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = AndroidSdk.targetSdk
//                buildTypes.create("beta")
            }
            configurations.configureEach {
                resolutionStrategy {
//                    force(libs.findLibrary("junit").get())
                    // Temporary workaround for https://issuetracker.google.com/174733673
                    force("org.objenesis:objenesis:2.6")
                }
            }
            dependencies {
                androidTestImplementation(kotlin("test"))
                testImplementation(kotlin("test"))
                // androidx
                implementation(libs.findLibrary("androidx-core-ktx").get())
                implementation(libs.findLibrary("androidx-appcompat").get())
                androidTestImplementation(libs.findLibrary("androidx-test-ext-junit").get())
                androidTestImplementation(libs.findLibrary("androidx-espresso-core").get())

            }
        }
    }
}