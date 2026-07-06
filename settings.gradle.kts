@file:Suppress("UnstableApiUsage")

rootProject.name = "tyzenhaus"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}

include(":i18n")
include(":entity")
include(":repository")
include(":repository:postgresql")
include(":core")
include(":launcher:fly")
include(":policies")
