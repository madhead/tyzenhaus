package me.madhead.tyzenhaus.repository.postgresql.transaction

import me.madhead.tyzenhaus.entity.transaction.Transaction
import java.sql.ResultSet
import java.util.Locale

internal fun ResultSet.toTransaction(): Transaction? {
    return if (this.next()) {
        Transaction(
            id = this.getLong("id"),
            groupId = this.getLong("group_id"),
            payer = this.getLong("payer"),
            recipients = run {
                val value = this.getArray("recipients")

                if (this.wasNull()) {
                    emptySet()
                } else {
                    (value.array as? Array<*>)?.filterIsInstance<Long>()?.toSet() ?: emptySet()
                }
            },
            amount = this.getBigDecimal("amount"),
            currency = this.getString("currency"),
            title = run {
                val value = this.getString("title")

                if (this.wasNull()) {
                    null
                } else {
                    value
                }
            },
            timestamp = this.getTimestamp("timestamp").toInstant(),
        )
    } else {
        null
    }
}
