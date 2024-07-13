enableFeaturePreview(org.gradle.api.internal.FeaturePreviews.Feature.TYPESAFE_PROJECT_ACCESSORS.name)

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}

rootProject.name = "tyzenhaus"

include(":i18n")
include(":entity")
include(":repository")
include(":repository:postgresql")
include(":core")
include(":launcher:fly")
include(":policies")
