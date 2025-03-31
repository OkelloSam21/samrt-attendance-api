pluginManagement {
//    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Smart Attendance"
include(":app")
//include(":core:common")
//include(":core:data")
//include(":core:domain")
//include(":feature:auth")
//include(":feature:admin")
//include(":feature:lecturer")
//include(":feature:student")
//include(":modules-ui:common")
//include(":modules-ui:design")
//include(":modules-ui:presentation")
//include(":modules-ui:resources")
//include(":feature:mylibrary")
