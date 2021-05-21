plugins {
    kotlin("jvm")
}

dependencies {
    libs.bundles.boms.orNull?.forEach {
        api(platform(it))
    }

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)

    testRuntimeOnly(libs.junit.jupiter.engine)
}
