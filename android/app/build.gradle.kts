plugins {
    id("com.samuelokello.convention.android.application")
    id("com.samuelokello.convention.compose.application")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.samuelokello.smartattendance"
}

dependencies {
    implementation(project(":modules-ui:presentation"))
}
