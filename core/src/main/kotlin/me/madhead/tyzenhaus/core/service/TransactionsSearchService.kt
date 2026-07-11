package me.madhead.tyzenhaus.core.service

import java.math.BigDecimal
import java.time.Instant
import me.madhead.tyzenhaus.entity.transaction.Transaction
import me.madhead.tyzenhaus.repository.TransactionRepository

/**
 * Default page size for [transaction search][TransactionsSearchService.search], used when the caller does not request one.
 */
const val DEFAULT_TRANSACTIONS_PAGE_SIZE = 30

/**
 * Stateless pagination cursor: the `(timestamp, id)` of the last-seen transaction under the `ORDER BY timestamp DESC, id DESC` order.
 *
 * The next page is everything "after" this row, i.e., the transactions whose `(timestamp, id)` compares less than the cursor's.
 */
data class Cursor(
    val timestamp: Instant,
    val id: Long,
)

/**
 * Transaction search parameters.
 *
 * All filter parameters are optional. If a filter is omitted, any transaction matches it.
 * Therefore, a default, empty [TransactionsSearchParams] matches every transaction of the group.
 *
 * A Group ID is not a search parameter; it is supplied based on a security token.
 *
 * @property title Case-insensitive substring the transaction title must contain.
 * @property participants "Involves member", any-of: the transaction matches if any of these members is the payer or a recipient.
 * @property amountFrom Inclusive lower bound of the currency-agnostic amount.
 * @property amountTo Inclusive upper bound of the currency-agnostic amount.
 * @property currencies The transaction currency must be one of these.
 * @property dateFrom Inclusive lower bound of the transaction timestamp.
 * @property dateTo Inclusive upper bound of the transaction timestamp.
 * @property cursor Pagination cursor; `null` for the first page.
 * @property limit Maximum number of transactions to return.
 */
data class TransactionsSearchParams(
    val title: String? = null,
    val participants: Set<Long> = emptySet(),
    val amountFrom: BigDecimal? = null,
    val amountTo: BigDecimal? = null,
    val currencies: Set<String> = emptySet(),
    val dateFrom: Instant? = null,
    val dateTo: Instant? = null,
    val cursor: Cursor? = null,
    val limit: Int = DEFAULT_TRANSACTIONS_PAGE_SIZE,
)

/**
 * Search for transactions.
 */
class TransactionsSearchService(
    private val transactionRepository: TransactionRepository,
) {
    /**
     * Lists transactions of the [group], matching the given [ignored].
     */
    suspend fun search(group: Long, ignored: TransactionsSearchParams): List<Transaction> {
        return transactionRepository.search(group)
    }
}
