plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation("commons-validator:commons-validator:1.8.0")

    libs.bundles.boms.orNull?.forEach {
        api(platform(it))
    }

    implementation(libs.kotlinx.serialization.core)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockk)

    testRuntimeOnly(libs.junit.jupiter.engine)
}
