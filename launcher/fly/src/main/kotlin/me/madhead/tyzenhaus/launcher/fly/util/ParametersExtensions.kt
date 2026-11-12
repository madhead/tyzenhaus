package me.madhead.tyzenhaus.launcher.fly.util

import io.ktor.http.Parameters
import java.time.Instant
import me.madhead.tyzenhaus.entity.transaction.Cursor
import me.madhead.tyzenhaus.entity.transaction.DEFAULT_TRANSACTIONS_PAGE_SIZE
import me.madhead.tyzenhaus.entity.transaction.TransactionsSearchParams

/**
 * Parses Ktor's [Parameters] into a [TransactionsSearchParams].
 */
internal fun Parameters.toTransactionsSearchParams(): TransactionsSearchParams = TransactionsSearchParams(
    title = this["title"]?.takeIf { it.isNotBlank() },
    participants = getAll("participant").orEmpty().mapNotNull { it.toLongOrNull() }.toSet(),
    amountFrom = this["amountMin"]?.toBigDecimalOrNull(),
    amountTo = this["amountMax"]?.toBigDecimalOrNull(),
    currencies = getAll("currency").orEmpty().filter { it.isNotBlank() }.toSet(),
    dateFrom = this["dateFrom"]?.let { runCatching { Instant.parse(it) }.getOrNull() },
    dateTo = this["dateTo"]?.let { runCatching { Instant.parse(it) }.getOrNull() },
    cursor = this["cursor"]?.let { Cursor.decode(it) },
    limit = this["limit"]?.toIntOrNull()?.takeIf { it > 0 } ?: DEFAULT_TRANSACTIONS_PAGE_SIZE,
)
