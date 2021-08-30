plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation(libs.koin.core)
    implementation(libs.postgresql)
    implementation(libs.log4j.slf4j.impl)
}

application {
    applicationName = "tyzenhaus-stats"
    mainClass.set("me.madhead.tyzenhaus.runner.heroku.stats.TyzenhausStatsKt")
}
