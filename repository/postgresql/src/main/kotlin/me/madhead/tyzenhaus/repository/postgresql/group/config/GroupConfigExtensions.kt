package me.madhead.tyzenhaus.repository.postgresql.group.config

import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import java.sql.ResultSet
import java.util.Locale

internal fun ResultSet.toGroupConfig(): GroupConfig? {
    return if (this.next()) {
        GroupConfig(
            id = this.getLong("id"),
            invitedBy = run {
                val value = this.getLong("invited_by")

                if (this.wasNull()) {
                    null
                } else {
                    value
                }
            },
            invitedAt = run {
                val value = this.getTimestamp("invited_at")

                if (this.wasNull()) {
                    null
                } else {
                    value.toInstant()
                }
            },
            language = run {
                val value = this.getString("language")

                if (this.wasNull()) {
                    null
                } else {
                    Locale(value)
                }
            },
            members = run {
                val value = this.getArray("members")

                if (this.wasNull()) {
                    emptySet()
                } else {
                    (value.array as? Array<*>)?.filterIsInstance<Long>()?.toSet() ?: emptySet()
                }
            }
        )
    } else {
        null
    }
}
