package me.madhead.tyzenhaus.launcher.fly.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.localPort
import io.ktor.server.routing.route
import me.madhead.tyzenhaus.core.service.GroupMembersService
import me.madhead.tyzenhaus.launcher.fly.security.APITokenPrincipal
import org.koin.ktor.ext.inject

/**
 * Routes for [Telegram Mini App](https://core.telegram.org/bots/webapps) API.
 */
fun Route.miniAppAPI() {
    val config by inject<ApplicationConfig>()
    val groupMembersService by inject<GroupMembersService>()

    localPort(config.property("deployment.port").getString().toInt()) {
        route("/app/api") {
            authenticate("api") {
                get("/group/members") {
                    val principal = call.principal<APITokenPrincipal>()!!
                    val members = groupMembersService.groupMembers(principal.groupId)

                    if (members != null) {
                        call.respond(members)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            }
        }
    }
}
