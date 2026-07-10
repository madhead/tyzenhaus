package me.madhead.tyzenhaus.launcher.fly.routes

import io.ktor.server.config.ApplicationConfig
import io.ktor.server.http.content.staticFiles
import io.ktor.server.routing.Route
import io.ktor.server.routing.localPort
import java.io.File
import org.koin.ktor.ext.inject

/**
 * Routes for [Telegram Mini App](https://core.telegram.org/bots/webapps).
 */
fun Route.miniApp() {
    val config by inject<ApplicationConfig>()
    val miniAppPath = config.property("telegram.miniApp.path").getString()

    localPort(config.property("deployment.port").getString().toInt()) {
        staticFiles("/app", File(miniAppPath), index = null)
    }
}
