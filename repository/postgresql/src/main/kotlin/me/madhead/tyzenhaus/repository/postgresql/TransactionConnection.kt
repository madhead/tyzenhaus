package me.madhead.tyzenhaus.repository.postgresql

import java.sql.Connection
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * Carries the [Connection] of the transaction the current coroutine is running in.
 */
internal class TransactionConnection(
    val connection: Connection,
) : AbstractCoroutineContextElement(TransactionConnection) {
    companion object : CoroutineContext.Key<TransactionConnection>
}
