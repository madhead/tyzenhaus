package me.madhead.tyzenhaus.entity.groupconfig

import java.util.Locale

/**
 * Per-group configuration.
 * This configuration is respected when sending messages to the group, tracking expenses, calculating debts, etc…
 */
data class GroupConfig(
        val id: Long,
        val language: Locale? = null,
)
