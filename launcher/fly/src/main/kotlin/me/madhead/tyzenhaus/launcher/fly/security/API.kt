package me.madhead.tyzenhaus.launcher.fly.security

import io.ktor.http.HttpStatusCode
import io.ktor.http.decodeURLQueryComponent
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.bearer
import io.ktor.server.request.header
import io.ktor.server.response.header
import io.ktor.server.response.respond
import java.time.Instant
import java.util.UUID
import korlibs.crypto.HMAC
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import me.madhead.tyzenhaus.launcher.fly.security.AuthError.INVALID_INIT_DATA
import me.madhead.tyzenhaus.launcher.fly.security.AuthError.INVALID_TOKEN
import me.madhead.tyzenhaus.launcher.fly.security.AuthError.NOT_A_PARTICIPANT
import me.madhead.tyzenhaus.launcher.fly.security.AuthError.TOKEN_EXPIRED
import me.madhead.tyzenhaus.repository.APITokenRepository
import me.madhead.tyzenhaus.repository.GroupConfigRepository

/**
 * Name of the authentication provider guarding the Mini App API.
 */
const val API = "api"

/**
 * Configures [API] authentication provider guarding the Mini App API.
 */
fun AuthenticationConfig.api(
    botToken: String,
    tokenRepository: APITokenRepository,
    groupConfigRepository: GroupConfigRepository,
    json: Json,
) {
    val webAppDataSecretKeyHash = HMAC.hmacSHA256("WebAppData".toByteArray(), botToken.toByteArray()).bytes

    bearer(API) {
        authenticate { credential ->
            val token = try {
                UUID.fromString(credential.token)
            } catch (_: Exception) {
                return@authenticate unauthorized(INVALID_TOKEN)
            }
            val apiToken = tokenRepository.get(token)
            val userId = request.header("X-Telegram-Init-Data")?.validate(webAppDataSecretKeyHash, json)

            when {
                apiToken == null -> unauthorized(INVALID_TOKEN)

                apiToken.validUntil < Instant.now() -> unauthorized(TOKEN_EXPIRED)

                userId == null -> unauthorized(INVALID_INIT_DATA)

                groupConfigRepository.get(apiToken.groupId)?.members?.contains(userId) != true -> forbidden(NOT_A_PARTICIPANT)

                else -> APITokenPrincipal(apiToken.groupId, apiToken.scope)
            }
        }
    }
}

/**
 * [Validates](https://core.telegram.org/bots/webapps#validating-data-received-via-the-mini-app) Telegram Mini App's `initData`
 */
fun String.validate(
    secretKeyHash: ByteArray,
    json: Json,
): Long? {
    val fields = this
        .decodeURLQueryComponent()
        .split("&")
    val hash = fields.value("hash")?.lowercase()
    val computedHash = HMAC
        .hmacSHA256(
            secretKeyHash,
            fields.filterNot { it.startsWith("hash=") }.sorted().joinToString("\n").toByteArray(),
        )
        .hexLower

    if (hash == null || hash != computedHash) return null

    return fields
        .value("user")
        ?.let { user ->
            runCatching {
                json.parseToJsonElement(user).jsonObject["id"]?.jsonPrimitive?.longOrNull
            }.getOrNull()
        }
}

private fun List<String>.value(field: String): String? =
    find { it.startsWith("$field=") }?.removePrefix("$field=")

private suspend fun ApplicationCall.unauthorized(code: String): APITokenPrincipal? {
    response.header(AUTH_ERROR_HEADER, code)

    respond(HttpStatusCode.Unauthorized)

    return null
}

private suspend fun ApplicationCall.forbidden(code: String): APITokenPrincipal? {
    response.header(AUTH_ERROR_HEADER, code)

    respond(HttpStatusCode.Forbidden)

    return null
}
