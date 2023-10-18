package me.madhead.tyzenhaus.launcher.fly.routes

import io.ktor.server.config.ApplicationConfig
import io.ktor.server.http.content.react
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.routing.Route
import io.ktor.server.routing.localPort
import org.koin.ktor.ext.inject

/**
 * Routes for [Telegram Mini App](https://core.telegram.org/bots/webapps).
 */
fun Route.miniApp() {
    val config by inject<ApplicationConfig>()
    val miniAppPath = config.property("telegram.miniApp.path").getString()

    localPort(config.property("deployment.port").getString().toInt()) {
        singlePageApplication {
            applicationRoute = "app"
            react(miniAppPath)
        }
    }
}
