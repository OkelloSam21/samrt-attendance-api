package helpers

import AndroidSdk
import com.android.build.api.dsl.CommonExtension
import extensions.compilerOptions
import org.gradle.api.JavaVersion
import org.gradle.api.Project


internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = AndroidSdk.compileSdk
        defaultConfig {
            minSdk = AndroidSdk.minimumSdk
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
        buildFeatures {
            buildConfig = true
        }
    }
    commonExtension.compilerOptions {
//        allWarningsAsErrors = true
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.FlowPreview",
            "-opt-in=kotlin.Experimental"
        )
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}