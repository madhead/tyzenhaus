package me.madhead.tyzenhaus.repository.postgresql.balance

import java.sql.ResultSet
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.madhead.tyzenhaus.entity.balance.Balance

internal fun ResultSet.toBalance(json: Json): Balance? {
    return if (this.next()) {
        json.decodeFromString<Balance>(this.getString("balance")).copy(
            groupId = this.getLong("group_id"),
            version = this.getLong("version")
        )
    } else {
        null
    }
}
