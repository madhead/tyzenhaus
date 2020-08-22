package me.madhead.tyzenhaus.repository.postgresql.groupconfig

import me.madhead.tyzenhaus.entity.groupconfig.GroupConfig
import java.sql.ResultSet
import java.util.Locale

fun ResultSet.toGroupConfig(): GroupConfig? {
    if (this.next()) {
        return GroupConfig(
                id = this.getLong(1),
                language = run {
                    val value = this.getString(2)

                    if (this.wasNull()) {
                        null
                    } else {
                        Locale(value)
                    }
                }
        )
    } else {
        return null
    }
}