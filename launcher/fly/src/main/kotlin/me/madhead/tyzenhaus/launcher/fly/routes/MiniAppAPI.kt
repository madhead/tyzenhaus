package me.madhead.tyzenhaus.launcher.fly.routes

import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import java.time.LocalDateTime

/**
 * Routes for [Telegram Mini App](https://core.telegram.org/bots/webapps) API.
 */
fun Route.miniAppAPI() {
    route("/app/api") {
        get("test") {
            call.respondText("""{"result":"ok", "date":"${LocalDateTime.now()}"}""", ContentType.Application.Json)
        }
    }
}
