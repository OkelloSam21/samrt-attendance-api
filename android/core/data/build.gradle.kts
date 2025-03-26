plugins {
    id("com.samuelokello.convention.library")
    id("com.samuelokello.convention.core")
}

android {
    namespace = "com.samuelokello.smartattendance.data"

    defaultConfig{
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }
}