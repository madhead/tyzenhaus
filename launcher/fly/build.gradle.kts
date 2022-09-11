plugins {
    kotlin("jvm")
    application
}

dependencies {
    libs.bundles.boms.orNull?.forEach {
        api(platform(it))
    }

    implementation(libs.bundles.ktor)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.tgbotapi)
    implementation(libs.postgresql)
    implementation(libs.log4j.slf4j.impl)
    implementation(libs.micrometer.registry.prometheus)
    implementation(projects.repository.postgresql)
    implementation(projects.core)
}

application {
    applicationName = "tyzenhaus"
    mainClass.set("me.madhead.tyzenhaus.launcher.fly.TyzenhausKt")
}
