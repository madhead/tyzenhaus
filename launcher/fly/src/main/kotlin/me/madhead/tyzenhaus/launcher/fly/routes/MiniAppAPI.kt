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
import me.madhead.tyzenhaus.core.service.GroupCurrenciesService
import me.madhead.tyzenhaus.core.service.GroupMembersService
import me.madhead.tyzenhaus.launcher.fly.security.APITokenPrincipal
import org.koin.ktor.ext.inject

/**
 * Routes for [Telegram Mini App](https://core.telegram.org/bots/webapps) API.
 */
fun Route.miniAppAPI() {
    val config by inject<ApplicationConfig>()
    val groupMembersService by inject<GroupMembersService>()
    val groupCurrenciesService by inject<GroupCurrenciesService>()

    localPort(config.property("deployment.port").getString().toInt()) {
        authenticate("api") {
            route("/app/api") {
                route("group") {
                    get("members") {
                        val principal = call.principal<APITokenPrincipal>()!!
                        val members = groupMembersService.groupMembers(principal.groupId)

                        if (members != null) {
                            call.respond(members)
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    }

                    get("currencies") {
                        val principal = call.principal<APITokenPrincipal>()!!
                        val currencies = groupCurrenciesService.groupCurrencies(principal.groupId) ?: emptyList()

                        call.respond(currencies)
                    }
                }
            }
        }
    }
}
