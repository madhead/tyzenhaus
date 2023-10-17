package me.madhead.tyzenhaus.entity.api.token

import java.time.Instant
import java.util.UUID

/**
 * Scope of API tokens for Mini Apps.
 *
 * Available scopes:
 * - HISTORY: Allows to read group's transaction history.
 */
enum class Scope {
    HISTORY,
}

/**
 * Security token used to access API from the Mini Apps.
 */
data class APIToken(
    val token: UUID,
    val groupId: Long,
    val scope: Scope,
    val validUntil: Instant,
)
