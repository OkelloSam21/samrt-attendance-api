plugins {
    `kotlin-dsl`
    id("com.google.devtools.ksp") version "2.1.10-1.0.31"
}

group = "com.samuelokello.convention"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    implementation(libs.com.google.devtools.ksp.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication"){
            id = "com.samuelokello.convention.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("composeApplication") {
            id = "com.samuelokello.convention.compose.application"
            implementationClass = "ComposeApplicationConventionPlugin"
        }
        register("library-convention") {
            id = "com.samuelokello.convention.library"
            implementationClass = "LibraryConventionPlugin"
        }
        register("composeLibrary") {
            id = "com.samuelokello.convention.compose.library"
            implementationClass = "ComposeLibraryConventionPlugin"
        }
        register("coreModule") {
            id = "com.samuelokello.convention.core"
            implementationClass = "CoreModuleConventionPlugin"
        }
        register("featureModule") {
            id = "com.samuelokello.convention.feature"
            implementationClass = "FeatureConventionPlugin"
        }
        register("uiModule") {
            id = "com.samuelokello.convention.ui"
            implementationClass = "UiModuleConventionPlugin"
        }
    }
}
