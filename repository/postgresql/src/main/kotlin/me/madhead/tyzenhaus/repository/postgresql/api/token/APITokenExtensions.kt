package me.madhead.tyzenhaus.repository.postgresql.api.token

import java.sql.ResultSet
import java.util.UUID
import me.madhead.tyzenhaus.entity.api.token.APIToken
import me.madhead.tyzenhaus.entity.api.token.Scope

internal fun ResultSet.toAPIToken(): APIToken? {
    return if (this.next()) {
        APIToken(
            token = this.getObject("token", UUID::class.java),
            groupId = this.getLong("group_id"),
            scope = Scope.valueOf(this.getString("scope")),
            validUntil = this.getTimestamp("valid_until").toInstant(),
        )
    } else {
        null
    }
}
