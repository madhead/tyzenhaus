package me.madhead.tyzenhaus.launcher.fly.security

import io.ktor.server.auth.Principal
import me.madhead.tyzenhaus.entity.api.token.Scope

/**
 * Represents a principal (authentication result) used to access API for the Mini Apps.
 */
data class APITokenPrincipal(
    val groupId: Long,
    val scope: Scope,
) : Principal
