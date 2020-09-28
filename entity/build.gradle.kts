plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    libs.bundles.boms.orNull?.forEach {
        api(platform(it))
    }

    implementation(libs.kotlinx.serialization.core)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)

    testRuntimeOnly(libs.junit.jupiter.engine)
}
