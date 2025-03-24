import com.android.build.api.dsl.ApplicationExtension
import helpers.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import helpers.libs

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.application")
        pluginManager.apply("org.jetbrains.kotlin.android")
        pluginManager.apply("com.google.dagger.hilt.android")
        pluginManager.apply("com.google.devtools.ksp")

        extensions.configure<ApplicationExtension> {
            configureKotlinAndroid(this)
            defaultConfig.targetSdk = AndroidSdk.targetSdk

            buildTypes.getByName("release").apply {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }
        dependencies {
            "implementation" (libs.findLibrary("hilt-android").get())
            "ksp"(libs.findLibrary("hilt-android-compiler").get())
        }
    }
}
