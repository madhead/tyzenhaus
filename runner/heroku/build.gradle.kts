plugins {
    kotlin("jvm")
    application
}

dependencies {
    libs.bundles.boms.orNull?.forEach {
        api(platform(it))
    }

    implementation(libs.ktor.server.netty)
    implementation(libs.koin.ktor)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.tgbotapi)
    implementation(libs.postgresql)
    implementation(libs.log4j.slf4j.impl)
    implementation(projects.repository.postgresql)
    implementation(projects.core)
}

application {
    applicationName = "tyzenhaus"
    mainClass.set("io.ktor.server.netty.EngineMain")
}
