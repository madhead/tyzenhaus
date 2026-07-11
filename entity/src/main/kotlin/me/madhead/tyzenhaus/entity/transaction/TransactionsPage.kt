package me.madhead.tyzenhaus.entity.transaction

/**
 * A single page of a transaction search.
 */
data class TransactionsPage(
    val transactions: List<Transaction>,
    val nextCursor: Cursor? = null,
)
