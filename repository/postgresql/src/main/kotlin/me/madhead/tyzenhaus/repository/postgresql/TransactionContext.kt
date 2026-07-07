package me.madhead.tyzenhaus.repository.postgresql

import java.sql.Connection

/**
 * Holds the [Connection] of the transaction the current coroutine is running in, if any.
 */
@PublishedApi
internal object TransactionContext {
    val current: ThreadLocal<Connection?> = ThreadLocal()
}
