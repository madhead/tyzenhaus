package me.madhead.tyzenhaus.repository.postgresql.transaction

import java.sql.ResultSet
import me.madhead.tyzenhaus.entity.transaction.Transaction

internal fun ResultSet.toTransaction(): Transaction =
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

internal fun ResultSet.toTransactions(): List<Transaction> = buildList {
    while (this@toTransactions.next()) {
        this.add(this@toTransactions.toTransaction())
    }
}

internal fun ResultSet.toCurrencies(): List<String> {
    val result = mutableListOf<String>()

    while (this.next()) {
        result.add(this.getString("currency"))
    }

    return result
}
