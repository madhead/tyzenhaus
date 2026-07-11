package me.madhead.tyzenhaus.launcher.fly.security

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.auth.AuthenticationChecked
import io.ktor.server.auth.principal
import io.ktor.server.response.header
import io.ktor.server.response.respond
import me.madhead.tyzenhaus.entity.api.token.Scope
import me.madhead.tyzenhaus.launcher.fly.security.AuthError.WRONG_SCOPE

/**
 * Route scoped plugin checking for the required permissions (scope).
 */
val AuthorizationPlugin = createRouteScopedPlugin(
    name = "AuthorizationPlugin",
    createConfiguration = ::AuthorizationPluginConfiguration,
) {
    pluginConfig.apply {
        on(AuthenticationChecked) { call ->
            val principal = call.principal<APITokenPrincipal>()

            if (principal?.scope != pluginConfig.scope) {
                call.response.header(AUTH_ERROR_HEADER, WRONG_SCOPE)
                call.respond(HttpStatusCode.Forbidden)
            }
        }
    }
}

/**
 * Configuration for the [Authorization Plugin][AuthorizationPlugin].
 */
data class AuthorizationPluginConfiguration(
    var scope: Scope? = null
)
