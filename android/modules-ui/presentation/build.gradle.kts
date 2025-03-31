plugins {
    id("com.samuelokello.convention.compose.library")
    id("com.samuelokello.convention.ui")
}
android {
    namespace = "com.samuelokello.smartattendance.modulesui.presentation"
}

dependencies {
    implementation(project(":feature:admin"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:student"))
    implementation(project(":feature:lecturer"))
}