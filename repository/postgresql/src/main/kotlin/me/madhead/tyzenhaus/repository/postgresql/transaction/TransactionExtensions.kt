package me.madhead.tyzenhaus.repository.postgresql.transaction

import me.madhead.tyzenhaus.entity.transaction.Transaction
import java.sql.ResultSet

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
            timestamp = this.getTimestamp("timestamp").toInstant(),
        )
    } else {
        null
    }
}
