package me.madhead.tyzenhaus.repository.postgresql.group.config

import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import java.sql.ResultSet
import java.util.Locale

internal fun ResultSet.toGroupConfig(): GroupConfig? {
    return if (this.next()) {
        GroupConfig(
                id = this.getLong(@Suppress("MagicNumber") 1),
                language = run {
                    val value = this.getString(@Suppress("MagicNumber") 2)

                    if (this.wasNull()) {
                        null
                    } else {
                        Locale(value)
                    }
                }
        )
    } else {
        null
    }
}
