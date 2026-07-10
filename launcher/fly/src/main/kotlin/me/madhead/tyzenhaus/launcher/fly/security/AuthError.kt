package me.madhead.tyzenhaus.launcher.fly.security

/**
 * The name of the response header carrying a machine-readable reason for an authentication/authorization failure.
 */
const val AUTH_ERROR_HEADER = "X-Auth-Error"

/**
 * Reason codes reported via the [AUTH_ERROR_HEADER].
 */
object AuthError {
    /**
     * The bearer token is missing, malformed or unknown.
     */
    const val INVALID_TOKEN = "invalid_token"

    /**
     * The bearer token has expired.
     */
    const val TOKEN_EXPIRED = "token_expired"

    /**
     * The `initData` is missing or forged.
     */
    const val INVALID_INIT_DATA = "invalid_init_data"

    /**
     * The Telegram user is not a participant of the token's group expense calculation.
     */
    const val NOT_A_PARTICIPANT = "not_a_participant"

    /**
     * The token's scope does not match the called API scope.
     */
    const val WRONG_SCOPE = "wrong_scope"
}
