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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://devrepo.kakao.com/nexus/content/groups/public/")
    }
}

rootProject.name = "Turnin"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":domain")
include(":presentation")
include(":core:designsystem")
include(":core:data")
include(":core:presentation")
include(":core:domain")
include(":core:common")
// iOS 프레임워크 모듈은 Xcode가 있는 macOS에서만 빌드 가능하므로 macOS에서만 포함한다. (Linux CI 영향 차단)
if (System.getProperty("os.name").startsWith("Mac")) {
    include(":shared")
}
