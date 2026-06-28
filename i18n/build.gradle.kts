plugins {
    id("kotlin-convention")
}

dependencies {
    libs.bundles.boms.orNull?.forEach {
        api(platform(it))
    }

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)

    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}
