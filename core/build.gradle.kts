plugins {
    kotlin("jvm")
}

dependencies {
    api(libs.tgbotapi)
    api(projects.entity)
    api(projects.repository)
    implementation(projects.i18n)
    implementation(libs.log4j.core)
}
