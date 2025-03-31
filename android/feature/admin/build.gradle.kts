plugins {
    id("com.samuelokello.convention.compose.library")
    id("com.samuelokello.convention.library")
    id("com.samuelokello.convention.feature")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.samuelokello.smartattendance.feature.admin"
}