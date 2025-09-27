pluginManagement {
    includeBuild("build-logic") // use this plugin
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
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google()
        mavenCentral()
        maven("https://devrepo.kakao.com/nexus/content/groups/public/")
    }
}

rootProject.name = "Peekr"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":domain")
include(":data")
include(":presentation")
include(":core:designsystem")
include(":core:data")
include(":core:presentation")
include(":core:domain")
include(":core:common")
