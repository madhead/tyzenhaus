plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.Dependencies.KOTLINX_SERIALIZATION}")
}
