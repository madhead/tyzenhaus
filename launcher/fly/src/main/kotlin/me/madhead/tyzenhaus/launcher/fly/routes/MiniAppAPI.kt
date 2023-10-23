package me.madhead.tyzenhaus.launcher.fly.routes

import com.soywiz.krypto.HMAC
import io.ktor.http.HttpStatusCode
import io.ktor.http.decodeURLQueryComponent
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.localPort
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.utils.io.core.toByteArray
import me.madhead.tyzenhaus.core.service.GroupCurrenciesService
import me.madhead.tyzenhaus.core.service.GroupMembersService
import me.madhead.tyzenhaus.launcher.fly.security.APITokenPrincipal
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject

/**
 * Routes for [Telegram Mini App](https://core.telegram.org/bots/webapps) API.
 */
fun Route.miniAppAPI() {
    val config by inject<ApplicationConfig>()
    val groupMembersService by inject<GroupMembersService>()
    val groupCurrenciesService by inject<GroupCurrenciesService>()
    val webAppDataSecretKeyHash by lazy {
        HMAC.hmacSHA256(
            "WebAppData".toByteArray(),
            this@miniAppAPI.get<ApplicationConfig>().property("telegram.token").getString().toByteArray()
        )
    }

    localPort(config.property("deployment.port").getString().toInt()) {
        authenticate("api") {
            route("/app/api") {
                route("auth") {
                    post("validation") {
                        val initData = call.receiveText()
                        val fields = initData
                            .decodeURLQueryComponent()
                            .split("&")
                        val preparedData = fields
                            .filterNot { it.startsWith("hash=") }
                            .sorted()
                            .joinToString("\n")
                        val computedHash = HMAC.hmacSHA256(webAppDataSecretKeyHash.bytes, preparedData.toByteArray()).hexLower
                        val hash = fields.find { it.startsWith("hash=") }?.removePrefix("hash=")?.lowercase()

                        if (computedHash == hash) {
                            call.respond(HttpStatusCode.NoContent)
                        } else {
                            call.respond(HttpStatusCode.Unauthorized)
                        }
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
                }
            }
        }
    }
}
