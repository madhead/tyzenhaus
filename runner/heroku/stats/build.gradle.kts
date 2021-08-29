plugins {
    kotlin("jvm")
    application
}

dependencies {
}

application {
    applicationName = "tyzenhaus-stats"
    mainClass.set("me.madhead.tyzenhaus.runner.heroku.stats.TyzenhausStatsKt")
}
