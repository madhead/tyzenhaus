package me.madhead.tyzenhaus.repository.postgresql.balance

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.madhead.tyzenhaus.entity.balance.Balance
import java.sql.ResultSet

internal fun ResultSet.toBalance(json: Json): Balance? {
    return if (this.next()) {
        json.decodeFromString<Balance>(this.getString("balance")).copy(version = this.getLong("version"))
    } else {
        null
    }
}
