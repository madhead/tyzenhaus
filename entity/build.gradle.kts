plugins {
    id("kotlin-convention")
    id("kotlin-serialization-convention")
}

dependencies {
    libs.bundles.boms.orNull?.forEach {
        api(platform(it))
    }

    implementation(libs.kotlinx.serialization.core)

    testImplementation(libs.mockk)
}
