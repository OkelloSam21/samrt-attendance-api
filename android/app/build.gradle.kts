plugins {
    id("com.samuelokello.convention.android.application")
    id("com.samuelokello.convention.compose.application")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.samuelokello.smartattendance"
}

dependencies {
}
