package me.madhead.tyzenhaus.launcher.fly.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.localPort
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import me.madhead.tyzenhaus.core.service.GroupCurrenciesService
import me.madhead.tyzenhaus.core.service.GroupMembersService
import me.madhead.tyzenhaus.core.service.TransactionsSearchParams
import me.madhead.tyzenhaus.core.service.TransactionsSearchService
import me.madhead.tyzenhaus.entity.api.token.Scope
import me.madhead.tyzenhaus.launcher.fly.security.API
import me.madhead.tyzenhaus.launcher.fly.security.APITokenPrincipal
import me.madhead.tyzenhaus.launcher.fly.security.AuthorizationPlugin
import org.koin.ktor.ext.inject

/**
 * Routes for [Telegram Mini App](https://core.telegram.org/bots/webapps) API.
 *
 * All routes are guarded by the [API] authentication provider, which validates the bearer token (group binding +
 * scope), the accompanying `initData` (genuine Telegram user) and the user's membership in the token's group.
 */
fun Route.miniAppAPI() {
    val config by inject<ApplicationConfig>()
    val groupMembersService by inject<GroupMembersService>()
    val groupCurrenciesService by inject<GroupCurrenciesService>()
    val transactionsSearchService by inject<TransactionsSearchService>()

    localPort(config.property("deployment.port").getString().toInt()) {
        authenticate(API) {
            route("/app/api") {
                route("auth") {
                    // Reaching this handler means the auth provider already validated the token, initData and membership.
                    post("validation") {
                        call.respond(HttpStatusCode.NoContent)
                    }
                }

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

                    route("transactions") {
                        install(AuthorizationPlugin) { scope = Scope.HISTORY }

                        get {
                            val principal = call.principal<APITokenPrincipal>()!!

                            call.respond(transactionsSearchService.search(principal.groupId, TransactionsSearchParams()))
                        }
                    }
                }
            }
        }
    }
}
