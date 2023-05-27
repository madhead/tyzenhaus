plugins {
    kotlin("jvm")
}

dependencies {
    libs.bundles.boms.orNull?.forEach {
        api(platform(it))
    }

    api(libs.tgbotapi)
    api(projects.entity)
    api(projects.repository)
    implementation(projects.i18n)
    implementation(libs.log4j.api)
    implementation(libs.micrometer.registry.prometheus)
}
