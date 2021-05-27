package me.madhead.tyzenhaus.entity.group.config

import java.time.Instant
import java.util.Locale

/**
 * Per-group configuration.
 * This configuration is respected when sending messages to the group, tracking expenses, calculating debts, etc…
 */
data class GroupConfig(
    val id: Long,
    val invitedBy: Long? = null,
    val invitedAt: Instant? = null,
    val language: Locale? = null,
    val members: Set<Long> = emptySet(),
)
